package com.hyd.ms.io.packaging;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PackUriHelperTest {

    @Test
    void getSourcePartUriFromRelationshipPartUri() {
        PackUriHelper.ValidatedPartUri relPath = PackUriHelper.validatePartUri("/1/2/3/_rels/a.xml.rels");
        PackUriHelper.ValidatedPartUri sourcePath = PackUriHelper.getSourcePartUriFromRelationshipPartUri(relPath);
        assertEquals("/1/2/3/a.xml", sourcePath.getUri().toString());
    }
}
