package com.hyd.ooxml.generated.packaging;

import com.hyd.ooxml.generated.BasicPartRootElement;
import com.hyd.ooxml.packaging.ContentType;
import com.hyd.ooxml.packaging.OpenXmlPart;
import com.hyd.ooxml.packaging.RelationshipType;
import com.hyd.ooxml.packaging.XmlPart;

@XmlPart(targetPath = "docProps", targetName = "core", rootElementType = BasicPartRootElement.class)
@RelationshipType("http://schemas.openxmlformats.org/package/2006/relationships/metadata/core-properties")
@ContentType("application/vnd.openxmlformats-package.core-properties+xml")
public class CoreFilePropertiesPart extends OpenXmlPart {

}
