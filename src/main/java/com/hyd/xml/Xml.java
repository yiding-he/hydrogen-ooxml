package com.hyd.xml;


import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

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

    public static Element createElement(String namespace, String localName) {
        return DocumentHelper.createElement(namespace + ":" + localName);
    }

    public static Element createElement(String localName) {
        return DocumentHelper.createElement(localName);
    }
}
