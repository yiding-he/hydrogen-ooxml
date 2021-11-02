package com.hyd.ms.io.packaging;

import com.hyd.ms.io.Stream;
import com.hyd.ms.io.compression.ZipArchive;
import com.hyd.ms.io.compression.ZipArchiveEntry;
import com.hyd.ms.io.packaging.PackUriHelper.ValidatedPartUri;

public class ZipPackagePart extends PackagePart {

    // package which belongs to
    private final ZipPackage zipPackage;

    // archive which belongs to
    private final ZipArchive zipArchive;

    // entry content
    private final ZipArchiveEntry zipArchiveEntry;

    protected ZipPackagePart(
        ZipPackage zipPackage, ZipArchive zipArchive, ZipArchiveEntry zipArchiveEntry,
        ValidatedPartUri partUri, String contentType
    ) {
        super(zipPackage, partUri, contentType);
        this.zipPackage = zipPackage;
        this.zipArchive = zipArchive;
        this.zipArchiveEntry = zipArchiveEntry;
    }

    @Override
    protected Stream getStreamCore() {
        return this.zipArchiveEntry.getStream();
    }
}
