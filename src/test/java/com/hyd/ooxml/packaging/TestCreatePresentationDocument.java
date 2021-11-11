package com.hyd.ooxml.packaging;

import com.hyd.ooxml.PresentationDocumentType;
import com.hyd.ooxml.generated.packaging.PresentationPart;
import org.junit.jupiter.api.Test;

class TestCreatePresentationDocument {

    @Test
    void create() {
        PresentationDocument presentationDoc = PresentationDocument.create("target/1.pptx", PresentationDocumentType.Presentation);
        PresentationPart presentationPart = presentationDoc.addPresentationPart();


        presentationDoc.close();
    }
}
