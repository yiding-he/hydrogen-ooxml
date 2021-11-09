package com.hyd.utilities;

import org.apache.commons.lang3.StringUtils;

public class Uris {

    public static String getBaseName(String uri) {
        int lastSlash = StringUtils.lastIndexOf(uri, "/");
        if (lastSlash < 0) {
            return uri;
        }

        int lastDot = StringUtils.lastIndexOf(uri, ".", lastSlash);
        if (lastDot < 0) {
            return StringUtils.substring(uri, lastSlash + 1);
        } else {
            return StringUtils.substring(uri, lastSlash + 1, lastDot);
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

    public static String combine(String...  fragments) {
        return String.join("/", fragments);
    }
}
