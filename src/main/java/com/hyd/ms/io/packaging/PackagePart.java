package com.hyd.ms.io.packaging;

import com.hyd.ms.io.Stream;

public abstract class PackagePart {

    private final PackUriHelper.ValidatedPartUri uri;

    private final Package container;

    private final InternalRelationshipCollection relationships;

    private final boolean relationshipPart;

    private final ContentType contentType;

    private boolean deleted;

    protected PackagePart(Package container, PackUriHelper.ValidatedPartUri uri, String contentType) {
        this.uri = uri;
        this.container = container;
        this.relationships = new InternalRelationshipCollection(container, this);
        this.relationshipPart = PackUriHelper.isRelationshipPartUri(uri);
        this.contentType = contentType == null ? null : new ContentType(contentType);
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

    public Stream getStream() {
        return getStreamCore();
    }

    /////////////////////////////////////////////////////////////////// abstract methods

    protected abstract Stream getStreamCore();
}
