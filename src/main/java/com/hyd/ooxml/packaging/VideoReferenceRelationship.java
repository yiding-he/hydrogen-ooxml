package com.hyd.ooxml.packaging;

public class VideoReferenceRelationship extends DataPartReferenceRelationship {

    public static final String RELATIONSHIP_TYPE = "http://schemas.openxmlformats.org/officeDocument/2006/relationships/video";

    public VideoReferenceRelationship(MediaDataPart dataPart, String id) {
        super(dataPart, RELATIONSHIP_TYPE, id);
    }
}
