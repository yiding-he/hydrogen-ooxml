package com.hyd.utilities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UrisTest {

    @Test
    public void testGetBaseName() throws Exception {
        assertNull(Uris.getBaseName(null));
        assertEquals("", Uris.getBaseName(""));
        assertEquals("", Uris.getBaseName("/"));
        assertEquals("a", Uris.getBaseName("a"));
        assertEquals("a", Uris.getBaseName("/a"));
        assertEquals("b", Uris.getBaseName("/a/b"));
        assertEquals("c", Uris.getBaseName("/a/b/c"));
        assertEquals("c", Uris.getBaseName("/a/b/c.txt"));
        assertEquals("c", Uris.getBaseName("/a/b/c."));
        assertEquals("c", Uris.getBaseName("/a./b.c/c."));
    }
}
