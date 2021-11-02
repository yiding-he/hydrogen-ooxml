package com.hyd.ms.io.compression;

import com.hyd.ms.io.ByteArrayStream;
import com.hyd.ms.io.Stream;
import com.hyd.utilities.assertion.Assert;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public class ZipArchiveEntry {

    private final ZipArchive archive;

    private final String name;

    private final ByteArrayStream content = new ByteArrayStream();

    private final boolean directory;

    public ZipArchiveEntry(ZipArchive archive, String name, byte[] content) {
        Assert.notBlank(name, "name");
        Assert.not(name.endsWith("/"), "file entry cannot end with '/': %s", name);

        this.archive = archive;
        this.name = name;
        this.directory = false;

        setContent(content);
    }

    public ZipArchiveEntry(ZipArchive archive, String name) {
        this.archive = archive;
        this.name = StringUtils.appendIfMissing(name, "/");
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
        try {
            return IOUtils.toByteArray(content.read(), content.length());
        } catch (IOException e) {
            throw new ZipException(e);
        }
    }

    public void setContent(byte[] content) {
        try {
            this.content.write().write(content);
        } catch (IOException e) {
            throw new ZipException(e);
        }
    }

    public Stream getStream() {
        return null;
    }
}
