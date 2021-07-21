package com.hyd.xml;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;

public class Xml {

    private static final DocumentBuilder DOCUMENT_BUILDER;

    static {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DOCUMENT_BUILDER = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new XmlException(e);
        }
    }

    public static Document newDocument() {
        return DOCUMENT_BUILDER.newDocument();
    }

    public static byte[] toBytes(Document document) {
        try {
            Transformer tr = TransformerFactory.newInstance().newTransformer();
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
}
