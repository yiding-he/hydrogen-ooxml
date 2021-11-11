package com.hyd.ooxml.packaging;

import com.hyd.ms.io.MemoryStream;
import com.hyd.ooxml.generated.packaging.PresentationPart;
import com.hyd.ooxml.generated.presentation.Presentation;
import org.junit.jupiter.api.Test;

class TestReadPresentationDocument {

    @Test
    public void testReadPptx() throws Exception {
        PresentationDocument presentationDocument = PresentationDocument.open(
            "src/test/resources/simple-create-by-ms-office.pptx", false
        );



        PresentationPart presentationPart = (PresentationPart) presentationDocument.getRootPart();
        Presentation presentation = presentationPart.getPresentation();

        MemoryStream stream = new MemoryStream();
        presentation.writeTo(stream);
        System.out.println(stream);
    }
}
