package com.hyd.ooxml.packaging;

public class MediaReferenceRelationship extends DataPartReferenceRelationship {

    public static final String RELATIONSHIP_TYPE = "http://schemas.microsoft.com/office/2007/relationships/media";

    public MediaReferenceRelationship(MediaDataPart dataPart, String id) {
        super(dataPart, RELATIONSHIP_TYPE, id);
    }
}
