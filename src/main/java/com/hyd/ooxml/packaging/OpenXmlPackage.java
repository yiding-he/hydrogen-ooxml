package com.hyd.ooxml.packaging;

import com.hyd.ms.io.MemoryStream;
import com.hyd.ms.io.Stream;
import com.hyd.ms.io.packaging.Package;
import com.hyd.ms.io.packaging.*;
import com.hyd.utilities.assertion.Assert;

import java.io.Closeable;
import java.net.URI;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public abstract class OpenXmlPackage extends OpenXmlPartContainer implements Closeable {

    protected Package __package;

    protected String mainPartContentType;

    protected OpenSettings openSettings;

    protected CompressionOption compressionOption = CompressionOption.Normal;

    private PartUriHelper partUriHelper = new PartUriHelper();

    private final LinkedList<DataPart> dataPartList = new LinkedList<>();

    protected OpenXmlPackage() {
        __package = null;
        mainPartContentType = null;
        openSettings = null;
    }

    protected OpenXmlPackage(PackageLoader loader, OpenSettings settings) {
        openSettings = new OpenSettings(settings);
        __package = loader.getPackage();
        if (loader.isOpen()) {
            load(__package);
        }
    }

    private void load(Package __package) {
        // todo implement OpenXmlPackage.load()
        PackageRelationshipPropertyCollection relationshipCollection = new PackageRelationshipPropertyCollection(__package);

        boolean hasMainPart = false;
        for (RelationshipProperty rp : relationshipCollection) {
            if (rp.getRelationshipType().equals(getMainPartRelationshipType())) {
                hasMainPart = true;
                URI targetUri = PackUriHelper.resolvePartUri(URI.create("/"), rp.getTargetUri());
                PackagePart metroPart = __package.getPart(targetUri);
                mainPartContentType = metroPart.getContentType();
                break;
            }
        }
        Assert.that(hasMainPart, "main part not found");

        Map<URI, OpenXmlPart> loadedParts = new HashMap<>();
        loadReferencedPartsAndRelationships(this, (OpenXmlPart)null, relationshipCollection, loadedParts);
    }

    public PackagePart createMetroPart(URI partUri, String contentType) {
        return this.__package.createPart(partUri, contentType, this.compressionOption);
    }

    public void save() {
        savePartContents(true);
        this.__package.flush();
    }

    private void savePartContents(boolean save) {
        // TODO implement com.hyd.ooxml.packaging.OpenXmlPackage.savePartContents()
        boolean isAnyPartChanged = false;
        for (IdPartPair part : parts()) {
            if (part.openXmlPart.isRootElementLoaded()) {
                isAnyPartChanged = true;
                break;
            }
        }
        if (isAnyPartChanged) {
            for (IdPartPair part : parts()) {
                if (part.openXmlPart.isRootElementLoaded()) {
                    part.openXmlPart.getPartRootElement().save();
                }
            }
        }
    }

    @Override
    public void close() {
        // TODO implement com.hyd.ooxml.packaging.OpenXmlPackage.close()
    }

    @Override
    public OpenXmlPackage clone() {
        return clone(new MemoryStream(), true, new OpenSettings());
    }

    public OpenXmlPackage clone(Stream stream, boolean editable, OpenSettings openSettings) {
        Assert.not(stream == null, "stream cannot be null");
        if (openSettings == null) {
            openSettings = this.openSettings;
        }

        save();

        try (OpenXmlPackage clone = createClone(stream)) {
            for (IdPartPair pair : parts()) {
                clone.addPart(pair.openXmlPart, pair.relationshipId);
            }
        }

        return openClone(stream, editable, openSettings);
    }

    protected <T extends OpenXmlPart> void changeDocumentTypeInternal() {
        // TODO implement com.hyd.ooxml.packaging.OpenXmlPackage.changeDocumentTypeInternal()
    }

    public URI getUniquePartUri(String contentType, URI parentUri, URI targetUri) {
        URI partUri;
        do {
            partUri = partUriHelper.getUniquePartUri(contentType, parentUri, targetUri);
        } while (this.__package.partExists(partUri));
        return partUri;
    }

    @Override
    protected OpenXmlPackage getInternalOpenXmlPackage() {
        return this;
    }

    @Override
    protected OpenXmlPart getThisOpenXmlPart() {
        return null;
    }

    @Override
    protected PackageRelationship createRelationship(
        URI targetUri, TargetMode targetMode, String relationshipType) {
        return null;// TODO implement com.hyd.ooxml.packaging.OpenXmlPackage.createRelationship()
    }

    @Override
    protected PackageRelationship createRelationship(
        URI targetUri, TargetMode targetMode, String relationshipType, String id) {
        return null;// TODO implement com.hyd.ooxml.packaging.OpenXmlPackage.createRelationship()
    }

    public DataPart addDataPartToList(DataPart dataPart) {
        this.dataPartList.add(dataPart);
        return dataPart;
    }

    ///////////////////////////////////////////////////////////////////

    protected abstract OpenXmlPackage createClone(Stream stream);

    protected abstract OpenXmlPackage openClone(Stream stream, boolean isEditable, OpenSettings openSettings);

    protected abstract String getMainPartRelationshipType();
}
