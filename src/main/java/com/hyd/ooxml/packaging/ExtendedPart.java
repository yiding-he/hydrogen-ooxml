package com.hyd.ooxml.packaging;

public class ExtendedPart extends OpenXmlPart {

    private final String relationshipType;

    public ExtendedPart(String relationshipType) {
        this.relationshipType = relationshipType;
    }

    @Override
    public String getRelationshipType() {
        return relationshipType;
    }

    @Override
    protected String getTargetFileExtension() {
        return ".dat";
    }

    @Override
    public String getTargetPath() {
        return "udata";
    }

    @Override
    public String getTargetName() {
        return "data";
    }

    @Override
    protected OpenXmlPart addPartFrom(OpenXmlPart subPart, String rId) {
        return addPartFrom(subPart, rId, false);
    }
}
