package com.hyd.ooxml.packaging;

import com.hyd.ms.io.Stream;
import com.hyd.ms.io.packaging.PackUriHelper;
import com.hyd.ms.io.packaging.PackagePart;
import com.hyd.ms.io.packaging.PackageRelationship;
import com.hyd.ms.io.packaging.TargetMode;
import com.hyd.ooxml.framework.PackageCache;
import com.hyd.ooxml.framework.PartConstraintRule;
import com.hyd.utilities.assertion.Assert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.util.*;

@SuppressWarnings("unchecked")
@Slf4j
public abstract class OpenXmlPartContainer {

    protected final Map<String, OpenXmlPart> childrenPartsDictionary = new TreeMap<>();

    protected final LinkedList<ReferenceRelationship> referenceRelationships = new LinkedList<>();

    protected Object annotations;

    protected OpenXmlPartData data;

    {
        data = PackageCache.getInstance().parsePartData(this);
    }

    public LinkedList<ReferenceRelationship> getReferenceRelationshipList() {
        return referenceRelationships;
    }

    public Iterable<IdPartPair> parts() {
        return childrenPartsDictionary.entrySet()
            .stream().map(entry -> new IdPartPair(entry.getKey(), entry.getValue()))
            ::iterator;
    }

    public Iterable<ExternalRelationship> getExternalRelationships() {
        return getReferenceRelationshipList().stream()
            .filter(r -> r instanceof ExternalRelationship)
            .map(r -> (ExternalRelationship) r)
            ::iterator;
    }

    public Iterable<HyperlinkRelationship> getHyperlinkRelationships() {
        return getReferenceRelationshipList().stream()
            .filter(r -> r instanceof HyperlinkRelationship)
            .map(r -> (HyperlinkRelationship) r)
            ::iterator;
    }

    public Iterable<DataPartReferenceRelationship> getDataPartReferenceRelationships() {
        return getReferenceRelationshipList().stream()
            .filter(r -> r instanceof DataPartReferenceRelationship)
            .map(r -> (DataPartReferenceRelationship) r)
            ::iterator;
    }

    public <T extends OpenXmlPart> T addPart(T part, String id) {
        return (T) addPartFrom(part, id);
    }

    protected OpenXmlPart addPartFrom(OpenXmlPart subPart, String rId) {
        return addPartFrom(subPart, rId, true);
    }

    protected OpenXmlPart addPartFrom(OpenXmlPart subPart, String rId, boolean validateConstraints) {

        if (subPart.getOpenXmlPackage() == getInternalOpenXmlPackage()) {
            if (isChildPart(subPart)) {
                String idOfPart = getIdOfPart(subPart);
                if (!rId.equals(idOfPart)) {
                    throw new IllegalArgumentException("subPart already has another relationship rId");
                } else {
                    return subPart;
                }
            }
        }

        if (validateConstraints) {
            String relType = subPart.getRelationshipType();
            if (!data.getPartConstraints().containsRelationship(relType)) {
                Assert.that(subPart instanceof ExtendedPart,
                    "The part of relationship type '%s' cannot be added here", relType);
            } else {
                PartConstraintRule rule = data.getPartConstraints().getConstrainRule(relType);
                Assert.that(rule.matchContentType(subPart.getContentType()),
                    "The part of relationship type '%s' cannot be added here", relType);

                if (!rule.isMaxOccursGreaterThanOne()) {
                    Assert.that(getSubPart(relType) == null,
                        "Only one instance of relationship type '%s' is allowed for this parent", relType);
                }
            }
        }

        return addSubPart(subPart, rId);
    }

    public OpenXmlPart getSubPart(String relationshipType) {
        Assert.notNull(relationshipType, "relationshipType");
        return childrenPartsDictionary.values().stream()
            .filter(p -> p.getRelationshipType().equals(relationshipType))
            .findFirst().orElse(null);
    }

    public <T extends OpenXmlPart> T addNewPart(Class<T> type, String id) {
        return addNewPartInternal(type, null, id);
    }

    private <T extends OpenXmlPart> T addNewPartInternal(Class<T> type, String contentType, String id) {
        Assert.not(
            id != null && childrenPartsDictionary.containsKey(id),
            "part id already exists: %s", id
        );

        try {
            T part = type.newInstance();
            initPart(part, StringUtils.defaultString(contentType, part.getAnnotatedContentType()), id);
            return part;
        } catch (OpenXmlPackageException e) {
            throw e;
        } catch (Exception e) {
            throw new OpenXmlPackageException(e);
        }
    }

