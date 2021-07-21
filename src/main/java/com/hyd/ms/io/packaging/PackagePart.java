package com.hyd.ms.io.packaging;

import java.net.URI;

public abstract class PackagePart {

    private final PackUriHelper.ValidatedPartUri uri;

    private final Package container;

    private final InternalRelationshipCollection relationships;

    private final boolean relationshipPart;

    private boolean deleted;

    protected PackagePart(Package container, URI partUri) {
        this.uri = PackUriHelper.validatePartUri(partUri);
        this.container = container;
        this.relationships = new InternalRelationshipCollection(container, this);
        this.relationshipPart = PackUriHelper.isRelationshipPartUri(partUri);
    }

    public PackUriHelper.ValidatedPartUri getUri() {
        return uri;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isDeleted() {
        return deleted;
    }

    /////////////////////////////////////////////////////////////////// abstract methods


}
