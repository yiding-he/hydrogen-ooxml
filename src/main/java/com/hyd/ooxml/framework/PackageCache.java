package com.hyd.ooxml.framework;

import com.hyd.ooxml.packaging.OpenXmlPartContainer;
import com.hyd.ooxml.packaging.OpenXmlPartData;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PackageCache {

    private static final PackageCache instance = new PackageCache();

    public static PackageCache getInstance() {
        return instance;
    }

    private final Map<Class<? extends OpenXmlPartContainer>, OpenXmlPartData> partData = new ConcurrentHashMap<>();

    public OpenXmlPartData parsePartData(OpenXmlPartContainer part) {
        return partData.computeIfAbsent(part.getClass(), OpenXmlPartData::new);
    }
}
