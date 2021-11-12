package com.hyd.utilities;

import org.apache.commons.lang3.StringUtils;

public class Uris {

    public static String getBaseName(String uri) {
        int lastSlash = StringUtils.lastIndexOf(uri, "/");
        if (lastSlash < 0) {
            return uri;
        }

        if (!uri.contains(".")) {
            return StringUtils.substring(uri, lastSlash + 1);
        } else {
            return StringUtils.substringBeforeLast(StringUtils.substring(uri, lastSlash + 1), ".");
        }
    }

    public static String getExtension(String uri) {
        int lastSlash = StringUtils.lastIndexOf(uri, "/");
        if (lastSlash < 0) {
            return StringUtils.substringAfterLast(uri, ".");
        } else {
            return StringUtils.substringAfterLast(StringUtils.substring(uri, lastSlash + 1), ".");
        }
    }

    public static String getParent(String uri) {
        int lastSlash = StringUtils.lastIndexOf(uri, "/");
        if (lastSlash < 0) {
            return "/";
        } else {
            return StringUtils.substringBefore(uri, lastSlash);
        }
    }

    public static String combine(String...  fragments) {
        return String.join("/", fragments);
    }
}
