package com.hyd.ms.io.compression;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ZipArchiveTest {

    @Test
    public void testCreate() throws Exception {
        String entryContent = "Hello!";
        String entryName = "1/1/1/1/1.txt";

        ZipArchive zipArchive = new ZipArchive();
        zipArchive.createDirectoryEntry("1");
        zipArchive.createDirectoryEntry("1/1");
        zipArchive.createDirectoryEntry("1/1/1");
        zipArchive.createEntry(entryName).setContent(entryContent.getBytes());
        outputEntries("zipArchive", zipArchive);

        zipArchive.dispose(Files.newOutputStream(Paths.get("target/1.zip")));

        ZipArchive read = new ZipArchive(Files.newInputStream(Paths.get("target/1.zip")));
        outputEntries("read", read);

        ZipArchiveEntry entry = read.getEntry(entryName);
        assertNotNull(entry);
        assertArrayEquals(entryContent.getBytes(), entry.getContent());
    }

    private void outputEntries(String name, ZipArchive zipArchive) {
        System.out.println("[Entries of " + name + "]");
        zipArchive.entries().forEachRemaining(
            entry -> System.out.println(
                entry.getName() + " : " + (entry.isDirectory() ? "(DIR)" : entry.getContent().length)
            )
        );
    }

    @Test
    public void testRead() throws Exception {
        ZipArchive zipArchive = new ZipArchive(
            ZipArchiveTest.class.getResourceAsStream("/javax.annotation-api-1.3.2.jar")
        );

        zipArchive.entries().forEachRemaining(entry -> {
            System.out.println(entry.getName() + " : " + entry.getContent().length);
        });
    }
}
