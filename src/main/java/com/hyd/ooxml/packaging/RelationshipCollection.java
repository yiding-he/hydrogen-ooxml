package com.hyd.ooxml.packaging;

import com.hyd.ms.io.packaging.Package;
import com.hyd.ms.io.packaging.PackageRelationship;
import com.hyd.ms.io.packaging.PackageRelationshipCollection;
import com.hyd.ms.io.packaging.TargetMode;

import java.net.URI;
import java.util.ArrayList;

public abstract class RelationshipCollection extends ArrayList<RelationshipProperty> {

    private final PackageRelationshipCollection collection;

    public RelationshipCollection(PackageRelationshipCollection collection) {
        this.collection = collection;
        build();
    }

    private void build() {
        for (PackageRelationship r : collection) {
            RelationshipProperty relationshipProperty = new RelationshipProperty();
            relationshipProperty.setTargetUri(r.getTargetUri());
            relationshipProperty.setTargetMode(r.getTargetMode());
            relationshipProperty.setId(r.getId());
            relationshipProperty.setRelationshipType(r.getRelationshipType());
            add(relationshipProperty);
        }
    }


    public abstract void replaceRelationship(
        URI targetUri, TargetMode targetMode, String strRelationshipType, String strId
    );

    public abstract Package getPackage();
}
