package com.hyd.ooxml.packaging;

import com.hyd.ms.io.packaging.PackUriHelper;
import com.hyd.utilities.Uris;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class PartUriHelper {

    /**
     * List of contentTypes that need to have a '1' appended to the name for the first item in the package.
     * Section numbers in comments refer to the ISO/IEC 29500 standard.
     */
    private static final HashSet<String> NUMBERED_CONTENT_TYPES = new HashSet<>();

    static {
        NUMBERED_CONTENT_TYPES.addAll(Arrays.asList(
            //11.3 WordprocessingML Parts
            "application/vnd.openxmlformats-officedocument.wordprocessingml.footer+xml",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.header+xml",

            //12.3 SpreadsheetML Parts
            "application/vnd.openxmlformats-officedocument.spreadsheetml.chartsheet+xml",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.comments+xml",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.dialogsheet+xml",
            "application/vnd.openxmlformats-officedocument.drawing+xml",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.externalLink+xml",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.dialogsheet+xml",
            "application/vnd.openxmlformats-officedocument.drawing+xml",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.externalLink+xml",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheetMetadata+xml",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.pivotCacheDefinition+xml",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.pivotCacheRecords+xml",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.queryTable+xml",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.revisionLog+xml",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.tableSingleCells+xml",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.table+xml",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml",

            //13.3 PresentationML Parts
            "application/vnd.openxmlformats-officedocument.presentationml.comments+xml",
            "application/vnd.openxmlformats-officedocument.presentationml.handoutMaster+xml",
            "application/vnd.openxmlformats-officedocument.presentationml.notesMaster+xml",
            "application/vnd.openxmlformats-officedocument.presentationml.notesSlide+xml",
            "application/vnd.openxmlformats-officedocument.presentationml.slide+xml",
            "application/vnd.openxmlformats-officedocument.presentationml.slideLayout+xml",
            "application/vnd.openxmlformats-officedocument.presentationml.slideMaster+xml",
            "application/vnd.openxmlformats-officedocument.presentationml.slideUpdateInfo+xml",
            "application/vnd.openxmlformats-officedocument.presentationml.tags+xml",

            //14.2 DrawingML Parts
            "application/vnd.openxmlformats-officedocument.drawingml.chart+xml",
            "application/vnd.openxmlformats-officedocument.drawingml.chartshapes+xml",
            "application/vnd.openxmlformats-officedocument.drawingml.diagramColors+xml",
            "application/vnd.openxmlformats-officedocument.drawingml.diagramData+xml",
            "application/vnd.openxmlformats-officedocument.drawingml.diagramLayout+xml",
            "application/vnd.openxmlformats-officedocument.drawingml.diagramStyle+xml",
            "application/vnd.openxmlformats-officedocument.theme+xml",
            "application/vnd.openxmlformats-officedocument.themeOverride+xml",

            //15.2 Shared Parts
            "application/vnd.openxmlformats-officedocument.customXmlProperties+xml",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.printerSettings",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.printerSettings",
            "application/vnd.openxmlformats-officedocument.presentationml.printerSettings"
        ));
    }

    private final Map<String, Integer> sequenceNumbers = new HashMap<>();

    private final Map<URI, Integer> reservedUri = new HashMap<>();

    public URI getUniquePartUri(String contentType, URI parentUri, URI targetUri) {
        return getUniquePartUri(
            contentType, PackUriHelper.resolvePartUri(parentUri, targetUri),".",
            Uris.getBaseName(targetUri.toString()), Uris.getExtension(targetUri.toString())
        );
    }

    public URI getUniquePartUri(
        String contentType, URI parentUri, String targetPath, String targetName, String targetExt
    ) {
        URI partUri;
        do {
            String sequenceNumber = getNextSequenceNumber(contentType);
            String path = Uris.combine(targetPath, (targetName + sequenceNumber + "." + targetExt));
            partUri = PackUriHelper.resolvePartUri(parentUri, PackUriHelper.uri(path));
        } while (reservedUri.containsKey(partUri));

        reservedUri.put(partUri, 0);
        return partUri;
    }

    private String getNextSequenceNumber(String contentType) {
        boolean first = !this.sequenceNumbers.containsKey(contentType);
        int count = this.sequenceNumbers.computeIfAbsent(contentType, __ -> 0) + 1;
        this.sequenceNumbers.put(contentType, count);

        if (first && !NUMBERED_CONTENT_TYPES.contains(contentType)) {
            return "";
        } else {
            return String.valueOf(count);
        }
    }

    public void reserveUri(String contentType, URI partUri) {
        getNextSequenceNumber(contentType);
        addToReserveUri(PackUriHelper.validatePartUri(partUri).getUri());
    }

    private void addToReserveUri(URI uri) {
        this.reservedUri.put(uri, 0);
    }
}
