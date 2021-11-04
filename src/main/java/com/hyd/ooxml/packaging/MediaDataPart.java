package com.hyd.ooxml.packaging;

import java.net.URI;

public class MediaDataPart extends DataPart {

    public MediaDataPart(OpenXmlPackage openXmlPackage, String contentType, URI uri) {
        super(openXmlPackage, contentType, uri);
    }
}
