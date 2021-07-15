package com.hyd.ms.io.packaging;

import com.hyd.ms.io.FileAccess;
import com.hyd.ms.io.FileMode;
import com.hyd.ms.io.FileShare;
import com.hyd.ms.io.IoException;
import com.hyd.ms.io.compression.ZipArchive;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * In memory zip package struct
 */
public class ZipPackage extends Package {

    private ZipArchive zipArchive;

    /**
     * Constructor
     *
     * @param path              intended to be used in FileStream
     * @param packageFileMode   intended to be used in FileStream
     * @param packageFileAccess intended to be used in {@link ZipArchive}
     * @param share             intended to be used in FileStream
     */
    public ZipPackage(String path, FileMode packageFileMode, FileAccess packageFileAccess, FileShare share) {
        super(packageFileAccess);

        try (InputStream is = Files.newInputStream(Paths.get(path))) {
            this.zipArchive = new ZipArchive(is);
        } catch (IOException e) {
            throw new IoException(e);
        }
    }

    /**
     * Constructor
     *
     * @param is                content stream
     * @param packageFileMode   intended to be used in FileStream
     * @param packageFileAccess intended to be used in {@link ZipArchive}
     */
    public ZipPackage(InputStream is, FileMode packageFileMode, FileAccess packageFileAccess) {
        super(packageFileAccess);
        this.zipArchive = new ZipArchive(is);
    }

    @Override
    public void close() throws IOException {
        // nothing to do
    }

    ///////////////////////////////////////////////////////////////////


    @Override
    protected PackagePart createPartCore(
        URI partUri, String contentType, CompressionOption compressionOption) {
        return null;
    }

    @Override
    protected PackagePart getPartCore(URI partUri) {
        return null;
    }

    @Override
    protected void deletePartCore(URI partUri) {

    }

    @Override
    protected PackagePart[] getPartsCore() {
        return new PackagePart[0];
    }
}
