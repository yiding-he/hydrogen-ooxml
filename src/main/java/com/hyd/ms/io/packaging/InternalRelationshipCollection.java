package com.hyd.ms.io.packaging;

import com.hyd.ms.io.FileAccess;
import com.hyd.utilities.assertion.Assert;
import com.hyd.xml.Xml;
import com.hyd.xml.XmlBuilder;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;

import java.net.URI;
import java.util.TreeMap;
import java.util.UUID;

@Slf4j
public class InternalRelationshipCollection {

    public static final ContentType RELATIONSHIP_PART_CONTENT_TYPE
        = new ContentType("application/vnd.openxmlformats-package.relationships+xml");

    public static final String RELATIONSHIPS_NAMESPACE = "http://schemas.openxmlformats.org/package/2006/relationships";

    /**
     * our package - in case _sourcePart is null
     */
    private final Package __package;

    /**
     * owning part - null if package is the owner
     */
    private final PackagePart sourcePart;

    /**
     * where our relationships are persisted
     */
    private PackagePart relationshipPart;

    /**
     * the URI of our relationship part
     */
    private final URI uri;

    private final TreeMap<String, PackageRelationship> relationships = new TreeMap<>();

    private boolean dirty;

    public InternalRelationshipCollection(PackagePart part) {
        this(part.getPackage(), part);
    }

    public InternalRelationshipCollection(Package __package) {
        this(__package, null);
    }

    /**
     * Constructor
     *
     * @param __package package
     * @param part     will be null if package is the source of the relationships
     */
    private InternalRelationshipCollection(Package __package, PackagePart part) {
        this.__package = __package;
        this.sourcePart = part;
        this.uri = PackUriHelper.getRelationshipPartUri(
            part == null ? PackUriHelper.PACKAGE_ROOT_URI : sourcePart.getUri().getUri()
        );
        if (__package.partExists(this.uri)) {
            this.relationshipPart = __package.getPart(this.uri);
            parseRelationshipPart(this.relationshipPart);
        }
    }

    private void parseRelationshipPart(PackagePart relationshipPart) {
        FileAccess packageAccess = this.__package.getFileOpenAccess();
        Assert.that(
            packageAccess == FileAccess.Read || packageAccess == FileAccess.ReadWrite,
            "This method should only be called when FileAccess is Read or ReadWrite"
        );

        Document doc = Xml.parseDocumentAndClose(relationshipPart.getStream().read());
        Xml.lookupElements(doc.getDocumentElement(), "Relationship").forEach(rel -> {
            final String id = rel.getAttribute("Id");
            final String type = rel.getAttribute("Type");
            final String target = rel.getAttribute("Target");
            final TargetMode targetMode = TargetMode.fromRelationship(rel);
            add(PackUriHelper.uri(target), targetMode, type, id, true);
        });
    }

    public Iterable<PackageRelationship> getRelationshipIterable() {
        return this.relationships.values();
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

        PackageRelationship relationship = new PackageRelationship(__package, sourcePart, targetUri, targetMode, relationshipType, id);
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

    public void flush() {
        if (this.relationships.isEmpty()) {
            if (this.__package.partExists(this.uri)) {
                this.__package.deletePart(this.uri);
            }
            this.relationshipPart = null;
        } else {
            ensureRelationshipPart();
            writeRelationshipPart(this.relationshipPart);
        }
        this.dirty = false;
    }

    private void writeRelationshipPart(PackagePart relationshipPart) {
        log.debug("Writing relationships {}", relationshipPart.getUri());

        XmlBuilder xmlBuilder = new XmlBuilder(relationshipPart.getStream());
        XmlBuilder.XmlBuilderElement root = xmlBuilder
            .createRoot("Relationships")
            .setDefaultNamespace("http://schemas.openxmlformats.org/package/2006/relationships");

        for (PackageRelationship rel : getRelationshipIterable()) {
            xmlBuilder.appendChild(root, "Relationship")
                .addAttribute("Type", rel.getRelationshipType())
                .addAttribute("Target", rel.getTargetUri().toString())
                .addAttribute("Id", rel.getId());
        }

        xmlBuilder.finish();
    }

    private void ensureRelationshipPart() {
        if (this.relationshipPart == null || this.relationshipPart.isDeleted()) {
            if (this.__package.partExists(this.uri)) {
                this.relationshipPart = this.__package.getPart(this.uri);
            } else {
                this.relationshipPart = this.__package.createPart(this.uri, RELATIONSHIP_PART_CONTENT_TYPE.toString());
            }
        }
    }

    public void clear() {
        this.relationships.clear();
    }

    public void delete(String id) {
        this.dirty = this.relationships.remove(id) != null;
    }
}
