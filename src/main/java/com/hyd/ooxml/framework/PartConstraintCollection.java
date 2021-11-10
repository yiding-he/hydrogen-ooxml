package com.hyd.ooxml.framework;

public class PartConstraintCollection {

    public boolean containsRelationship(String relationshipType) {
        // TODO we may need xml validation here
        return true;
    }

    public PartConstraintRule getConstrainRule(String relType) {
        // TODO we may need xml validation here
        return new PartConstraintRule(null, false, true);
    }
}
