package com.hyd.ms.io.packaging;

import com.hyd.assertion.Assert;

import java.net.URI;
import java.util.TreeMap;
import java.util.UUID;

public class InternalRelationshipCollection {

    /**
     * our package - in case _sourcePart is null
     */
    private final Package _package;

    /**
     * owning part - null if package is the owner
     */
    private final PackagePart sourcePart;

    /**
     * the URI of our relationship part
     */
    private final URI uri;

    private final TreeMap<String, PackageRelationship> relationships = new TreeMap<>();

    private boolean dirty;

    /**
     * Constructor
     *
     * @param _package package
     * @param part     will be null if package is the source of the relationships
     */
    public InternalRelationshipCollection(Package _package, PackagePart part) {
        this._package = _package;
        this.sourcePart = part;
        this.uri = PackUriHelper.getRelationshipPartUri(
            part == null ? PackUriHelper.PACKAGE_ROOT_URI : sourcePart.getUri().getUri()
        );
    }

    public PackageRelationship add(
        URI targetUri, TargetMode targetMode, String relationshipType, String id
    ) {
        return add(targetUri, targetMode, relationshipType, id, false);
    }

    public PackageRelationship add(
        URI targetUri, TargetMode targetMode, String relationshipType, String id, boolean parsing
    ) {
        Assert.notNull(targetUri, "targetUri");
        Assert.notNull(targetMode, "targetMode");
        Assert.notBlank(relationshipType, "relationshipType");

        Assert.not(
            targetMode == TargetMode.Internal && targetUri.isAbsolute(),
            "invalid uri '" + targetUri + "' : internal target must be relative"
        );

        // Generate an ID if id is null. Throw exception if neither null nor a valid unique xsd:ID.
        if (id == null)
            id = generateUniqueRelationshipId();
        else
            validateUniqueRelationshipId(id);

        PackageRelationship relationship = new PackageRelationship(_package, sourcePart, targetUri, targetMode, relationshipType, id);
        relationships.put(id, relationship);

        //If we are adding relationships as a part of Parsing the underlying relationship part, we should not set
        //the dirty flag to false.
        dirty = !parsing;

        return relationship;
    }

    private void validateUniqueRelationshipId(String id) {
        Assert.isNCName(id, "relationship id");
        Assert.not(relationships.containsKey(id), "relationship id '" + id + "' already exists");
    }

    private String generateUniqueRelationshipId() {
        String id;
        do {
            id = generateRelationshipId();
        } while (relationships.containsKey(id));
        return id;
    }

    private static String generateRelationshipId() {
        return "R" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}
