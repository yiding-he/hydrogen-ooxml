package com.hyd.utilities.assertion;

import org.apache.commons.lang3.ArrayUtils;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class Assert {

    public static void notNull(Object obj, String name) {
        if (obj == null) {
            throw new NullPointerException(name + " should not be null");
        }
    }

    public static void notBlank(String s, String name) {
        if (isBlank(s)) {
            throw new IllegalArgumentException(name + " is null or blank string");
        }
    }

    ///////////////////////////////////////////////////////////////////

    public static void that(boolean assertion, String message, Object... messageParams) {
        if (!assertion) {
            String finalMessage = ArrayUtils.isEmpty(messageParams) ? message : String.format(message, messageParams);
            throw new AssertException(finalMessage);
        }
    }

    public static void not(boolean assertion, String message, Object... messageParams) {
        that(!assertion, message, messageParams);
    }

    public static void shouldBeNull(Object obj, String message, Object... messageParams) {
        that(obj == null, message, messageParams);
    }

    public static void isNCName(String s, String name) {
        not(s.contains(":"), name + " should be a NCName");
    }
}
