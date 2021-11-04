package com.hyd.ooxml.packaging;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DataPartReferenceRelationship extends ReferenceRelationship {

    public static DataPartReferenceRelationship create(
        OpenXmlPartContainer container, DataPart dataPart, String relationshipType, String id
    ) {
        // todo implement DataPartReferenceRelationship.create()
        return null;
    }

    private DataPart dataPart;
}
