package com.hyd.ooxml.packaging;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OpenXmlPartHelper {

    private static Map<String, List<Class<? extends OpenXmlPart>>> data = new ConcurrentHashMap<>();

    public static List<Class<? extends OpenXmlPart>> searchOpenXmlPartTypes(String packageName) {
        return data.computeIfAbsent(packageName, _packageName -> {
            Reflections reflections = new Reflections(_packageName, Scanners.SubTypes);
            return new ArrayList<>(reflections.getSubTypesOf(OpenXmlPart.class));
        });
    }
}
