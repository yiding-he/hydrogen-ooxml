package com.hyd.ooxml;

import org.apache.commons.lang3.StringUtils;

public class E extends OpenXmlCompositeElement {

    private final String tagName;

    private final String prefix;

    private final OpenXmlNamespace[] namespaces;

    public E(String prefixAndName, OpenXmlNamespace... namespaces) {
        this.prefix = StringUtils.substringBefore(prefixAndName, ":");
        this.tagName = StringUtils.substringAfter(prefixAndName, ":");
        this.namespaces = namespaces;
    }

    public E(String tagName, String prefix, OpenXmlNamespace... namespaces) {
        this.tagName = tagName;
        this.prefix = prefix;
        this.namespaces = namespaces;
    }

    @Override
    protected String getXmlTagName() {
        return prefix == null ? tagName : (prefix + ":" + tagName);
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public OpenXmlNamespace[] getNamespaces() {
        return namespaces;
    }

    public E attr(String name, String value) {
        setAttribute(name, value);
        return this;
    }

    public E children(OpenXmlElement... children) {
        append(children);
        return this;
    }
}
