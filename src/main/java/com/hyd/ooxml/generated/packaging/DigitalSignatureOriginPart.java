package com.hyd.ooxml.generated.packaging;

import com.hyd.ooxml.OpenXmlPartRootElement;
import com.hyd.ooxml.packaging.ContentType;
import com.hyd.ooxml.packaging.OpenXmlPart;
import com.hyd.ooxml.packaging.RelationshipType;
import com.hyd.ooxml.packaging.XmlPart;

@XmlPart(targetPath = "_xmlsignatures", targetName = "origin", targetExtension = "sigs", rootElementType = OpenXmlPartRootElement.NONE.class)
@RelationshipType("http://schemas.openxmlformats.org/package/2006/relationships/digital-signature/origin")
@ContentType("application/vnd.openxmlformats-package.digital-signature-origin")
public class DigitalSignatureOriginPart extends OpenXmlPart {

}