    protected void initPart(OpenXmlPart newPart, String contentType, String id) {
        Assert.notBlank(contentType, "contentType");
        OpenXmlPackage internalOpenXmlPackage = getInternalOpenXmlPackage();
        OpenXmlPart thisOpenXmlPart = getThisOpenXmlPart();
        newPart.createInternal(internalOpenXmlPackage, thisOpenXmlPart, contentType, null);
        String relationshipId = attachChild(newPart, id);
        this.childrenPartsDictionary.put(relationshipId, newPart);
    }

    private OpenXmlPart addSubPart(OpenXmlPart subPart, String id) {
        // check if part is shared
        if (subPart.getOpenXmlPackage() == getInternalOpenXmlPackage()) {
            // it is a part shared in the same package
            String relationshipId = attachChild(subPart, id);
            this.childrenPartsDictionary.put(relationshipId, subPart);
            return subPart;
        } else {
            return addSubPartFromOtherPackage(subPart, false, id);
        }
    }

    protected String attachChild(OpenXmlPart part, String id) {
        PackageRelationship relationship;
        if (id == null) {
            relationship = createRelationship(part.getUri(), TargetMode.Internal, part.getRelationshipType());
        } else {
            relationship = createRelationship(part.getUri(), TargetMode.Internal, part.getRelationshipType(), id);
        }
        return relationship.getId();
    }

    private String getIdOfPart(OpenXmlPart subPart) {
        for (Map.Entry<String, OpenXmlPart> entry : childrenPartsDictionary.entrySet()) {
            if (entry.getValue() == subPart) {
                return entry.getKey();
            }
        }
        throw new IllegalArgumentException("subPart is out of range");
    }

    @SuppressWarnings("ConstantConditions")
    private boolean isChildPart(OpenXmlPart subPart) {
        Assert.that(subPart != null, "subPart cannot be null");
        Assert.that(subPart.getOpenXmlPackage() == this.getInternalOpenXmlPackage(), "subPart is out of range");
        return childrenPartsDictionary.containsValue(subPart);
    }

    private OpenXmlPart createOpenXmlPart(String relationshipType) {
        Assert.notNull(relationshipType, "relationshipType");

        if (data.getPartConstraints().containsRelationship(relationshipType)) {
            return createPartCore(relationshipType);
        } else {
            return new ExtendedPart(relationshipType);
        }
    }

    private OpenXmlPart createPartCore(String relationshipType) {
        log.warn("We need implement {}.createPartCore()", getClass().getCanonicalName());
        return new ExtendedPart(relationshipType);
    }

    private OpenXmlPart addSubPartFromOtherPackage(OpenXmlPart part, boolean keepIdAndUri, String rId) {
        return addSubPartFromOtherPackage(part, new HashMap<>(), new HashMap<>(), keepIdAndUri, rId);
    }

    OpenXmlPart addSubPartFromOtherPackage(
        OpenXmlPart part, Map<OpenXmlPart, OpenXmlPart> partDictionary, Map<DataPart, DataPart> dataPartsDictionary,
        boolean keepIdAndUri, String rId
    ) {
        if (keepIdAndUri) {
            Assert.notBlank(rId, "rId");
        }

        OpenXmlPart child;
        if (partDictionary.containsKey(part)) {
            child = partDictionary.get(part);
            String relationshipId = attachChild(child, rId);
            childrenPartsDictionary.put(relationshipId, child);

        } else {
            child = createOpenXmlPart(part.getRelationshipType());

            // try to keep the same name
            child.createInternal2(getInternalOpenXmlPackage(), getThisOpenXmlPart(), part.getContentType(), part.getUri());

            try (Stream stream = part.getStream()) {
                child.feedData(stream);
            }

            String relationshipId = attachChild(child, rId);
            childrenPartsDictionary.put(relationshipId, child);
            partDictionary.put(part, child);

            for (IdPartPair idPartPair : part.parts()) {
                child.addSubPartFromOtherPackage(idPartPair.openXmlPart, partDictionary, dataPartsDictionary, true, idPartPair.relationshipId);
            }

            for (ExternalRelationship r : part.getExternalRelationships()) {
                child.addExternalRelationship(r.getRelationshipType(), r.getUri(), r.getId());
            }

            for (HyperlinkRelationship r : part.getHyperlinkRelationships()) {
                child.addHyperlinkRelationship(r.getUri(), r.isExternal(), r.getId());
            }

            //////////////////////////

            // First, we need copy the referenced media data part.
            for (DataPartReferenceRelationship r : part.getDataPartReferenceRelationships()) {
                if (!dataPartsDictionary.containsKey(r.getDataPart())) {
                    dataPartsDictionary.put(r.getDataPart(), null);
                }
            }

            Map<DataPart, DataPart> updatedParts = new HashMap<>();
            dataPartsDictionary.forEach((dataPart, value) -> {
                if (value == null) {
                    DataPart newDataPart = new MediaDataPart(getInternalOpenXmlPackage(), dataPart.getContentType(), dataPart.getUri());
                    try (Stream stream = dataPart.getStream()) {
                        newDataPart.feedData(stream);
                    }
                    getInternalOpenXmlPackage().addDataPartToList(newDataPart);
                    updatedParts.put(dataPart, newDataPart);
                }
            });

            dataPartsDictionary.putAll(updatedParts);

            // then create data part reference relationship
            for (DataPartReferenceRelationship r : part.getDataPartReferenceRelationships()) {
                DataPart newDataPart = dataPartsDictionary.get(r.getDataPart());
                if (newDataPart instanceof MediaDataPart) {
                    child.addDataPartReferenceRelationship(
                        DataPartReferenceRelationship.create(child, newDataPart, r.getRelationshipType(), r.getId())
                    );
                }
            }

        }
        return child;
    }

