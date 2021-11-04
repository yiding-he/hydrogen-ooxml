package com.hyd.ooxml.packaging;

public class AudioReferenceRelationship extends DataPartReferenceRelationship {

    public static final String RELATIONSHIP_TYPE = "http://schemas.openxmlformats.org/officeDocument/2006/relationships/audio";

    public AudioReferenceRelationship(MediaDataPart dataPart, String id) {
        super(dataPart, RELATIONSHIP_TYPE, id);
    }
}
