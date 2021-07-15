package com.hyd.assertion;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class Assert {

    public static void notNull(Object obj, String name) {
        if (obj == null) {
            throw new NullPointerException(name + " should not be null");
        }
    }

    public static void notBlank(String s, String name) {
        if (isBlank(s)) {
            throw new IllegalArgumentException(name + " is blank string");
        }
    }

    ///////////////////////////////////////////////////////////////////

    public static void that(boolean assertion, String message) {
        if (!assertion) {
            throw new AssertException(message);
        }
    }

    public static void not(boolean assertion, String message) {
        that(!assertion, message);
    }

    public static void isNCName(String s, String name) {
        not(s.contains(":"), name + " should be a NCName");
    }
}
