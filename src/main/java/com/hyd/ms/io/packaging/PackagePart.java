package com.hyd.ms.io.packaging;

import com.hyd.ms.io.FileAccess;
import com.hyd.ms.io.Stream;
import com.hyd.utilities.assertion.Assert;

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

    public void flushRelationships() {
        Assert.not(deleted, "part is deleted");
        if (container.getFileOpenAccess() != FileAccess.Read) {
            relationships.flush();
        }
    }

    public boolean isRelationshipPart() {
        return relationshipPart;
    }

    public void close() {
        // nothing need to do
    }

    public void flush() {
        // nothing need to do
    }

    /////////////////////////////////////////////////////////////////// abstract methods

    protected abstract Stream getStreamCore();
}
