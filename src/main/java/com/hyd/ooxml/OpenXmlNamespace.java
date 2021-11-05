package com.hyd.ooxml;

public enum OpenXmlNamespace {

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
}
