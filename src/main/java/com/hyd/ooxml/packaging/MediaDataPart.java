package com.hyd.ooxml.packaging;

import com.hyd.ms.io.packaging.PackagePart;

import java.net.URI;

public class MediaDataPart extends DataPart {

    public MediaDataPart(OpenXmlPackage openXmlPackage, String contentType, URI uri) {
        super(openXmlPackage, contentType, uri);
    }

    public MediaDataPart(PackagePart metroPart, URI uri) {
        super(metroPart, uri);
    }

    public MediaDataPart(OpenXmlPackage openXmlPackage, PackagePart packagePart) {
        super(openXmlPackage, packagePart);
    }
}
