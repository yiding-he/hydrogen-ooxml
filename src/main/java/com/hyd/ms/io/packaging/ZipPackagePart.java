package com.hyd.ms.io.packaging;

import com.hyd.ms.io.compression.ZipArchive;
import com.hyd.ms.io.compression.ZipArchiveEntry;
import com.hyd.ms.io.packaging.PackUriHelper.ValidatedPartUri;

public class ZipPackagePart extends PackagePart {

    private final ZipPackage zipPackage;

    private final ZipArchive zipArchive;

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
}
