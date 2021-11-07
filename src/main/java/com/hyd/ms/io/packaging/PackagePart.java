package com.hyd.ms.io.packaging;

import com.hyd.ms.io.FileAccess;
import com.hyd.ms.io.Stream;
import com.hyd.utilities.assertion.Assert;

public abstract class PackagePart {

    private final PackUriHelper.ValidatedPartUri uri;

    private final Package container;

    private final boolean relationshipPart;

    private final ContentType contentType;

    private InternalRelationshipCollection relationships;

    private boolean deleted;

    protected PackagePart(Package container, PackUriHelper.ValidatedPartUri uri, String contentType) {
        this.uri = uri;
        this.container = container;
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

    public Package getPackage() {
        return this.container;
    }

    public void flushRelationships() {
        Assert.not(this.deleted, "part is deleted");
        if (this.container.getFileOpenAccess() != FileAccess.Read && this.relationships != null) {
            this.relationships.flush();
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

    public String getContentType() {
        return this.contentType.toString();
    }

    public void clearRelationships() {
        this.relationships.clear();
    }

    private void ensureRelationships() {
        if (this.relationships == null) {
            Assert.not(this.isRelationshipPart(), "relationship part cannot have relationships");
            this.relationships = new InternalRelationshipCollection(this);
        }
    }

    /////////////////////////////////////////////////////////////////// abstract methods

    protected abstract Stream getStreamCore();
}
