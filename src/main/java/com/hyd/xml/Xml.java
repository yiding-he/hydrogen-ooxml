package com.hyd.xml;


import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class Xml {

    public static Document newDocument() {
        return DocumentHelper.createDocument();
    }

    public static Document parseString(String xml) {
        ByteArrayInputStream is = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
        return parseDocumentAndClose(is);
    }

    public static Document parseDocumentAndClose(InputStream inputStream) {
        try {
            try {
                return new SAXReader().read(inputStream);
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
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            XMLWriter xmlWriter;
            if (indent) {
                xmlWriter = new XMLWriter(new OutputStreamWriter(out), OutputFormat.createPrettyPrint());
            } else {
                xmlWriter = new XMLWriter(new OutputStreamWriter(out));
            }
            xmlWriter.write(document);
            return out.toByteArray();
        } catch (Exception e) {
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

    public static List<Element> lookupElements(Document doc, String xpath) {
        return toElementList(doc.selectNodes(xpath));
    }

    public static List<Element> lookupElements(Element element, String xpath) {
        return toElementList( element.selectNodes(xpath));
    }

    public static List<Element> toElementList(List<Node> nodeList) {
        return nodeList.stream().map(n -> (Element)n).collect(Collectors.toList());
    }

    public static Element firstChild(Element parent, String tagName) {
        String xpath = tagName + "[1]";
        List<Element> elements = lookupElements(parent, xpath);
        return elements.isEmpty() ? null : elements.get(0);
    }

    public static void addNamespace(Element element, String prefix, String namespaceUri) {
        element.addAttribute("xmlns:" + prefix, namespaceUri);
    }

    public static void setDefaultNamespace(Element element, String namespaceUri) {
        element.addAttribute("xmlns", namespaceUri);
    }

    public static void setAttr(Element e, String attrName, String attrValue) {
        if (attrValue != null) {
            e.addAttribute(attrName, attrValue);
        }
    }

    public static Element createElement(String namespace, String localName) {
        return DocumentHelper.createElement(namespace + ":" + localName);
    }

    public static Element createElement(String localName) {
        return DocumentHelper.createElement(localName);
    }
}
