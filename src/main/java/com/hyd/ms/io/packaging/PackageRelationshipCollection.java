package com.hyd.ms.io.packaging;

import com.hyd.utilities.FilteredIterator;

/**
 * Package relationships exposed to api user
 */
public class PackageRelationshipCollection extends FilteredIterator<PackageRelationship> {

    public PackageRelationshipCollection(InternalRelationshipCollection relationships, String filter) {
        super(relationships.getRelationshipIterator(), r -> r.getRelationshipType().equals(filter));
    }
}
