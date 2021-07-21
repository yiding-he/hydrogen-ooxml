package com.hyd.ooxml.packaging;

import com.hyd.ms.io.packaging.Package;

public abstract class OpenXmlPackage extends OpenXmlPartContainer {

    private Package _package;

    private String mainPartContentType;

    private OpenSettings openSettings;

    protected OpenXmlPackage() {
        _package = null;
        mainPartContentType = null;
        openSettings = null;
    }

    protected OpenXmlPackage(PackageLoader loader, OpenSettings settings) {
        openSettings = new OpenSettings(settings);
    }
}
