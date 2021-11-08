package com.hyd.ooxml;

import com.hyd.ms.io.Stream;
import com.hyd.ooxml.framework.metadata.OpenXmlAttribute;
import com.hyd.ooxml.packaging.OpenXmlPart;
import com.hyd.utilities.assertion.Assert;
import com.hyd.xml.XmlBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public abstract class OpenXmlPartRootElement extends OpenXmlCompositeElement {

    protected OpenXmlPart openXmlPart;

    public void save() {
        Assert.notNull(openXmlPart, "openXmlPart@" + getClass().getSimpleName());
        log.debug("Saving root part {} to {}/{}.xml",
            this.getClass().getSimpleName(),
            this.openXmlPart.getTargetPath(), this.openXmlPart.getTargetName());
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
        Set<OpenXmlNamespace> namespaces = new HashSet<>(Arrays.asList(getNamespaces()));

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
        } else {
            log.debug("hasChildren: {}", hasChildren());
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
