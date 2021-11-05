package com.hyd.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Xml {

    private static final DocumentBuilder DOCUMENT_BUILDER;

    public static final XPath XPATH;

    static {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            docFactory.setNamespaceAware(true);
            DOCUMENT_BUILDER = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new XmlException(e);
        }

        XPATH = XPathFactory.newInstance().newXPath();
    }

    public static Document newDocument() {
        return DOCUMENT_BUILDER.newDocument();
    }

    public static Document parseString(String xml) {
        return parseDocumentAndClose(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
    }

    public static Document parseDocumentAndClose(InputStream inputStream) {
        try {
            try {
                return DOCUMENT_BUILDER.parse(inputStream);
            } finally {
                inputStream.close();
            }
        } catch (Exception e) {
            throw new XmlException(e);
        }
    }

    public static byte[] toBytes(Document document) {
        return toBytes(document, false);
    }

    public static byte[] toBytes(Document document, boolean indent) {
        try {
            Transformer tr = TransformerFactory.newInstance().newTransformer();
            tr.setOutputProperty(OutputKeys.INDENT, indent ? "yes" : "no");
            tr.setOutputProperty(OutputKeys.METHOD, "xml");
            tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            if (document.getXmlStandalone()) {
                tr.setOutputProperty(OutputKeys.STANDALONE, "yes");
            }

            // send DOM to file
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            tr.transform(new DOMSource(document), new StreamResult(bos));

            return bos.toByteArray();
        } catch (TransformerException e) {
            throw new XmlException(e);
        }
    }

    public static String toString(Document document) {
        return toString(document, false);
    }

    public static String toString(Document document, boolean indent) {
        return new String(toBytes(document, indent), StandardCharsets.UTF_8);
    }

    ///////////////////////////////////////////////////////////////////

    private static Object evaluate(Object item, String xpath) {
        try {
            return XPATH.compile(xpath).evaluate(item, XPathConstants.NODESET);
        } catch (Exception e) {
            throw new XmlException(e);
        }
    }

    public static List<Element> lookupElements(Document doc, String xpath) {
        return toElementList((NodeList) evaluate(doc, xpath));
    }

    public static List<Element> lookupElements(Element element, String xpath) {
        return toElementList((NodeList) evaluate(element, xpath));
    }

    public static List<Element> toElementList(NodeList nodeList) {
        List<Element> elementList = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node item = nodeList.item(i);
            if (item instanceof Element) {
                elementList.add((Element) item);
            }
        }
        return elementList;
    }

    public static Element firstChild(Element parent, String tagName) {
        String xpath = tagName + "[1]";
        List<Element> elements = lookupElements(parent, xpath);
        return elements.isEmpty() ? null : elements.get(0);
    }

    public static void addNamespace(Element element, String prefix, String namespaceUri) {
        element.setAttribute("xmlns:" + prefix, namespaceUri);
    }

    public static void setDefaultNamespace(Element element, String namespaceUri) {
        element.setAttribute("xmlns", namespaceUri);
    }

    public static void setAttr(Element e, String attrName, String attrValue) {
        if (attrValue != null) {
            e.setAttribute(attrName, attrValue);
        }
    }
}
