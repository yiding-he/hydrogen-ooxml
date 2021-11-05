package com.hyd.ooxml.framework.metadata;

import com.hyd.ooxml.OpenXmlNamespace;
import lombok.Data;

@Data
public class AttributeMetadata {

    private OpenXmlNamespace namespace;

    private String name;

    private String value;

    public AttributeMetadata(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public AttributeMetadata(OpenXmlNamespace namespace, String name, String value) {
        this.namespace = namespace;
        this.name = name;
        this.value = value;
    }
}
