package com.hyd.ooxml.packaging;

import com.hyd.ms.io.MemoryStream;
import com.hyd.ms.io.Stream;
import com.hyd.ms.io.packaging.Package;
import com.hyd.ms.io.packaging.*;
import com.hyd.utilities.assertion.Assert;

import java.io.Closeable;
import java.net.URI;
import java.util.LinkedList;

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
    }

    public PackagePart createMetroPart(URI partUri, String contentType) {
        return this.__package.createPart(partUri, contentType, this.compressionOption);
    }

    public void save() {
        // TODO implement com.hyd.ooxml.packaging.OpenXmlPackage.save()
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
}
