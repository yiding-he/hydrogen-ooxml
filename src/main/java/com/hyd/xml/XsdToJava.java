package com.hyd.xml;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.dom4j.Document;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class XsdToJava {

    private final String filePath;

    @Data
    @AllArgsConstructor
    private static class TypeDef {

        private String tagName;

        private String typeName;

    }

    public XsdToJava(String filePath) {
        this.filePath = filePath;
    }

    public void generate() throws Exception {
        Document document = Xml.parseDocumentAndClose(Files.newInputStream(Paths.get(filePath)));
        Map<String, TypeDef> types = new HashMap<>();


    }
}
