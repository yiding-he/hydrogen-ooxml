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

}
