package com.hyd.ooxml.packaging;

import java.util.Map;
import java.util.TreeMap;

public abstract class OpenXmlPartContainer {

    private final Map<String, OpenXmlPart> childrenPartsDictionary = new TreeMap<>();

}
