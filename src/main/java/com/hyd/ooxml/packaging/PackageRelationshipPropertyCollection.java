package com.hyd.ooxml.packaging;

import com.hyd.ms.io.packaging.Package;
import com.hyd.ms.io.packaging.TargetMode;

import java.net.URI;

public class PackageRelationshipPropertyCollection extends RelationshipCollection {

    private final Package __package;

    public PackageRelationshipPropertyCollection(Package __package) {
        super(__package.getRelationships());
        this.__package = __package;
    }

    @Override
    public void replaceRelationship(
        URI targetUri, TargetMode targetMode, String strRelationshipType, String relId
    ) {
        __package.deleteRelationship(relId);
        __package.createRelationship(targetUri, targetMode, strRelationshipType, relId);
    }

    @Override
    public Package getPackage() {
        return this.__package;
    }
}
