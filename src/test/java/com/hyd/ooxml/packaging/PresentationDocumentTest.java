package com.hyd.ooxml.packaging;

import com.hyd.ooxml.PresentationDocumentType;
import com.hyd.ooxml.generated.packaging.PresentationPart;
import com.hyd.ooxml.generated.presentation.Presentation;
import org.junit.jupiter.api.Test;

class PresentationDocumentTest {

    @Test
    void create() {
        PresentationDocument presentationDoc = PresentationDocument.create("target/1.pptx", PresentationDocumentType.Presentation);
        PresentationPart presentationPart = presentationDoc.addPresentationPart();
        presentationPart.setPresentation(new Presentation());

        createPresentationParts(presentationPart);

        presentationDoc.save();
        presentationDoc.close();
    }

    private void createPresentationParts(PresentationPart presentationPart) {
        // TODO implement com.hyd.ooxml.packaging.PresentationDocumentTest.createPresentationParts()
    }
}
