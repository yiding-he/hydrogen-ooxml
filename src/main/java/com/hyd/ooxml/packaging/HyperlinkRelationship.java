package com.hyd.ooxml.packaging;

import java.net.URI;

public class HyperlinkRelationship extends ReferenceRelationship {

    public static final String RELATIONSHIP_TYPE_CONST = "http://schemas.openxmlformats.org/officeDocument/2006/relationships/hyperlink";

    public HyperlinkRelationship(URI uri, boolean external, String id) {
        super(uri, external, RELATIONSHIP_TYPE_CONST, id);
    }
}
