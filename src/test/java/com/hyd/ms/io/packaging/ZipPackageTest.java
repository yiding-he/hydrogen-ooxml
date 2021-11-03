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
    public void testCreateEmpty() throws Exception {
        String filePath = "target/" + System.currentTimeMillis() + ".zip";

        ZipPackage zipPackage = new ZipPackage(filePath);
        PackagePart part = zipPackage.createPart(new URI("/1.txt"), "text/plain");
        part.getStream().write().write("Hello".getBytes(StandardCharsets.UTF_8));
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
