package com.hyd.ooxml.packaging;

import java.net.URI;

public class ExternalRelationship extends ReferenceRelationship {

    public ExternalRelationship(URI uri, String relationshipType, String id) {
        super(uri, true, relationshipType, id);
    }
}
