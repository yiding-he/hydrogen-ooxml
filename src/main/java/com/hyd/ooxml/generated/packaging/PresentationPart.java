package com.hyd.ooxml.generated.packaging;

import com.hyd.ms.io.packaging.PackageRelationship;
import com.hyd.ms.io.packaging.TargetMode;
import com.hyd.ooxml.OpenXmlPartRootElement;
import com.hyd.ooxml.generated.presentation.Presentation;
import com.hyd.ooxml.packaging.OpenXmlPart;

import java.net.URI;

public class PresentationPart extends OpenXmlPart {

    public static final String RELATIONSHIP_TYPE_CONSTANT =
        "http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument";

    private Presentation rootElement;

    @Override
    public Presentation getInternalRootElement() {
        return rootElement;
    }

    @Override
    protected void setInternalRootElement(OpenXmlPartRootElement rootElement) {
        this.rootElement = (Presentation) rootElement;
    }

    @Override
    protected OpenXmlPartRootElement getPartRootElement() {
        return getPresentation();
    }

    @Override
    public String getRelationshipType() {
        return RELATIONSHIP_TYPE_CONSTANT;
    }

    @Override
    protected PackageRelationship createRelationship(
        URI targetUri, TargetMode targetMode, String relationshipType) {
        throw new UnsupportedOperationException("Not implemented yet");
        // TODO implement com.hyd.ooxml.generated.packaging.PresentationPart.createRelationship()
    }

    @Override
    protected PackageRelationship createRelationship(
        URI targetUri, TargetMode targetMode, String relationshipType, String id) {
        throw new UnsupportedOperationException("Not implemented yet");
        // TODO implement com.hyd.ooxml.generated.packaging.PresentationPart.createRelationship()
    }

    @Override
    public String getTargetPath() {
        return "ppt";
    }

    @Override
    public String getTargetName() {
        return "presentation";
    }

    public Presentation getPresentation() {
        return this.rootElement;
    }

    public void setPresentation(Presentation presentation) {
        setDomTree(presentation);
    }
}
