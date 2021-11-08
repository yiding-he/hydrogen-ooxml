package com.hyd.ooxml.generated.packaging;

import com.hyd.ooxml.OpenXmlPartRootElement;
import com.hyd.ooxml.generated.drawing.Theme;
import com.hyd.ooxml.packaging.ContentType;
import com.hyd.ooxml.packaging.OpenXmlPart;
import com.hyd.ooxml.packaging.RelationshipType;
import com.hyd.ooxml.packaging.XmlPart;

@XmlPart(targetPath = "theme", targetName = "theme")
@RelationshipType("http://schemas.openxmlformats.org/officeDocument/2006/relationships/theme")
@ContentType("application/vnd.openxmlformats-officedocument.theme+xml")
public class ThemePart extends OpenXmlPart {

    private Theme rootElement;

    @Override
    protected OpenXmlPartRootElement getInternalRootElement() {
        return rootElement;
    }

    @Override
    protected void setInternalRootElement(OpenXmlPartRootElement rootElement) {
        this.rootElement = (Theme) rootElement;
    }

    @Override
    protected OpenXmlPartRootElement getPartRootElement() {
        return rootElement;
    }

    public void setTheme(Theme theme) {
        setDomTree(theme);
    }
}
