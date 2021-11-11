package com.hyd.ooxml.generated.packaging;

import com.hyd.ooxml.generated.BasicPartRootElement;
import com.hyd.ooxml.packaging.ContentType;
import com.hyd.ooxml.packaging.OpenXmlPart;
import com.hyd.ooxml.packaging.RelationshipType;
import com.hyd.ooxml.packaging.XmlPart;

@XmlPart(targetPath = "docProps", targetName = "app", rootElementType = BasicPartRootElement.class)
@RelationshipType("http://schemas.openxmlformats.org/officeDocument/2006/relationships/extended-properties")
@ContentType("application/vnd.openxmlformats-officedocument.extended-properties+xml")
public class ExtendedFilePropertiesPart extends OpenXmlPart {

}
