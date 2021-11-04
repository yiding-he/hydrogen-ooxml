package com.hyd.ooxml.packaging;

import java.util.Objects;

public class IdPartPair {

    public String relationshipId;

    public OpenXmlPart openXmlPart;

    public IdPartPair(String relationshipId, OpenXmlPart openXmlPart) {
        this.relationshipId = relationshipId;
        this.openXmlPart = openXmlPart;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IdPartPair that = (IdPartPair) o;
        return relationshipId.equals(that.relationshipId) && openXmlPart == that.openXmlPart;
    }

    @Override
    public int hashCode() {
        return Objects.hash(relationshipId, openXmlPart);
    }
}
