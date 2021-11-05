package com.hyd.ooxml;

import com.hyd.ms.io.Stream;
import com.hyd.ooxml.packaging.OpenXmlPart;

import java.nio.charset.StandardCharsets;

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
        return null;// TODO implement com.hyd.ooxml.OpenXmlPartRootElement.writeTo()


    }
}
