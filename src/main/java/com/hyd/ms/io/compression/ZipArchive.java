package com.hyd.ms.io.compression;

import com.hyd.ms.io.IoException;
import com.hyd.utilities.assertion.Assert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Slf4j
public class ZipArchive {

    private final Map<String, ZipArchiveEntry> entriesDictionary = new HashMap<>();

    public ZipArchive() {

    }

    public ZipArchive(InputStream is) {
        try {
            readInputStream(is);
        } catch (IOException e) {
            throw new IoException(e);
        }
    }

    private void readInputStream(InputStream is) throws IOException {
        ZipInputStream zipInputStream = new ZipInputStream(is);
        ZipEntry nextEntry;

        while ((nextEntry = zipInputStream.getNextEntry()) != null) {
            String entryName = nextEntry.getName();
            ZipArchiveEntry entry;

            if (nextEntry.isDirectory()) {
                entry = new ZipArchiveEntry(this, entryName);
            } else {
                byte[] bytes = IOUtils.toByteArray(zipInputStream);
                entry = new ZipArchiveEntry(this, entryName, bytes);
            }

            this.entriesDictionary.put(entryName, entry);
        }
    }

    /**
     * write to output stream
     */
    public void write(OutputStream os) {
        try (ZipOutputStream zos = new ZipOutputStream(os)) {
            entriesDictionary.forEach((name, entry) -> {
                try {
                    zos.putNextEntry(new ZipEntry(entry.getName()));
                    if (!entry.isDirectory()) {
                        zos.write(entry.getContent());
                        zos.closeEntry();
                    }
                } catch (IOException e) {
                    throw new ZipException(e);
                }
            });
        } catch (IOException e) {
            throw new ZipException(e);
        }
    }

    /**
     * clear content
     */
    public void dispose() {
        dispose(null);
    }

    /**
     * write to output stream and clear content
     */
    public void dispose(OutputStream os) {
        if (os != null) {
            write(os);
        }
        this.entriesDictionary.clear();
    }

    /**
     * get an iterator of entries
     */
    public Iterator<ZipArchiveEntry> entries() {
        return this.entriesDictionary.values().iterator();
    }

    /**
     * create a new file entry
     */
    public ZipArchiveEntry createEntry(String entryName) {
        Assert.notBlank(entryName, "entryName");
        ZipArchiveEntry entry = new ZipArchiveEntry(this, entryName, new byte[0]);
        this.entriesDictionary.put(entryName, entry);
        log.debug("zip entry '{}' created", entryName);
        return entry;
    }

    /**
     * get or create a file entry
     */
    public ZipArchiveEntry getOrCreateEntry(String entryName) {
        Assert.notBlank(entryName, "entryName");
        ZipArchiveEntry entry = getEntry(entryName);
        if (entry != null) {
            return entry;
        } else {
            return createEntry(entryName);
        }
    }

    /**
     * create a directory entry
     */
    public void createDirectoryEntry(String entryName) {
        Assert.notBlank(entryName, "entryName");

        entryName = StringUtils.appendIfMissing(entryName, "/");
        ZipArchiveEntry entry = new ZipArchiveEntry(this, entryName);
        this.entriesDictionary.put(entryName, entry);
    }

    /**
     * fetch an entry by name
     */
    public ZipArchiveEntry getEntry(String entryName) {
        return this.entriesDictionary.get(entryName);
    }
}
