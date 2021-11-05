package com.hyd.ooxml.generated.packaging;

import com.hyd.ms.io.packaging.PackageRelationship;
import com.hyd.ms.io.packaging.TargetMode;
import com.hyd.ooxml.packaging.OpenXmlPart;

import java.net.URI;

public class PresentationPart extends OpenXmlPart {

    public static final String RELATIONSHIP_TYPE_CONSTANT =
        "http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument";

    @Override
    public String getRelationshipType() {
        return null;// TODO implement com.hyd.ooxml.generated.packaging.PresentationPart.getRelationshipType()
    }

    @Override
    protected OpenXmlPart getThisOpenXmlPart() {
        return null;// TODO implement com.hyd.ooxml.generated.packaging.PresentationPart.getThisOpenXmlPart()
    }

    @Override
    protected PackageRelationship createRelationship(
        URI targetUri, TargetMode targetMode, String relationshipType) {
        return null;// TODO implement com.hyd.ooxml.generated.packaging.PresentationPart.createRelationship()
    }

    @Override
    protected PackageRelationship createRelationship(
        URI targetUri, TargetMode targetMode, String relationshipType, String id) {
        return null;// TODO implement com.hyd.ooxml.generated.packaging.PresentationPart.createRelationship()
    }
}
