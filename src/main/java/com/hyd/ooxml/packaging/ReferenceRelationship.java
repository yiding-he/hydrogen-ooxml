package com.hyd.ooxml.packaging;

import lombok.Data;

import java.net.URI;

@Data
public abstract class ReferenceRelationship {

    private OpenXmlPartContainer container;

    private String relationshipType;

    private boolean external;

    private String id;

    private URI uri;

    public ReferenceRelationship() {
    }

    public ReferenceRelationship(URI uri, boolean external, String relationshipType, String id) {
        this.relationshipType = relationshipType;
        this.external = external;
        this.id = id;
        this.uri = uri;
    }
}