    void addExternalRelationship(String relationshipType, URI uri, String id) {
        throw new UnsupportedOperationException("Not implemented yet");
        // TODO implement com.hyd.ooxml.packaging.OpenXmlPartContainer.addExternalRelationship()
    }

    void addHyperlinkRelationship(URI uri, boolean external, String id) {
        throw new UnsupportedOperationException("Not implemented yet");
        // TODO implement com.hyd.ooxml.packaging.OpenXmlPartContainer.addHyperlinkRelationship()
    }

    DataPartReferenceRelationship addDataPartReferenceRelationship(DataPartReferenceRelationship relationship) {
        DataPart mediaDataPart = relationship.getDataPart();
        createRelationship(mediaDataPart.getUri(), TargetMode.Internal, relationship.getRelationshipType(), relationship.getId());
        getReferenceRelationshipList().add(relationship);
        return relationship;
    }

    protected void loadReferencedPartsAndRelationships(
        OpenXmlPackage openXmlPackage, OpenXmlPart sourcePart,
        PackageRelationshipPropertyCollection relationshipCollection, Map<URI, OpenXmlPart> loadedParts
    ) {
        for (RelationshipProperty rel : relationshipCollection) {
            if (rel.getRelationshipType().equals(HyperlinkRelationship.RELATIONSHIP_TYPE_CONST)) {
                HyperlinkRelationship hyperRel = new HyperlinkRelationship(
                    rel.getTargetUri(), rel.getTargetMode() == TargetMode.External, rel.getId()
                );
                hyperRel.setContainer(this);
                referenceRelationships.add(hyperRel);

            } else {
                if (rel.getTargetMode() == TargetMode.Internal) {
                    if (!rel.getTargetUri().toString().equalsIgnoreCase("NULL")) {
                        URI sourceUri = sourcePart == null ? URI.create("/") : sourcePart.getUri();
                        URI targetUri = PackUriHelper.resolvePartUri(sourceUri, rel.getTargetUri());

                        if (loadedParts.containsKey(targetUri)) {
                            OpenXmlPart child = loadedParts.get(targetUri);
                            Assert.that(Objects.equals(child.getRelationshipType(), rel.getRelationshipType()),
                                "A shared part is referenced by multiple source parts with a different relationship type: %s", targetUri);
                            childrenPartsDictionary.put(rel.getId(), child);

                        } else if (DataPartReferenceRelationship.isDataPartReferenceRelationship(rel.getRelationshipType())) {
                            DataPart dataPart = openXmlPackage.findDataPart(targetUri);
                            if (dataPart == null) {
                                PackagePart packagePart = openXmlPackage.__package.getPart(targetUri);
                                dataPart = new MediaDataPart(openXmlPackage, packagePart);
                                openXmlPackage.addDataPartToList(dataPart);
                            }
                            DataPartReferenceRelationship dataRel = DataPartReferenceRelationship.create(this, dataPart, rel.getRelationshipType(), rel.getId());
                            referenceRelationships.add(dataRel);

                        } else {
                            OpenXmlPart child = createOpenXmlPart(rel.getRelationshipType());
                            loadedParts.put(targetUri, child);
                            child.load(openXmlPackage, sourcePart, targetUri, rel.getId(), loadedParts);
                            childrenPartsDictionary.put(rel.getId(), child);
                        }
                    }
                } else {
                    ExternalRelationship extRel = new ExternalRelationship(rel.getTargetUri(),rel.getRelationshipType(), rel.getId());
                    extRel.setContainer(this);
                    referenceRelationships.add(extRel);
                }
            }
        }
    }

    ///////////////////////////////////////////////////////////////////

    protected abstract OpenXmlPackage getInternalOpenXmlPackage();

    protected abstract OpenXmlPart getThisOpenXmlPart();

    protected abstract PackageRelationship createRelationship(
        URI targetUri, TargetMode targetMode, String relationshipType);

    protected abstract PackageRelationship createRelationship(
        URI targetUri, TargetMode targetMode, String relationshipType, String id);
}
