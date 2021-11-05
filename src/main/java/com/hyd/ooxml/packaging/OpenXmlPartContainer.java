package com.hyd.ooxml.packaging;

import com.hyd.ms.io.Stream;
import com.hyd.ms.io.packaging.PackageRelationship;
import com.hyd.ms.io.packaging.TargetMode;
import com.hyd.utilities.assertion.Assert;

import java.net.URI;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

@SuppressWarnings("unchecked")
public abstract class OpenXmlPartContainer {

    protected final Map<String, OpenXmlPart> childrenPartsDictionary = new TreeMap<>();

    protected final LinkedList<ReferenceRelationship> referenceRelationships = new LinkedList<>();

    protected Object annotations;

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
            .map(r -> (ExternalRelationship)r)
            ::iterator;
    }

    public Iterable<HyperlinkRelationship> getHyperlinkRelationships() {
        return getReferenceRelationshipList().stream()
            .filter(r -> r instanceof HyperlinkRelationship)
            .map(r -> (HyperlinkRelationship)r)
            ::iterator;
    }

    public Iterable<DataPartReferenceRelationship> getDataPartReferenceRelationships() {
        return getReferenceRelationshipList().stream()
            .filter(r -> r instanceof DataPartReferenceRelationship)
            .map(r -> (DataPartReferenceRelationship)r)
            ::iterator;
    }

    public <T extends OpenXmlPart> T addPart(T part, String id) {
        return (T) addPartFrom(part, id);
    }

    private OpenXmlPart addPartFrom(OpenXmlPart subPart, String id) {
        if (subPart.getOpenXmlPackage() == getInternalOpenXmlPackage()) {
            if (isChildPart(subPart)) {
                String idOfPart = getIdOfPart(subPart);
                if (!id.equals(idOfPart)) {
                    throw new IllegalArgumentException("subPart already has another relationship id");
                } else {
                    return subPart;
                }
            }
        }

        // TODO add PartConstraints validation here

        return addSubPart(subPart, id);
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

    private String attachChild(OpenXmlPart part, String id) {
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
        return null;// TODO implement com.hyd.ooxml.packaging.OpenXmlPartContainer.createOpenXmlPart()
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
        // TODO implement com.hyd.ooxml.packaging.OpenXmlPartContainer.addExternalRelationship()
    }

    void addHyperlinkRelationship(URI uri, boolean external, String id) {
        // TODO implement com.hyd.ooxml.packaging.OpenXmlPartContainer.addHyperlinkRelationship()
    }

    DataPartReferenceRelationship addDataPartReferenceRelationship(DataPartReferenceRelationship relationship) {
        DataPart mediaDataPart = relationship.getDataPart();
        createRelationship(mediaDataPart.getUri(), TargetMode.Internal, relationship.getRelationshipType(), relationship.getId());
        getReferenceRelationshipList().add(relationship);
        return relationship;
    }

    protected void loadReferencedPartsAndRelationships(
        OpenXmlPackage openXmlPackage, OpenXmlPart openXmlPart,
        PackageRelationshipPropertyCollection relationshipCollection, Map<URI, OpenXmlPart> loadedParts
    ) {
        // TODO implement com.hyd.ooxml.packaging.OpenXmlPackage.loadReferencedPartsAndRelationships()
    }

    ///////////////////////////////////////////////////////////////////

    protected abstract OpenXmlPackage getInternalOpenXmlPackage();

    protected abstract OpenXmlPart getThisOpenXmlPart();

    protected abstract PackageRelationship createRelationship(
        URI targetUri, TargetMode targetMode, String relationshipType);

    protected abstract PackageRelationship createRelationship(
        URI targetUri, TargetMode targetMode, String relationshipType, String id);
}
