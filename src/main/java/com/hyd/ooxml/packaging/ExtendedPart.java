package com.hyd.ooxml.packaging;

import com.hyd.ooxml.OpenXmlPartRootElement;
import com.hyd.ooxml.generated.ExtendedPartRootElement;

@XmlPart(targetPath = "udata", targetName = "data", targetExtension = "dat", rootElementType = ExtendedPartRootElement.class)
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
    protected OpenXmlPart addPartFrom(OpenXmlPart subPart, String rId) {
        return addPartFrom(subPart, rId, false);
    }

    @Override
    protected <T extends OpenXmlPartRootElement> void loadDomTree(Class<T> type) {
        // Content could not be xml format
    }
}
