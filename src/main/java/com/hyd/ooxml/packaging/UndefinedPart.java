package com.hyd.ooxml.packaging;

import com.hyd.ooxml.OpenXmlPartRootElement;
import com.hyd.utilities.Uris;

import java.net.URI;
import java.util.Map;

public class UndefinedPart extends OpenXmlPart {

    private final String relationshipType;

    private String targetPath;

    private String targetName;

    private String targetFileExtension;

    public UndefinedPart(String relationshipType) {
        this.relationshipType = relationshipType;
    }

    @Override
    public void load(
        OpenXmlPackage openXmlPackage, OpenXmlPart parent, URI targetUri, String id, Map<URI, OpenXmlPart> loadedParts
    ) {
        super.load(openXmlPackage, parent, targetUri, id, loadedParts);
        String uriString = targetUri.toString();
        this.targetPath = Uris.getParent(uriString);
        this.targetName = Uris.getBaseName(uriString);
        this.targetFileExtension = Uris.getExtension(uriString);
    }

    @Override
    protected <T extends OpenXmlPartRootElement> void loadDomTree(Class<T> type) {

    }

    @Override
    public String getTargetPath() {
        return targetPath;
    }

    @Override
    public String getTargetName() {
        return targetName;
    }

    @Override
    public String getTargetFileExtension() {
        return targetFileExtension;
    }

    @Override
    public String getRelationshipType() {
        return relationshipType;
    }

    @Override
    public Class<? extends OpenXmlPartRootElement> getRootElementType() {
        return OpenXmlPartRootElement.NONE.class;
    }
}
