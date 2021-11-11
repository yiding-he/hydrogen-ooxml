package com.hyd.ms.io.packaging;


import org.dom4j.Element;

public enum TargetMode {

    Internal, External;

    public static TargetMode fromRelationship(Element relElement) {
        String value = relElement.attributeValue("TargetMode");
        return value == null || value.isEmpty() ? Internal : valueOf(value);
    }
}
