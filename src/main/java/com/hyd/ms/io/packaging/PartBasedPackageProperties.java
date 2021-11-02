package com.hyd.ms.io.packaging;

import com.hyd.ms.io.packaging.PackUriHelper.ValidatedPartUri;

import java.io.IOException;
import java.util.UUID;

public class PartBasedPackageProperties extends PackageProperties {

    public static final String CORE_DOCUMENT_PROPERTIES_RELATIONSHIP_TYPE
        = "http://schemas.openxmlformats.org/package/2006/relationships/metadata/core-properties";

    public static final String DEFAULT_PROPERTY_PART_NAME_PREFIX =
        "/package/services/metadata/core-properties/";

    public static final String DEFAULT_PROPERTY_PART_NAME_EXTENSION = ".psmdcp";

    public static final ContentType CORE_DOCUMENT_PROPERTIES_CONTENT_TYPE
        = new ContentType("application/vnd.openxmlformats-package.core-properties+xml");

    private final Package __package;

    private PackagePart propertyPart;

    private boolean dirty;

    public PartBasedPackageProperties(Package __package) {
        this.__package = __package;
    }

    @Override
    public void close() throws IOException {
        flush();
    }

    private void flush() {
        if (!dirty) {
            return;
        }

        if (this.propertyPart == null) {
            String propertyPartName = DEFAULT_PROPERTY_PART_NAME_PREFIX
                + UUID.randomUUID().toString().replace("-", "")
                + DEFAULT_PROPERTY_PART_NAME_EXTENSION;

            ValidatedPartUri partUri = PackUriHelper.validatePartUri(PackUriHelper.uri(propertyPartName));
            this.propertyPart = this.__package.createPart(
                partUri.getUri(), CORE_DOCUMENT_PROPERTIES_CONTENT_TYPE.toString()
            );
        }

        this.propertyPart.isDeleted();
        // TODO Write XML content to this part
    }
}
