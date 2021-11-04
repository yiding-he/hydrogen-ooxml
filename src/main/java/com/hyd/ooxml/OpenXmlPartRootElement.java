package com.hyd.ooxml;

import com.hyd.ms.io.Stream;
import com.hyd.ooxml.packaging.OpenXmlPart;

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
        // TODO implement com.hyd.ooxml.OpenXmlPartRootElement.save()
    }
}
