package com.hyd.ooxml;

import com.hyd.ms.io.Stream;
import com.hyd.ooxml.framework.metadata.OpenXmlAttribute;
import com.hyd.ooxml.packaging.OpenXmlPart;
import com.hyd.xml.Xml;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class OpenXmlPartRootElement extends OpenXmlCompositeElement {

    protected OpenXmlPart openXmlPart;

    public void save() {
        saveToPart(openXmlPart);
    }

    private void saveToPart(OpenXmlPart openXmlPart) {
        try (Stream stream = openXmlPart.getStream()) {
            save(stream);
        }
    }

    private void save(Stream stream) {
        String xml = writeTo();
        stream.writeBytes(xml.getBytes(StandardCharsets.UTF_8));
    }

    private String writeTo() {
        Document document = Xml.newDocument();
        Element root = this.toDomElement(document);
        document.appendChild(root);

        // gather all namespaces from descendants and apply them to root element
        Set<OpenXmlNamespace> namespaces = new HashSet<>();
        for (OpenXmlElement descendant : descendants()) {
            namespaces.addAll(Arrays.asList(descendant.getNamespaces()));
        }
        for (OpenXmlNamespace namespace : namespaces) {
            Xml.addNamespace(root, namespace.getPrefix(), namespace.getUri());
        }

        for (OpenXmlAttribute attribute : attributes()) {
            Xml.setAttr(root, attribute.getNameWithPrefix(), attribute.getValue());
        }

        buildChildElements(root);

        return Xml.toString(document);
    }

    private void buildChildElements(Element root) {
        // TODO implement com.hyd.ooxml.OpenXmlPartRootElement.buildChildElements()
    }
}
