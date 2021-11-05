package com.hyd.ooxml;

import com.hyd.ms.io.MemoryStream;
import org.junit.jupiter.api.Test;

class OpenXmlPartRootElementTest {

    public static class A extends OpenXmlPartRootElement {

    }

    @Test
    void save() {
        MemoryStream memoryStream = new MemoryStream();
        A a = new A();
        a.save();
    }
}
