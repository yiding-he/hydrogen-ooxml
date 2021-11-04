package com.hyd.ooxml.packaging;

import com.hyd.ms.io.Stream;
import com.hyd.ms.io.packaging.PackagePart;
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

    public String getContentType() {
        return this.metroPart.getContentType();
    }

    public Stream getStream() {
        return this.metroPart.getStream();
    }

    public void feedData(Stream sourceStream) {
        sourceStream.copyTo(getStream());
    }
}
