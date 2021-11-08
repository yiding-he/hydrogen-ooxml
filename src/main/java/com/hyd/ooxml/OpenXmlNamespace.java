package com.hyd.ooxml;

import java.util.stream.Stream;

public enum OpenXmlNamespace {

    Relationship("r", "http://schemas.openxmlformats.org/officeDocument/2006/relationships"),
    Presentation("p", "http://schemas.openxmlformats.org/presentationml/2006/main"),
    DrawingML("a", "http://schemas.openxmlformats.org/drawingml/2006/main")

    ;

    private final String prefix;

    private final String uri;

    OpenXmlNamespace(String prefix, String uri) {
        this.prefix = prefix;
        this.uri = uri;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getUri() {
        return uri;
    }

    public static OpenXmlNamespace fromPrefix(String prefix) {
        return Stream.of(values())
            .filter(n -> n.prefix.equals(prefix)).findFirst()
            .orElseThrow(() -> new IllegalArgumentException("prefix '" + prefix + "' is undefined"));
    }
}
