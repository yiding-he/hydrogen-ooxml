package com.hyd.ms.io.compression;

import com.hyd.assertion.Assert;
import org.apache.commons.lang3.StringUtils;

public class ZipArchiveEntry {

    private final static byte[] EMPTY = new byte[0];

    private final ZipArchive archive;

    private final String name;

    private byte[] content;

    private final boolean directory;

    public ZipArchiveEntry(ZipArchive archive, String name, byte[] content) {
        Assert.notBlank(name, "name");
        Assert.not(name.endsWith("/"), "file entry cannot end with '/': %s", name);

        this.archive = archive;
        this.name = name;
        this.content = content;
        this.directory = false;
    }

    public ZipArchiveEntry(ZipArchive archive, String name) {
        this.archive = archive;
        this.name = StringUtils.appendIfMissing(name, "/");
        this.content = EMPTY;
        this.directory = true;
    }

    public boolean isDirectory() {
        return directory;
    }

    public ZipArchive getArchive() {
        return archive;
    }

    public String getName() {
        return name;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
