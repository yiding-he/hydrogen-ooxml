package com.hyd.ooxml.packaging;

import com.hyd.ms.io.Stream;
import com.hyd.ms.io.packaging.PackagePart;

import java.net.URI;

public abstract class OpenXmlPart extends OpenXmlPartContainer {

    private OpenXmlPackage openXmlPackage;

    private PackagePart packagePart;

    private URI uri;

    //////////////////////////

    public OpenXmlPackage getOpenXmlPackage() {
        return openXmlPackage;
    }

    public URI getUri() {
        return uri;
    }

    public String getContentType() {
        return this.packagePart.getContentType();
    }

    @Override
    protected OpenXmlPackage getInternalOpenXmlPackage() {
        return openXmlPackage;
    }

    public Stream getStream() {
        return this.packagePart.getStream();
    }

    public abstract String getRelationshipType();

    public void createInternal2(OpenXmlPackage openXmlPackage, OpenXmlPart parent, String contentType, URI partUri) {
        // TODO implement com.hyd.ooxml.packaging.OpenXmlPart.createInternal2()
    }

    public void feedData(Stream stream) {
        // TODO implement com.hyd.ooxml.packaging.OpenXmlPart.feedData()
    }
}
