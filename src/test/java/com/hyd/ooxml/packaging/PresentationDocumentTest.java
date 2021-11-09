package com.hyd.ooxml.packaging;

import com.hyd.ooxml.E;
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
        theme.append(
            new E("a:themeElements").children(
                new E("a:clrScheme").children(
                    new E("a:dk1").children(createSysClr()),
                    new E("a:lt1").children(createSysClr()),
                    new E("a:dk2").children(createSysClr()),
                    new E("a:lt2").children(createSysClr()),
                    new E("a:accent1").children(createSysClr()),
                    new E("a:accent2").children(createSysClr()),
                    new E("a:accent3").children(createSysClr()),
                    new E("a:accent4").children(createSysClr()),
                    new E("a:accent5").children(createSysClr()),
                    new E("a:accent6").children(createSysClr()),
                    new E("a:hlink").children(createSysClr()),
                    new E("a:folHlink").children(createSysClr()),
                    new E("a:extLst").children(createSysClr())
                ),
                new E("a:fontScheme").children(
                    new E("a:majorFont").children(
                        new E("a:latin"),
                        new E("a:ea"),
                        new E("a:cs")
                    ),
                    new E("a:a:minorFont").children(
                        new E("a:latin"),
                        new E("a:ea"),
                        new E("a:cs")
                    )
                ),
                new E("a:fmtScheme").children(
                    new E("a:fillStyleLst").children(
                        new E("a:noFill"), new E("a:solidFill"), new E("a:grpFill")
                    ),
                    new E("a:lnStyleLst").children(
                        new E("a:ln").children(new E("a:noFill")),
                        new E("a:ln").children(new E("a:prstDash")),
                        new E("a:ln").children(new E("a:round"))
                    ),
                    new E("a:effectStyleLst").children(
                        new E("a:effectStyle").children(new E("a:effectLst").children(new E("a:blur"))),
                        new E("a:effectStyle").children(new E("a:effectLst").children(new E("a:blur"))),
                        new E("a:effectStyle").children(new E("a:effectLst").children(new E("a:blur")))
                    ),
                    new E("a:bgFillStyleLst").children(
                        new E("a:noFill"), new E("a:solidFill"), new E("a:grpFill")
                    )
                )
            )
        );
        themePart1.setTheme(theme);
        return themePart1;// TODO implement com.hyd.ooxml.packaging.PresentationDocumentTest.createTheme()
    }

    private E createSysClr() {
        return new E("a:sysClr").attr("val", "infoText").children(
            new E("a:red").attr("val", "100"),
            new E("a:green").attr("val", "100"),
            new E("a:blue").attr("val", "100")
        );
    }

    private SlideMasterPart createSlideMasterPart(SlideLayoutPart slideLayoutPart1) {
        SlideMasterPart slideMasterPart1 = slideLayoutPart1.addNewPart(SlideMasterPart.class, "rId1");
        SlideMaster slideMaster1 = new SlideMaster();
        slideMaster1.append(new E("p:cSld").children(
            new E("p:spTree").children(
                new E("p:nvGrpSpPr").children(
                    new E("p:cNvPr").attr("id", "1").attr("name", ""),
                    new E("p:cNvGrpSpPr"),
                    new E("p:nvPr")
                ),
                new E("p:grpSpPr").children(
                    new E("a:xfrm")
                ),
                new E("p:sp").children(
                    new E("p:nvSpPr").children(
                        new E("p:cNvPr").attr("id", "2").attr("name", "Title Placeholder 1"),
                        new E("p:cNvSpPr").children(
                            new E("a:spLocks").attr("noGrp", "1")
                        ),
                        new E("p:nvPr").children(
                            new E("p:ph").attr("type", "title")
                        )
                    ),
                    new E("p:spPr").children(),
                    new E("p:txBody").children(
                        new E("a:bodyPr"),
                        new E("a:lstStyle"),
                        new E("a:p")
                    )
                )
            )
        ));

        ColorMap colorMap1 = new ColorMap();
        colorMap1.setAttribute("bg1", "dk1");
        colorMap1.setAttribute("tx1", "dk1");
        colorMap1.setAttribute("bg2", "dk1");
        colorMap1.setAttribute("tx2", "dk1");
        colorMap1.setAttribute("accent1", "dk1");
        colorMap1.setAttribute("accent2", "dk1");
        colorMap1.setAttribute("accent3", "dk1");
        colorMap1.setAttribute("accent4", "dk1");
        colorMap1.setAttribute("accent5", "dk1");
        colorMap1.setAttribute("accent6", "dk1");
        colorMap1.setAttribute("hlink", "dk1");
        colorMap1.setAttribute("folHlink", "dk1");
        slideMaster1.append(colorMap1);

        slideMaster1.append(new E("p:sldLayoutIdLst").children(
            new E("p:sldLayoutId").attr("id", "2147483649").attr("r:id", "rId1")
        ));

        slideMaster1.append(new E("p:txStyles").children(
            new E("p:titleStyle"),
            new E("p:bodyStyle"),
            new E("p:otherStyle")
        ));

        slideMasterPart1.setSlideMaster(slideMaster1);
        return slideMasterPart1;
    }

    private SlideLayoutPart createSlideLayoutPart(SlidePart slidePart1) {
        SlideLayoutPart slideLayoutPart1 = slidePart1.addNewPart(SlideLayoutPart.class, "rId1");
        SlideLayout slideLayout = new SlideLayout();
        slideLayout.append(new E("p:cSld").children(
            new E("p:spTree").children(
                new E("p:nvGrpSpPr").children(
                    new E("p:cNvPr").attr("id", "1").attr("name", ""),
                    new E("p:cNvGrpSpPr"),
                    new E("p:nvPr")
                ),
                new E("p:grpSpPr").children(
                    new E("a:xfrm")
                ),
                new E("p:sp").children(
                    new E("p:nvSpPr").children(
                        new E("p:cNvPr").attr("id", "2").attr("name", "Title Placeholder 1"),
                        new E("p:cNvSpPr").children(
                            new E("a:spLocks").attr("noGrp", "1")
                        ),
                        new E("p:nvPr").children(
                            new E("p:ph")
                        )
                    ),
                    new E("p:spPr").children(),
                    new E("p:txBody").children(
                        new E("a:bodyPr"),
                        new E("a:lstStyle"),
                        new E("a:p").children(
                            new E("a:endParaRPr")
                        )
                    )
                )
            )
        ));
        slideLayout.append(new E("p:clrMapOvr").children(
            new E("a:masterClrMapping")
        ));
        slideLayoutPart1.setSlideLayout(slideLayout);
        return slideLayoutPart1;
    }

    private SlidePart createSlidePart(PresentationPart presentationPart) {
        SlidePart slidePart1 = presentationPart.addNewPart(SlidePart.class, "rId2");
        Slide slide1 = new Slide();
        slide1.append(new E("p:cSld").children(
            new E("p:spTree").children(
                new E("p:nvGrpSpPr").children(
                    new E("p:cNvPr").attr("id", "1").attr("name", ""),
                    new E("p:cNvGrpSpPr"),
                    new E("p:nvPr")
                ),
                new E("p:grpSpPr").children(
                    new E("a:xfrm").children(
                        new E("a:off").attr("x", "0").attr("y", "0"),
                        new E("a:ext").attr("cx", "0").attr("cy", "0"),
                        new E("a:chOff").attr("x", "0").attr("y", "0"),
                        new E("a:chExt").attr("cx", "0").attr("cy", "0")
                    )
                ),
                new E("p:sp").children(
                    new E("p:nvSpPr").children(
                        new E("p:cNvPr").attr("id", "2").attr("name", "Title Placeholder 1"),
                        new E("p:cNvSpPr").children(
                            new E("a:spLocks").attr("noGrp", "1")
                        ),
                        new E("p:nvPr").children(
                            new E("p:ph").attr("type", "title")
                        )
                    ),
                    new E("p:spPr").children(),
                    new E("p:txBody").children(
                        new E("a:bodyPr"),
                        new E("a:lstStyle"),
                        new E("a:p")
                    )
                )
            )
        ));
        slide1.append(new E("p:clrMapOvr").children(
            new E("a:masterClrMapping")
        ));
        slidePart1.setSlide(slide1);
        return slidePart1;
    }

}
