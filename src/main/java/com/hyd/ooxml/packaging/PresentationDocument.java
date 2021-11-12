package com.hyd.ooxml.packaging;

import com.hyd.ms.io.Stream;
import com.hyd.ooxml.PresentationDocumentType;
import com.hyd.ooxml.generated.packaging.PresentationPart;
import com.hyd.utilities.Uris;
import com.hyd.utilities.assertion.Assert;

import java.util.Map;
import java.util.TreeMap;

public class PresentationDocument extends OpenXmlPackage {

    private static final Map<PresentationDocumentType, String> MAIN_PART_CONTENT_TYPES = new TreeMap<>();

    static {
        MAIN_PART_CONTENT_TYPES.put(PresentationDocumentType.Presentation, "application/vnd.openxmlformats-officedocument.presentationml.presentation.main+xml");
        MAIN_PART_CONTENT_TYPES.put(PresentationDocumentType.Template, "application/vnd.openxmlformats-officedocument.presentationml.template.main+xml");
        MAIN_PART_CONTENT_TYPES.put(PresentationDocumentType.Slideshow, "application/vnd.openxmlformats-officedocument.presentationml.slideshow.main+xml");
        MAIN_PART_CONTENT_TYPES.put(PresentationDocumentType.MacroEnabledPresentation, "application/vnd.ms-powerpoint.presentation.macroEnabled.main+xml");
        MAIN_PART_CONTENT_TYPES.put(PresentationDocumentType.MacroEnabledTemplate, "application/vnd.ms-powerpoint.template.macroEnabled.main+xml");
        MAIN_PART_CONTENT_TYPES.put(PresentationDocumentType.MacroEnabledSlideshow, "application/vnd.ms-powerpoint.slideshow.macroEnabled.main+xml");
        MAIN_PART_CONTENT_TYPES.put(PresentationDocumentType.AddIn, "application/vnd.ms-powerpoint.addin.macroEnabled.main+xml");
    }

    public static PresentationDocument create(String path, PresentationDocumentType type) {
        return create(path, type, true);
    }

    public static PresentationDocument create(String path, PresentationDocumentType type, boolean autoSave) {
        return create(PackageLoader.createCore(path), type, autoSave);
    }

    public static PresentationDocument create(Stream stream, PresentationDocumentType type, boolean autoSave) {
        return create(PackageLoader.createCore(stream), type, autoSave);
    }

    public static PresentationDocument create(PackageLoader packageLoader, PresentationDocumentType type, boolean autoSave) {
        PresentationDocument document = new PresentationDocument(packageLoader, new OpenSettings(autoSave));
        document.documentType = type;
        document.mainPartContentType = MAIN_PART_CONTENT_TYPES.get(type);
        return document;
    }

    public static PresentationDocument createFromTemplate(String path) {
        Assert.notBlank(path, "path");

        String extension = Uris.getExtension(path);
        Assert.that(
            extension.equalsIgnoreCase("pptx") || extension.equalsIgnoreCase("pptm") ||
                extension.equalsIgnoreCase("potx") || extension.equalsIgnoreCase("potm"),
            "Illegal template file: %s", path
        );

        try (PresentationDocument template = PresentationDocument.open(path, false)) {

            // We've opened the template in read-only mode to let multiple processes or
            // threads open it without running into problems.
            PresentationDocument document = (PresentationDocument)template.clone();

            // If the template is a document rather than a template, we are done.
            if (extension.equals(".xlsx") || extension.equals(".xlsm"))
            {
                return document;
            }

            // Otherwise, we'll have to do some more work.
            document.changeDocumentType(PresentationDocumentType.Presentation);

            // We are done, so save and return.
            // TODO: Check whether it would be safe to return without saving.
            document.save();
            return document;
        }
    }

    public static PresentationDocument open(String path) {
        return open(path, false);
    }

    public static PresentationDocument open(String path, boolean isEditable) {
        return open(path, isEditable, new OpenSettings());
    }

    public static PresentationDocument open(String path, boolean isEditable, OpenSettings openSettings) {
        PresentationDocument doc = new PresentationDocument(PackageLoader.openCore(path, isEditable), openSettings);
        doc.updateDocumentTypeFromContentType();
        return doc;
    }

    public static PresentationDocument open(Stream stream, boolean isEditable, OpenSettings openSettings) {
        PresentationDocument doc = new PresentationDocument(PackageLoader.openCore(stream, isEditable), openSettings);
        doc.updateDocumentTypeFromContentType();
        return doc;
    }

    @Override
    public OpenXmlPart getRootPart() {
        return getSubPartOfType(PresentationPart.class);
    }

    ///////////////////////////////////////////////////////////////////

    private void updateDocumentTypeFromContentType() {
        // TODO implement com.hyd.ooxml.packaging.PresentationDocument.updateDocumentTypeFromContentType()
    }

    private void changeDocumentType(PresentationDocumentType newType) {
        // TODO implement com.hyd.ooxml.packaging.PresentationDocument.changeDocumentType()
        if (newType == this.documentType) {
            return;
        }

        PresentationDocumentType oldType = this.documentType;
        this.documentType = newType;

    }

    @Override
    protected OpenXmlPackage createClone(Stream stream) {
        return create(stream, this.documentType, this.openSettings.isAutoSave());
    }

    @Override
    protected OpenXmlPackage openClone(Stream stream, boolean isEditable, OpenSettings openSettings) {
        return open(stream, isEditable, openSettings);
    }

    @Override
    protected String getMainPartRelationshipType() {
        return PresentationPart.class.getAnnotation(RelationshipType.class).value();
    }

    ///////////////////////////////////////////////////////////////////

    private PresentationDocumentType documentType;

    protected PresentationDocument() {
    }

    protected PresentationDocument(PackageLoader loader, OpenSettings settings) {
        super(loader, settings);
    }


    public PresentationPart addPresentationPart() {
        PresentationPart childPart = new PresentationPart();
        initPart(childPart, this.mainPartContentType);
        return childPart;
    }

    private void initPart(PresentationPart part, String contentType) {
        initPart(part, contentType, null);
    }
}
