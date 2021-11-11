package com.hyd.ooxml.generated.packaging;

import com.hyd.ooxml.OpenXmlPartRootElement;
import com.hyd.ooxml.generated.presentation.Presentation;
import com.hyd.ooxml.packaging.OpenXmlPart;
import com.hyd.ooxml.packaging.RelationshipType;
import com.hyd.ooxml.packaging.XmlPart;

@XmlPart(targetPath = "ppt", targetName = "presentation", rootElementType = Presentation.class)
@RelationshipType("http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument")
public class PresentationPart extends OpenXmlPart {

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

    public Presentation getPresentation() {
        return this.rootElement;
    }

    public void setPresentation(Presentation presentation) {
        setDomTree(presentation);
    }
}
