package com.hyd.xml;

import org.junit.jupiter.api.Test;

class XsdToJavaTest {

    @Test
    public void testGenerateCode() throws Exception {
        XsdToJava xsdToJava = new XsdToJava("src/test/resources/pml.xsd");
        xsdToJava.generate();
    }
}
