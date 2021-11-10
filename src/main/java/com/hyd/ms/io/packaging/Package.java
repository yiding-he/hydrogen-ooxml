package com.hyd.ms.io.packaging;

import com.hyd.ms.io.FileAccess;
import com.hyd.ms.io.FileMode;
import com.hyd.ms.io.FileShare;
import com.hyd.ms.io.Stream;
import com.hyd.ms.io.packaging.PackUriHelper.ValidatedPartUri;
import com.hyd.utilities.assertion.Assert;

import java.io.Closeable;
import java.net.URI;
import java.util.HashMap;
import java.util.TreeMap;

public abstract class Package implements Closeable {

    public static Package open(Stream stream, FileMode packageMode, FileAccess packageAccess) {
        return init(new ZipPackage(stream, packageMode, packageAccess));
    }

    public static Package open(String path, FileMode packageMode, FileAccess packageAccess, FileShare packageShare) {
        return init(new ZipPackage(path, packageMode, packageAccess));
    }

    private static Package init(Package createdPackage) {

        // We need to get all the parts if any exists from the underlying file
        // so that we have the names in the Normalized form in our in-memory
        // data structures.
        // Note: If ever this call is removed, each individual call to GetPartCore,
        // may result in undefined behavior as the underlying ZipArchive, maintains the
        // files list as being case-sensitive.
        FileAccess openAccess = createdPackage.getFileOpenAccess();
        if (openAccess == FileAccess.ReadWrite || openAccess == FileAccess.Read) {
            createdPackage.getParts();
        }

        return createdPackage;
    }

    /////////////////////////////////////////////////////////////////// members

    private final FileAccess fileAccess;

    private TreeMap<ValidatedPartUri, PackagePart> partList = new TreeMap<>();

    private InternalRelationshipCollection relationships;

    private PackageProperties packageProperties;

    private PackagePartCollection partCollection;

    private boolean disposed = false;

    /////////////////////////////////////////////////////////////////// constructors

    protected Package(FileAccess fileAccess) {
        this.fileAccess = fileAccess;
    }

    /////////////////////////////////////////////////////////////////// public methods

    public FileAccess getFileOpenAccess() {
        return fileAccess;
    }

    public PackageProperties getPackageProperties() {
        if (packageProperties == null) {
            packageProperties = new PartBasedPackageProperties(this);
        }
        return packageProperties;
    }

    public void flush() {
        throwIfDisposed();
        throwIfReadOnly();

        flushRelationships();

        this.partList.values().forEach(p -> {
            // DoWriteRelationshipsXml
            if (!p.isRelationshipPart()) {
                p.flushRelationships();
            }
            // DoFlush
            p.flush();
        });

        flushCore();
    }

    @Override
    public void close() {

        if (disposed) {
            return;
        }

        try {
            if (packageProperties != null) {
                packageProperties.close();
            }

            flushRelationships();

            new HashMap<>(this.partList).forEach((validatedPartUri, part) -> {
                // DoCloseRelationshipsXml
                if (!part.isRelationshipPart() && !part.isDeleted()) {
                    part.flushRelationships();
                }
                // DoClose
                closePart(part);
            });

            dispose(true);
        } finally {
            this.disposed = true;
        }
    }

    private void closePart(PackagePart part) {
        if (part.isRelationshipPart() &&
            PackUriHelper.comparePartUri(part.getUri(), PackageRelationship.CONTAINER_RELATIONSHIP_PART_NAME) != 0) {
            ValidatedPartUri owningPartUri = PackUriHelper.getSourcePartUriFromRelationshipPartUri(part.getUri());
            if (this.partList.containsKey(owningPartUri)) {
                this.partList.get(owningPartUri).close();
            }
        }
        part.close();
    }

    private void flushRelationships() {
        if (this.relationships != null) {
            this.relationships.flush();
        }
    }


    public PackagePart createPart(URI partUri, String contentType) {
        return createPart(partUri, contentType, CompressionOption.NotCompressed);
    }

