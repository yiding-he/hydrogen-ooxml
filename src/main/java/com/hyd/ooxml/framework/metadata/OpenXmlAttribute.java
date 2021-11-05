package com.hyd.ooxml.framework.metadata;

import com.hyd.ooxml.OpenXmlNamespace;
import lombok.Data;

@Data
public class OpenXmlAttribute {

    private OpenXmlNamespace namespace;

    private String name;

    private String value;

    public OpenXmlAttribute(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public OpenXmlAttribute(OpenXmlNamespace namespace, String name, String value) {
        this.namespace = namespace;
        this.name = name;
        this.value = value;
    }

    public String getNameWithPrefix() {
        String prefix = this.namespace == null ? null : this.namespace.getPrefix();
        return prefix == null ? name : (prefix + ":" + name);
    }
}
