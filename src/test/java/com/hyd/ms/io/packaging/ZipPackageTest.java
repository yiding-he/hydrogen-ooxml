package com.hyd.ms.io.packaging;

import com.hyd.ms.io.FileMode;
import com.hyd.utilities.assertion.AssertException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ZipPackageTest {

    @Test
    public void testModeNotAvailable() throws Exception {
        assertThrows(
            AssertException.class,
            () -> {
                new ZipPackage("target/1.zip", FileMode.Append, null);
            }
        );
    }

    @Test
    public void testCreateWithRelationship() throws Exception {
        String filePath = "target/relationships.zip";

        ZipPackage zipPackage = new ZipPackage(filePath);
        PackagePart part1 = zipPackage.createPart(URI.create("/1.txt"), "text/plain");
        part1.getStream().write().write("Hello1".getBytes(StandardCharsets.UTF_8));

        PackagePart part2 = zipPackage.createPart(URI.create("/2.txt"), "text/plain");
        part2.getStream().write().write("Hello2".getBytes(StandardCharsets.UTF_8));
        part1.createRelationship(part2.getUri().getUri(), TargetMode.Internal, "any");

        zipPackage.close();

        assertTrue(new File(filePath).exists());
        assertTrue(new File(filePath).length() > 0, "Archive should not be empty");
    }

    @Test
    public void testOpenFile() throws Exception {
        ZipPackage zipPackage = new ZipPackage("target/1.zip");
        zipPackage.close();
    }
}
