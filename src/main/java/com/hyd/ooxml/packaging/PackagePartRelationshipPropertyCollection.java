package com.hyd.ooxml.packaging;

import com.hyd.ms.io.packaging.Package;
import com.hyd.ms.io.packaging.PackagePart;
import com.hyd.ms.io.packaging.TargetMode;

import java.net.URI;

/**
 * Represents a collection of relationships that are obtained from the package part.
 */
public class PackagePartRelationshipPropertyCollection extends RelationshipCollection {

    private final PackagePart packagePart;

    public PackagePartRelationshipPropertyCollection(PackagePart packagePart) {
        super(packagePart.getRelationships());
        this.packagePart = packagePart;
    }

    @Override
    public void replaceRelationship(URI targetUri, TargetMode targetMode, String strRelationshipType, String strId) {
        packagePart.deleteRelationship(strId);
        packagePart.createRelationship(targetUri, targetMode, strRelationshipType, strId);
    }

    @Override
    public Package getPackage() {
        return packagePart.getPackage();
    }
}
