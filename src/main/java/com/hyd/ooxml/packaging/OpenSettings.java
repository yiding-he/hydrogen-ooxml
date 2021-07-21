package com.hyd.ooxml.packaging;

import java.util.function.Function;

public class OpenSettings {

    private boolean autoSave;

    private long maxCharactersInPart;

    private Function<OpenXmlPackage, RelationshipErrorHandler> relationshipErrorHandlerFactory;

    private MarkupCompatibilityProcessSettings mcSettings =
        new MarkupCompatibilityProcessSettings(
            MarkupCompatibilityProcessMode.NoProcess, FileFormatVersions.Office2007
        );

    public OpenSettings(OpenSettings other) {
        this.autoSave = other.autoSave;
        this.mcSettings.setProcessMode(other.mcSettings.getProcessMode());
        this.mcSettings.setTargetFileFormatVersions(other.mcSettings.getTargetFileFormatVersions());
        this.maxCharactersInPart = other.maxCharactersInPart;
        this.relationshipErrorHandlerFactory = other.relationshipErrorHandlerFactory;
    }

    public boolean isAutoSave() {
        return autoSave;
    }

    public long getMaxCharactersInPart() {
        return maxCharactersInPart;
    }

    public Function<OpenXmlPackage, RelationshipErrorHandler> getRelationshipErrorHandlerFactory() {
        return relationshipErrorHandlerFactory;
    }
}
