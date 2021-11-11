package com.hyd.ooxml.packaging;

import com.hyd.ms.io.MemoryStream;
import com.hyd.ms.io.Stream;
import com.hyd.ms.io.packaging.Package;
import com.hyd.ms.io.packaging.*;
import com.hyd.ooxml.ApplicationType;
import com.hyd.utilities.assertion.Assert;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
public abstract class OpenXmlPackage extends OpenXmlPartContainer implements Closeable {

    protected Package __package;

    protected String mainPartContentType;

    protected OpenSettings openSettings;

    private boolean disposed;

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

    public OpenXmlPart getRootPart() {
        throw new UnsupportedOperationException("not implemented by " + getClass());
    }

    private void load(Package __package) {
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
        loadReferencedPartsAndRelationships(this, null, relationshipCollection, loadedParts);
    }

    public PackagePart createMetroPart(URI partUri, String contentType) {
        return this.__package.createPart(partUri, contentType, this.compressionOption);
    }

    public void save() {
        savePartContents(true);
        this.__package.flush();
    }

    private void savePartContents(boolean save) {

        List<OpenXmlPart> partsList = StreamSupport
            .stream(getAllParts().spliterator(), false).collect(Collectors.toList());

        log.debug("all parts count: {}", partsList.size());

        boolean isAnyPartChanged = false;
        for (OpenXmlPart part : partsList) {
            if (part.isRootElementLoaded()) {
                isAnyPartChanged = true;
                break;
            }
        }
        if (isAnyPartChanged) {
            for (OpenXmlPart part : partsList) {
                log.debug("part: {}, rootElementLoaded: {}", part.getClass().getSimpleName(), part.isRootElementLoaded());
                if (part.isRootElementLoaded()) {
                    part.getPartRootElement().save();
                }
            }
        }
    }

    @Override
    public void close() {
        dispose();
    }

    public void dispose() {
        dispose(true);
    }

    protected void dispose(boolean disposing) {
        if (disposed) {
            return;
        }
        if (disposing) {
            savePartContents(openSettings.isAutoSave());
            deleteUnusedDataPartOnClose();
            this.__package.close();
            this.__package = null;
            this.childrenPartsDictionary.clear();
            this.referenceRelationships.clear();
            this.partUriHelper = null;
        }
        disposed = true;
    }

    public Iterable<OpenXmlPart> getAllParts() {

        return () -> {
            Set<OpenXmlPart> visited = new HashSet<>();
            Deque<OpenXmlPart> queue = new LinkedList<>();

            parts().forEach(pair -> queue.offer(pair.openXmlPart));
            return new Iterator<OpenXmlPart>() {

                OpenXmlPart current;

                @Override
                public boolean hasNext() {
                    if (queue.isEmpty()) {
                        return false;
                    }

                    current = queue.poll();
                    current.parts().forEach(pair -> {
                        if (visited.add(pair.openXmlPart)) {
                            queue.offer(pair.openXmlPart);
                        }
                    });
                    return true;
                }

                @Override
                public OpenXmlPart next() {
                    return current;
                }
            };
        };
    }

    private void deleteUnusedDataPartOnClose() {

        Set<DataPart> dataPartSet = new HashSet<>(dataPartList);
        for (DataPartReferenceRelationship r : getDataPartReferenceRelationships()) {
            dataPartSet.remove(r.getDataPart());
            if (dataPartSet.isEmpty()) {
                // No more DataPart in the set. All DataParts are referenced somewhere.
                return;
            }
        }

        for (OpenXmlPart part : getAllParts()) {
            for (DataPartReferenceRelationship r : part.getDataPartReferenceRelationships()) {
                dataPartSet.remove(r.getDataPart());
                if (dataPartSet.isEmpty()) {
                    // No more DataPart in the set. All DataParts are referenced somewhere.
                    return;
                }
            }
        }

        for (DataPart dataPart : dataPartSet) {
            dataPart.destroy();
            dataPartList.remove(dataPart);
        }
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
        throw new UnsupportedOperationException("Not implemented yet");
        // TODO implement com.hyd.ooxml.packaging.OpenXmlPackage.changeDocumentTypeInternal()
    }

    public URI getUniquePartUri(
        String contentType, URI parentUri, String targetPath, String targetName, String targetExt
    ) {
        URI partUri;
        do {
            partUri = partUriHelper.getUniquePartUri(contentType, parentUri, targetPath, targetName, targetExt);
        } while (this.__package.partExists(partUri));
        return partUri;
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
        return __package.createRelationship(targetUri, targetMode, relationshipType);
    }

    @Override
    protected PackageRelationship createRelationship(
        URI targetUri, TargetMode targetMode, String relationshipType, String id) {
        throw new UnsupportedOperationException("Not implemented yet");
        // TODO implement com.hyd.ooxml.packaging.OpenXmlPackage.createRelationship()
    }

    public DataPart addDataPartToList(DataPart dataPart) {
        this.dataPartList.add(dataPart);
        return dataPart;
    }

    public ApplicationType getApplicationType() {
        return ApplicationType.None;
    }

    public DataPart findDataPart(URI uri) {
        return dataPartList.stream()
            .filter(p -> p.getUri().equals(uri))
            .findFirst().orElse(null);
    }

    public void reserveUri(String contentType, URI partUri) {
        this.partUriHelper.reserveUri(contentType, partUri);
    }

    ///////////////////////////////////////////////////////////////////

    protected abstract OpenXmlPackage createClone(Stream stream);

    protected abstract OpenXmlPackage openClone(Stream stream, boolean isEditable, OpenSettings openSettings);

    protected abstract String getMainPartRelationshipType();
}
