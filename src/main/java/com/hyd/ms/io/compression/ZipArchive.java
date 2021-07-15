package com.hyd.ms.io.compression;

import com.hyd.ms.io.IoException;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipArchive {

    private final Map<String, ZipArchiveEntry> entriesDictionary = new HashMap<>();

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
            if (nextEntry.isDirectory()) {
                continue;
            }

            String entryName = nextEntry.getName();
            byte[] bytes = IOUtils.toByteArray(zipInputStream);
            ZipArchiveEntry content = new ZipArchiveEntry(entryName, bytes);

            this.entriesDictionary.put(entryName, content);
        }
    }
}
