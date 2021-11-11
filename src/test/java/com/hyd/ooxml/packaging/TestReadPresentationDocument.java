package com.hyd.ooxml.packaging;

import org.junit.jupiter.api.Test;

class TestReadPresentationDocument {

    // String path = "src/test/resources/simple-create-by-ms-office.pptx";
    public static final String PATH = "F:\\Projects\\ZnXunzhi\\ajia-base\\xz-ooxml\\samples\\multi-pages.pptx";

    @Test
    public void testListAllParts() {
        PresentationDocument presentationDocument = PresentationDocument.open(PATH, false);

        System.out.println("///////////////////////////////////////////////////////////////////");
        for (OpenXmlPart part : presentationDocument.getAllParts()) {
            System.out.println("PART: " + part.getUri());
        }
        System.out.println("///////////////////////////////////////////////////////////////////");
    }
}
