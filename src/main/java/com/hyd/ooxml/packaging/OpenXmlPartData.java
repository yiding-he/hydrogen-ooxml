package com.hyd.ooxml.packaging;

import com.hyd.ooxml.framework.PartConstraintCollection;

public class OpenXmlPartData {

    private final Class<? extends OpenXmlPartContainer> containerType;

    public OpenXmlPartData(Class<? extends OpenXmlPartContainer> containerType) {
        this.containerType = containerType;
    }

    public PartConstraintCollection getPartConstraints() {
        return new PartConstraintCollection();
    }
}
