package com.hyd.ms.io.packaging;

import org.w3c.dom.Element;

public enum TargetMode {

    Internal, External;

    public static TargetMode fromRelationship(Element relElement) {
        String value = relElement.getAttribute("TargetMode");
        return value == null || value.isEmpty() ? Internal : valueOf(value);
    }
}
