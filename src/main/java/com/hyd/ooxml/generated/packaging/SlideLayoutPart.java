package com.hyd.ooxml.generated.packaging;

import com.hyd.ooxml.OpenXmlPartRootElement;
import com.hyd.ooxml.generated.presentation.SlideLayout;
import com.hyd.ooxml.packaging.ContentType;
import com.hyd.ooxml.packaging.OpenXmlPart;
import com.hyd.ooxml.packaging.RelationshipType;
import com.hyd.ooxml.packaging.XmlPart;

@XmlPart(targetPath = "../slideLayouts", targetName = "slideLayout")
@RelationshipType("http://schemas.openxmlformats.org/officeDocument/2006/relationships/slideLayout")
@ContentType("application/vnd.openxmlformats-officedocument.presentationml.slideLayout+xml")
public class SlideLayoutPart extends OpenXmlPart {

    private SlideLayout rootElement;

    @Override
    protected void setInternalRootElement(OpenXmlPartRootElement rootElement) {
        this.rootElement = (SlideLayout) rootElement;
    }

    @Override
    protected OpenXmlPartRootElement getInternalRootElement() {
        return rootElement;
    }

    @Override
    protected OpenXmlPartRootElement getPartRootElement() {
        return rootElement;
    }

    public void setSlideLayout(SlideLayout slideLayout) {
        setDomTree(slideLayout);
    }
}
