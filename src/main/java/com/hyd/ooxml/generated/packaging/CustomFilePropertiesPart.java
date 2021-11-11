package com.hyd.ooxml.generated.packaging;

import com.hyd.ooxml.generated.BasicPartRootElement;
import com.hyd.ooxml.packaging.ContentType;
import com.hyd.ooxml.packaging.OpenXmlPart;
import com.hyd.ooxml.packaging.RelationshipType;
import com.hyd.ooxml.packaging.XmlPart;

@XmlPart(targetPath = "docProps", targetName = "custom", rootElementType = BasicPartRootElement.class)
@RelationshipType("http://schemas.openxmlformats.org/officeDocument/2006/relationships/custom-properties")
@ContentType("application/vnd.openxmlformats-officedocument.custom-properties+xml")
public class CustomFilePropertiesPart extends OpenXmlPart {

}
