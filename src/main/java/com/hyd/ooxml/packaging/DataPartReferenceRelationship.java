package com.hyd.ooxml.packaging;

import com.hyd.utilities.assertion.Assert;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DataPartReferenceRelationship extends ReferenceRelationship {

    public static DataPartReferenceRelationship create(
        OpenXmlPartContainer container, DataPart dataPart, String relationshipType, String id
    ) {
        DataPartReferenceRelationship dataPartReferenceRelationship;
        switch (relationshipType) {
            case MediaReferenceRelationship.RELATIONSHIP_TYPE:
                dataPartReferenceRelationship = new MediaReferenceRelationship((MediaDataPart)dataPart, id);
                break;
            case AudioReferenceRelationship.RELATIONSHIP_TYPE:
                dataPartReferenceRelationship = new AudioReferenceRelationship((MediaDataPart)dataPart, id);
                break;
            case VideoReferenceRelationship.RELATIONSHIP_TYPE:
                dataPartReferenceRelationship = new VideoReferenceRelationship((MediaDataPart)dataPart, id);
                break;
            default:
                throw new IllegalArgumentException("relationshipType is out of range");
        }
        dataPartReferenceRelationship.setContainer(container);
        return dataPartReferenceRelationship;
    }

    protected DataPartReferenceRelationship(DataPart dataPart, String relationshipType, String id) {
        super(dataPart.getUri(), false, relationshipType, id);
        Assert.notNull(dataPart, "dataPart");
        this.dataPart = dataPart;
    }

    private DataPart dataPart;
}
