package com.hyd.ooxml.generated.packaging;

import com.hyd.ooxml.OpenXmlPartRootElement;
import com.hyd.ooxml.generated.presentation.Slide;
import com.hyd.ooxml.packaging.ContentType;
import com.hyd.ooxml.packaging.OpenXmlPart;
import com.hyd.ooxml.packaging.RelationshipType;
import com.hyd.ooxml.packaging.XmlPart;

@XmlPart(targetPath = "slides", targetName = "slide")
@RelationshipType("http://schemas.openxmlformats.org/officeDocument/2006/relationships/slide")
@ContentType("application/vnd.openxmlformats-officedocument.presentationml.slide+xml")
public class SlidePart extends OpenXmlPart {

    private Slide rootElement;

    public void setSlide(Slide slide) {
        setDomTree(slide);
    }

    @Override
    protected OpenXmlPartRootElement getInternalRootElement() {
        return rootElement;
    }

    @Override
    protected void setInternalRootElement(OpenXmlPartRootElement rootElement) {
        this.rootElement = (Slide) rootElement;
    }

    @Override
    protected OpenXmlPartRootElement getPartRootElement() {
        return rootElement;
    }
}
