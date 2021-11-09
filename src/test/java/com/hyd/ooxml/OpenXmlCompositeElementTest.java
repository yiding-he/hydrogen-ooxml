package com.hyd.ooxml;

import com.hyd.ms.io.MemoryStream;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OpenXmlCompositeElementTest {

    @XmlElement(localName = "e")
    private static class E extends OpenXmlCompositeElement {

    }

    @XmlElement(localName = "r")
    private static class R extends OpenXmlPartRootElement {

    }

    @Test
    public void testCreateXml() throws Exception {
        MemoryStream stream = new MemoryStream();

        E e1 = new E();
        E e2 = new E();
        E e3 = new E();

        e1.setAttribute("id", "e1");
        e2.setAttribute("id", "e2");
        e3.setAttribute("id", "e3");
        e1.append(e3);

        R r = new R();
        r.append(e1, e2);

        r.writeTo(stream);
        assertEquals(
            "<?xml version=\"1.0\" encoding=\"utf-8\"?><r><e id=\"e1\"><e id=\"e3\" /></e><e id=\"e2\" /></r>",
            stream.toString()
        );
    }
}
