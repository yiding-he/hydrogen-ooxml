package com.hyd.ooxml.packaging;

import com.hyd.ms.io.FileAccess;
import com.hyd.ms.io.FileMode;
import com.hyd.ms.io.FileShare;
import com.hyd.ms.io.Stream;
import com.hyd.ms.io.packaging.Package;

public class PackageLoader {

    public static PackageLoader createCore(String path) {
        return new PackageLoader(Package.open(path, FileMode.Create, FileAccess.ReadWrite, FileShare.None));
    }

    public static PackageLoader createCore(Stream stream) {
        return new PackageLoader(Package.open(stream, FileMode.Create, FileAccess.ReadWrite));
    }

    public static PackageLoader openCore(Stream stream, boolean readWriteMode) {
        FileAccess packageMode = readWriteMode ? FileAccess.ReadWrite : FileAccess.Read;
        FileMode packageAccess = readWriteMode ? FileMode.OpenOrCreate : FileMode.Open;
        return new PackageLoader(Package.open(stream, packageAccess, packageMode), true);
    }

    public static PackageLoader openCore(String path, boolean readWriteMode) {
        FileAccess packageMode = readWriteMode ? FileAccess.ReadWrite : FileAccess.Read;
        FileMode packageAccess = readWriteMode ? FileMode.OpenOrCreate : FileMode.Open;
        FileShare packageShare = readWriteMode ? FileShare.None : FileShare.Read;
        return new PackageLoader(Package.open(path, packageAccess, packageMode, packageShare), true);
    }

    ///////////////////////////////////////////////////////////////////

    private Package __package;

    private boolean open;

    public PackageLoader(Package __package) {
        this(__package, false);
    }

    public PackageLoader(Package __package, boolean isOpen) {
        this.__package = __package;
        this.open = isOpen;
    }

    public Package getPackage() {
        return __package;
    }

    public boolean isOpen() {
        return open;
    }
}
