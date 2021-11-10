package com.hyd.ooxml.packaging;

import com.hyd.ms.io.MemoryStream;
import com.hyd.ooxml.OpenXmlElement;
import com.hyd.ooxml.generated.packaging.PresentationPart;
import com.hyd.ooxml.generated.presentation.Presentation;
import com.hyd.xml.XmlBuilder;
import org.junit.jupiter.api.Test;

class TestReadPresentationDocument {

    @Test
    public void testReadPptx() throws Exception {
        PresentationDocument presentationDocument = PresentationDocument.open(
            "src/test/resources/simple-create-by-ms-office.pptx", false
        );

        PresentationPart presentationPart = (PresentationPart) presentationDocument.getRootPart();
        Presentation presentation = presentationPart.getPresentation();

        for (OpenXmlElement element : presentation.getChildElements()) {
            MemoryStream stream = new MemoryStream();
            XmlBuilder xmlBuilder = new XmlBuilder(stream);
            element.writeTo(xmlBuilder);
            xmlBuilder.finish();
            System.out.println(stream);
        }
    }
}