    public PackagePart createPart(URI partUri, String contentType, CompressionOption compressionOption) {
        ValidatedPartUri validatedPartUri = PackUriHelper.validatePartUri(partUri);
        if (partList.containsKey(validatedPartUri)) {
            throw new IllegalStateException("part '" + partUri + "' already exists");
        }

        // Add the part to the _partList if there is no prefix collision
        // Note: This is the only place where we pass a null to this method for the part and if the
        // methods returns successfully then we replace the null with an actual part.
        addIfNoPrefixCollisionDetected(validatedPartUri, null /* since we don't have a part yet */);

        PackagePart addedPart = createPartCore(partUri, contentType, compressionOption);

        // Set the entry for this Uri with the actual part
        this.partList.put(validatedPartUri, addedPart);
        return addedPart;
    }

    public PackagePartCollection getParts() {
        throwIfDisposed();
        throwIfWriteOnly();

        if (partCollection == null) {
            PackagePart[] parts = getPartsCore();
            TreeMap<ValidatedPartUri, PackagePart> seenPartUris = new TreeMap<>();

            for (PackagePart part : parts) {
                ValidatedPartUri partUri = part.getUri();
                if (seenPartUris.containsKey(partUri)) {
                    throw new IllegalStateException("Duplicate part uri found: " + partUri);
                } else {
                    seenPartUris.put(partUri, part);
                }
                if (!partList.containsKey(partUri)) {
                    addIfNoPrefixCollisionDetected(partUri, part);
                }
            }

            partCollection = new PackagePartCollection(seenPartUris);
        }
        return partCollection;
    }

    public PackagePart getPart(URI partUri) {
        ValidatedPartUri validatePartUri = PackUriHelper.validatePartUri(partUri);
        if (partList.containsKey(validatePartUri)) {
            return partList.get(validatePartUri);
        } else {
            PackagePart returnedPart = getPartCore(partUri);
            if (returnedPart != null) {
                addIfNoPrefixCollisionDetected(validatePartUri, returnedPart);
            }
            return returnedPart;
        }
    }

    public PackageRelationship createRelationship(
        URI targetUri, TargetMode targetMode, String relationshipType) {
        return createRelationship(targetUri, targetMode, relationshipType, null);
    }

    public PackageRelationship createRelationship(
        URI targetUri, TargetMode targetMode, String relationshipType, String id) {
        throwIfDisposed();
        throwIfReadOnly();
        ensureRelationships();
        return this.relationships.add(targetUri, targetMode, relationshipType, id);
    }

    public PackageRelationshipCollection getRelationshipsByType(String relationshipType) {
        throwIfDisposed();
        throwIfWriteOnly();
        Assert.notBlank(relationshipType, "relationshipType");
        return new PackageRelationshipCollection(relationships, relationshipType);
    }

    public PackageRelationshipCollection getRelationships() {
        throwIfDisposed();
        throwIfWriteOnly();
        ensureRelationships();
        return new PackageRelationshipCollection(relationships);
    }

    public void deletePart(URI partUri) {
        Assert.notNull(partUri, "partUri");

        ValidatedPartUri validatedPartUri = PackUriHelper.validatePartUri(partUri);
        if (partList.containsKey(validatedPartUri)) {
            PackagePart part = partList.get(validatedPartUri);
            part.setDeleted(true);

            // !!Important Note: The order of this call is important as one of the
            // sub-classes - ZipPackage relies upon the abstract layer to be
            // able to provide the ZipPackagePart in order to do the proper
            // clean up and delete operation.
            // The dependency is in ZipPackagePart.DeletePartCore method.
            // Ideally we would have liked to avoid this kind of a restriction
            // but due to the current class interfaces and data structure ownerships
            // between these objects, it tough to re-design at this point.
            deletePartCore(part.getUri().getUri());

            partList.remove(validatedPartUri);

        } else {
            // If the part is not in memory we still call the underlying layer
            // to delete the part if it exists
            deletePartCore(partUri);
        }

        if (PackUriHelper.isRelationshipPartUri(partUri)) {
            ValidatedPartUri owningPartUri = PackUriHelper.getSourcePartUriFromRelationshipPartUri(validatedPartUri);
            if (PackUriHelper.comparePartUri(owningPartUri, PackUriHelper.ROOT_RELATION_VALIDATED_URI) == 0) {
                this.clearRelationships();
            } else {
                if (this.partExists(owningPartUri.getUri())) {
                    this.getPart(owningPartUri.getUri()).clearRelationships();
                }
            }
        } else {
            deletePart(PackUriHelper.getRelationshipPartUri(validatedPartUri.getUri()));
        }
    }

