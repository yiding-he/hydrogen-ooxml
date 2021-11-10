package com.hyd.ooxml.packaging;

import org.junit.jupiter.api.Test;

class TestReadPresentationDocument {

    @Test
    public void testReadPptx() throws Exception {
        PresentationDocument presentationDocument = PresentationDocument.open(
            "src/test/resources/simple-create-by-ms-office.pptx", false
        );
    }
}
