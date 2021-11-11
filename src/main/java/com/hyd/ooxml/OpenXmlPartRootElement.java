package com.hyd.ooxml;

import com.hyd.ms.io.Stream;
import com.hyd.ooxml.packaging.OpenXmlPart;
import com.hyd.utilities.assertion.Assert;
import com.hyd.xml.Xml;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;

import java.nio.charset.StandardCharsets;

@Slf4j
public abstract class OpenXmlPartRootElement {

    public static class NONE extends OpenXmlPartRootElement {

    }

    protected OpenXmlPart openXmlPart;

    protected Document content;

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

    public Document getContent() {
        return content;
    }

    private void save(Stream stream) {
        writeTo(stream);
    }

    public void writeTo(Stream stream) {
        stream.writeBytes(Xml.toString(this.content, false).getBytes(StandardCharsets.UTF_8));
    }

    public OpenXmlPart getOpenXmlPart() {
        return openXmlPart;
    }

    public void setOpenXmlPart(OpenXmlPart openXmlPart) {
        this.openXmlPart = openXmlPart;
    }

    public void loadFromPart(OpenXmlPart openXmlPart, Stream stream) {
        // TODO implement com.hyd.ooxml.OpenXmlPartRootElement.loadFromPart()
        // It is way too much of a burden to write building process for every XML element by myself.
        // So this is it, and I will use DOM instead.
        try {
            log.debug("Parsing XML content of {}", openXmlPart.getUri());
            this.content = Xml.parseDocumentAndClose(stream.read());
        } catch (Exception e) {
            log.error("part contains invalid XML, uri={}, contentType={}", openXmlPart.getUri(), openXmlPart.getContentType());
            throw e;
        }
    }
}
