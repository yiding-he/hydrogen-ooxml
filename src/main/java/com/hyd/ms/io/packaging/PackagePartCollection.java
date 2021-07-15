package com.hyd.ms.io.packaging;

import java.util.TreeMap;

/**
 * Package part mappings exposed to api user
 */
public class PackagePartCollection {

    // Referencing Package.partList
    private final TreeMap<PackUriHelper.ValidatedPartUri, PackagePart> partList;

    public PackagePartCollection(TreeMap<PackUriHelper.ValidatedPartUri, PackagePart> partList) {
        this.partList = partList;
    }

    public boolean containsKey(PackUriHelper.ValidatedPartUri partUri) {
        return this.partList.containsKey(partUri);
    }
}