    public void deleteRelationship(String id) {
        if (this.relationships != null) {
            this.relationships.delete(id);
        }
    }

    private void clearRelationships() {
        if (this.relationships != null) {
            this.relationships.clear();
        }
    }

    /////////////////////////////////////////////////////////////////// private methods

    private void ensureRelationships() {
        if (this.relationships == null) {
            this.relationships = new InternalRelationshipCollection(this);
        }
    }

    private void throwIfReadOnly() {
        if (this.fileAccess == FileAccess.Read) {
            throw new IllegalStateException("Package is read only");
        }
    }

    private void throwIfWriteOnly() {
        if (this.fileAccess == FileAccess.Write) {
            throw new IllegalStateException("Package is write only");
        }
    }

    private void throwIfDisposed() {
        if (this.disposed) {
            throw new IllegalStateException("Package is closed");
        }
    }

    private void addIfNoPrefixCollisionDetected(
        ValidatedPartUri validatePartUri, PackagePart part
    ) {
        partList.put(validatePartUri, part);

        String normalizedPartName = validatePartUri.toString();
        String precedingPartName = partList.floorEntry(validatePartUri).toString();
        String followingPartName = partList.ceilingKey(validatePartUri).toString();

        if (precedingPartName != null
            && normalizedPartName.startsWith(precedingPartName)
            && normalizedPartName.length() > precedingPartName.length()
            && normalizedPartName.charAt(precedingPartName.length()) == '/') {

            partList.remove(validatePartUri);
            throw new IllegalArgumentException(
                "collision detected for '" + normalizedPartName + "', " +
                    "there is a '" + precedingPartName + "' already exists");
        }

        if (followingPartName != null
            && normalizedPartName.startsWith(followingPartName)
            && normalizedPartName.length() > followingPartName.length()
            && normalizedPartName.charAt(followingPartName.length()) == '/') {

            partList.remove(validatePartUri);
            throw new IllegalArgumentException(
                "collision detected for '" + normalizedPartName + "', " +
                    "there is a '" + followingPartName + "' already exists");
        }
    }

    public boolean partExists(URI uri) {
        throwIfDisposed();
        Assert.not(uri == null, "uri cannot be null");
        ValidatedPartUri validatedPartUri = PackUriHelper.validatePartUri(uri);
        return partList.containsKey(validatedPartUri);
    }

    ///////////////////////////////////////////////////////////////////

    protected abstract PackagePart createPartCore(URI partUri, String contentType, CompressionOption compressionOption);

    protected abstract PackagePart getPartCore(URI partUri);

    protected abstract void deletePartCore(URI partUri);

    protected abstract PackagePart[] getPartsCore();

    /**
     * This method is for custom implementation corresponding to the underlying file format.
     * This method flushes the contents of the package to the disc.
     */
    protected abstract void flushCore();

    protected void dispose(boolean disposing) {
        if (!disposed && disposing) {
            this.partList.clear();
            if (packageProperties != null) {
                packageProperties.close();
                packageProperties = null;
            }
            this.partList = null;
            this.relationships = null;
            this.disposed = true;
        }
    }
}
