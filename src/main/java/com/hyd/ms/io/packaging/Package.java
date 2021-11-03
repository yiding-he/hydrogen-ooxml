package com.hyd.ms.io.packaging;

import com.hyd.ms.io.FileAccess;
import com.hyd.ms.io.FileMode;
import com.hyd.ms.io.FileShare;
import com.hyd.utilities.assertion.Assert;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.util.TreeMap;

public abstract class Package implements Closeable {

    public static Package open(String path, FileMode packageMode, FileAccess packageAccess, FileShare packageShare) {
        return new ZipPackage(path, packageMode, packageAccess);
    }

    /////////////////////////////////////////////////////////////////// members

    private final FileAccess fileAccess;

    private final TreeMap<PackUriHelper.ValidatedPartUri, PackagePart> partList = new TreeMap<>();

    private final InternalRelationshipCollection relationships = new InternalRelationshipCollection(this, null);

    private PackageProperties packageProperties;

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

    @Override
    public void close() throws IOException {
        if (packageProperties != null) {
            packageProperties.close();
        }

        flushRelationships();

        this.partList.forEach((validatedPartUri, part) -> {
            closePart(part);
        });
        this.partList.clear();
        this.disposed = true;
    }

    private void closePart(PackagePart part) {
        // TODO implement com.hyd.ms.io.packaging.Package.closePart()
    }

    private void flushRelationships() {
        this.relationships.flush();
    }


    public PackagePart createPart(URI partUri, String contentType) {
        return createPart(partUri, contentType, CompressionOption.NotCompressed);
    }

    public PackagePart createPart(URI partUri, String contentType, CompressionOption compressionOption) {
        PackUriHelper.ValidatedPartUri validatedPartUri = PackUriHelper.validatePartUri(partUri);
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

    public PackagePart getPart(URI partUri) {
        PackUriHelper.ValidatedPartUri validatePartUri = PackUriHelper.validatePartUri(partUri);
        if (partList.containsKey(validatePartUri)) {
            return partList.get(validatePartUri);
        } else {
            PackagePart returnedPart = getPartCore(partUri);
            addIfNoPrefixCollisionDetected(validatePartUri, returnedPart);
            return returnedPart;
        }
    }

    public PackageRelationship createRelationship(
        URI targetUri, TargetMode targetMode, String relationshipType, String id) {
        return this.relationships.add(targetUri, targetMode, relationshipType, id);
    }

    public PackageRelationshipCollection GetRelationshipsByType(String relationshipType) {
        throwIfDisposed();
        throwIfWriteOnly();
        Assert.notBlank(relationshipType, "relationshipType");
        return new PackageRelationshipCollection(relationships, relationshipType);
    }

    public void deletePart(URI partUri) {
        Assert.notNull(partUri, "partUri");

        PackUriHelper.ValidatedPartUri validatedPartUri = PackUriHelper.validatePartUri(partUri);
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

        } else {
            deletePart(PackUriHelper.getRelationshipPartUri(validatedPartUri.getUri()));
        }
    }

    public void flush() {
        throwIfDisposed();
        throwIfReadOnly();

        flushRelationships();

        this.partList.values().forEach(p -> {
            if (!p.isRelationshipPart()) {
                p.flushRelationships();
            }
            p.flush();
        });

        flushCore();
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

    /////////////////////////////////////////////////////////////////// private methods

    private void addIfNoPrefixCollisionDetected(
        PackUriHelper.ValidatedPartUri validatePartUri, PackagePart part
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
        PackUriHelper.ValidatedPartUri validatedPartUri = PackUriHelper.validatePartUri(uri);
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
}
