package com.hyd.ms.io.packaging;

import com.hyd.ms.io.packaging.PackUriHelper.ValidatedPartUri;

import java.net.URI;

import static com.hyd.ms.io.packaging.PackUriHelper.validatePartUri;

public class PackageRelationship {

    public static ValidatedPartUri CONTAINER_RELATIONSHIP_PART_NAME = validatePartUri("/_rels/.rels");

    private final Package _package;

    private final PackagePart source;

    private final URI targetUri;

    private final String relationshipType;

    private final TargetMode targetMode;

    private final String id;

    public PackageRelationship(
        Package aPackage, PackagePart source, URI targetUri, TargetMode targetMode, String relationshipType, String id) {
        _package = aPackage;
        this.source = source;
        this.targetUri = targetUri;
        this.relationshipType = relationshipType;
        this.targetMode = targetMode;
        this.id = id;
    }

    public Package getPackage() {
        return _package;
    }

    public PackagePart getSource() {
        return source;
    }

    public URI getTargetUri() {
        return targetUri;
    }

    public String getRelationshipType() {
        return relationshipType;
    }

    public TargetMode getTargetMode() {
        return targetMode;
    }

    public String getId() {
        return id;
    }
}
