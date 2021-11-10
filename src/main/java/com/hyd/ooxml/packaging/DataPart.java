package com.hyd.ooxml.packaging;

import com.hyd.ms.io.Stream;
import com.hyd.ms.io.packaging.PackagePart;
import com.hyd.utilities.assertion.Assert;
import lombok.Getter;

import java.net.URI;

@Getter
public class DataPart {

    private OpenXmlPackage openXmlPackage;

    private final PackagePart metroPart;

    private final URI uri;

    public DataPart(PackagePart metroPart, URI uri) {
        this.metroPart = metroPart;
        this.uri = uri;
    }

    public DataPart(OpenXmlPackage openXmlPackage, String contentType, URI partUri) {
        this.openXmlPackage = openXmlPackage;
        this.uri = openXmlPackage.getUniquePartUri(contentType, URI.create("/"), partUri);
        this.metroPart = openXmlPackage.createMetroPart(this.uri, contentType);
    }

    public DataPart(OpenXmlPackage openXmlPackage, PackagePart packagePart) {
        Assert.that(openXmlPackage.__package.getPart(packagePart.getUri().getUri()) == packagePart,
            "argument packagePart should belong to argument openXmlPackage");

        this.openXmlPackage = openXmlPackage;
        this.metroPart = packagePart;
        this.uri = packagePart.getUri().getUri();

        Assert.that(this.metroPart.getRelationships().isEmpty(),
            "Media (Audio, Video) parts should not reference any other parts");
    }

    public String getContentType() {
        return this.metroPart.getContentType();
    }

    public Stream getStream() {
        return this.metroPart.getStream();
    }

    public void feedData(Stream sourceStream) {
        sourceStream.copyTo(getStream());
    }

    public void destroy() {
        openXmlPackage.__package.deletePart(uri);
        openXmlPackage = null;
    }
}
