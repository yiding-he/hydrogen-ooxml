package com.hyd.ooxml.framework;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PartConstraintRule {

    private final PartTypeInfo info;

    private final boolean minOccursIsNonZero;

    private final boolean maxOccursGreaterThanOne;

    public boolean matchContentType(String contentType) {
        // TODO we may need xml validation here
        return true;
    }
}
