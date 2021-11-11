package com.hyd.ooxml.generated.packaging;

import com.hyd.ooxml.OpenXmlPartRootElement;
import com.hyd.ooxml.generated.presentation.SlideMaster;
import com.hyd.ooxml.packaging.ContentType;
import com.hyd.ooxml.packaging.OpenXmlPart;
import com.hyd.ooxml.packaging.RelationshipType;
import com.hyd.ooxml.packaging.XmlPart;

@XmlPart(targetPath = "slideMasters", targetName = "slideMaster", rootElementType = SlideMaster.class)
@ContentType("application/vnd.openxmlformats-officedocument.presentationml.slideMaster+xml")
@RelationshipType("http://schemas.openxmlformats.org/officeDocument/2006/relationships/slideMaster")
public class SlideMasterPart extends OpenXmlPart {

    private SlideMaster rootElement;

    @Override
    protected OpenXmlPartRootElement getInternalRootElement() {
        return rootElement;
    }

    @Override
    protected void setInternalRootElement(OpenXmlPartRootElement rootElement) {
        this.rootElement = (SlideMaster) rootElement;
    }

    @Override
    protected OpenXmlPartRootElement getPartRootElement() {
        return rootElement;
    }

    public void setSlideMaster(SlideMaster slideMaster) {
        setDomTree(slideMaster);
    }
}
