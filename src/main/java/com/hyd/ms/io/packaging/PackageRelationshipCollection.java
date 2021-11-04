package com.hyd.ms.io.packaging;

import com.hyd.utilities.FilteredIterable;

/**
 * Package relationships exposed to api user
 */
public class PackageRelationshipCollection extends FilteredIterable<PackageRelationship> {

    public PackageRelationshipCollection(InternalRelationshipCollection relationships, String filter) {
        super(relationships.getRelationshipIterator(), r -> r.getRelationshipType().equals(filter));
    }
}
