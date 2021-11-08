package com.hyd.ooxml.packaging;

import com.hyd.ooxml.OpenXmlNamespace;
import com.hyd.ooxml.PresentationDocumentType;
import com.hyd.ooxml.generated.packaging.PresentationPart;
import com.hyd.ooxml.generated.presentation.*;
import org.junit.jupiter.api.Test;

class PresentationDocumentTest {

    @Test
    void create() {
        PresentationDocument presentationDoc = PresentationDocument.create("target/1.pptx", PresentationDocumentType.Presentation);
        PresentationPart presentationPart = presentationDoc.addPresentationPart();
        presentationPart.setPresentation(new Presentation());

        createPresentationParts(presentationPart);

        presentationDoc.close();
    }

    private void createPresentationParts(PresentationPart presentationPart) {

        SlideMasterId slideMasterId = new SlideMasterId();
        slideMasterId.setAttribute("id", "2147483648");
        slideMasterId.setAttribute(OpenXmlNamespace.Relationship, "id", "rId1");
        SlideMasterIdList slideMasterIdList1 = new SlideMasterIdList(slideMasterId);

        SlideId slideId = new SlideId();
        slideId.setAttribute("id", "256");
        slideId.setAttribute(OpenXmlNamespace.Relationship, "id", "rId2");
        SlideIdList slideIdList1 = new SlideIdList(slideId);

        SlideSize slideSize1 = new SlideSize();
        slideSize1.setAttribute("cx", "9144000");
        slideSize1.setAttribute("cy", "6858000");
        slideSize1.setAttribute("type", SlideSize.Types.Screen_4_3);

        NotesSize notesSize1 = new NotesSize();
        notesSize1.setAttribute("cx", "6858000");
        notesSize1.setAttribute("cy", "9144000");

        DefaultTextStyle defaultTextStyle1 = new DefaultTextStyle();

        presentationPart.getPresentation().append(
            slideMasterIdList1, slideIdList1, slideSize1, notesSize1, defaultTextStyle1
        );


    }
}
