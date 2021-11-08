package com.hyd.ooxml;

import com.hyd.ms.io.Stream;
import com.hyd.ooxml.framework.metadata.OpenXmlAttribute;
import com.hyd.ooxml.packaging.OpenXmlPart;
import com.hyd.xml.XmlBuilder;

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
        writeTo(stream);
    }

    private void writeTo(Stream stream) {

        XmlBuilder builder = new XmlBuilder(stream);
        XmlBuilder.XmlBuilderElement root = builder.createRoot(getXmlTagName());

        // gather all namespaces from descendants and apply them to root element
        Set<OpenXmlNamespace> namespaces = new HashSet<>();
        for (OpenXmlElement descendant : descendants()) {
            namespaces.addAll(Arrays.asList(descendant.getNamespaces()));
        }
        for (OpenXmlNamespace namespace : namespaces) {
            root.addNamespace(namespace);
        }

        for (OpenXmlAttribute attribute : attributes()) {
            root.addAttribute(attribute);
        }

        if (hasChildren() || !getInnerText().isEmpty()) {
            builder.setCurrentElement(root);
            writeContentTo(builder);
        }

        builder.finish();
    }

    public OpenXmlPart getOpenXmlPart() {
        return openXmlPart;
    }

    public void setOpenXmlPart(OpenXmlPart openXmlPart) {
        this.openXmlPart = openXmlPart;
    }
}
