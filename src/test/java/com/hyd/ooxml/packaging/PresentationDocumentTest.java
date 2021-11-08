package com.hyd.ooxml.packaging;

import com.hyd.ooxml.OpenXmlNamespace;
import com.hyd.ooxml.PresentationDocumentType;
import com.hyd.ooxml.generated.drawing.Theme;
import com.hyd.ooxml.generated.packaging.*;
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

        SlidePart slidePart1 = createSlidePart(presentationPart);
        SlideLayoutPart slideLayoutPart1 = createSlideLayoutPart(slidePart1);
        SlideMasterPart slideMasterPart1 = createSlideMasterPart(slideLayoutPart1);
        ThemePart themePart1 = createTheme(slideMasterPart1);

        slideMasterPart1.addPart(slideLayoutPart1, "rId1");
        presentationPart.addPart(slideMasterPart1, "rId1");
        presentationPart.addPart(themePart1, "rId5");
    }

    private ThemePart createTheme(SlideMasterPart slideMasterPart1) {
        ThemePart themePart1 = slideMasterPart1.addNewPart(ThemePart.class, "rId5");
        Theme theme = new Theme();
        themePart1.setTheme(theme);
        return themePart1;// TODO implement com.hyd.ooxml.packaging.PresentationDocumentTest.createTheme()
    }

    private SlideMasterPart createSlideMasterPart(SlideLayoutPart slideLayoutPart1) {
        SlideMasterPart slideMasterPart1 = slideLayoutPart1.addNewPart(SlideMasterPart.class, "rId1");
        SlideMaster slideMaster = new SlideMaster();
        slideMasterPart1.setSlideMaster(slideMaster);
        return slideMasterPart1;// TODO implement com.hyd.ooxml.packaging.PresentationDocumentTest.createSlideMasterPart()
    }

    private SlideLayoutPart createSlideLayoutPart(SlidePart slidePart1) {
        SlideLayoutPart slideLayoutPart1 = slidePart1.addNewPart(SlideLayoutPart.class, "rId1");
        SlideLayout slideLayout = new SlideLayout();
        slideLayoutPart1.setSlideLayout(slideLayout);
        return slideLayoutPart1;// TODO implement com.hyd.ooxml.packaging.PresentationDocumentTest.createSlideLayoutPart()
    }

    private SlidePart createSlidePart(PresentationPart presentationPart) {
        SlidePart slidePart1 = presentationPart.addNewPart(SlidePart.class, "rId2");
        Slide slide = new Slide();
        slidePart1.setSlide(slide);
        return slidePart1;// TODO implement com.hyd.ooxml.packaging.PresentationDocumentTest.createSlidePart()
    }
}
