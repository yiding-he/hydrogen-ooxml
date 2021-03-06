package com.hyd.ms.io.packaging;

import com.hyd.utilities.FilteredIterable;

/**
 * Package relationships exposed to api user
 */
public class PackageRelationshipCollection extends FilteredIterable<PackageRelationship> {

    public PackageRelationshipCollection(InternalRelationshipCollection relationships, String filter) {
        super(relationships.getRelationshipIterable().iterator(), r -> filter == null || r.getRelationshipType().equals(filter));
    }

    public PackageRelationshipCollection(InternalRelationshipCollection relationships) {
        super(relationships.getRelationshipIterable().iterator(), r -> true);
    }

    public boolean isEmpty() {
        return !iterator().hasNext();
    }
}
