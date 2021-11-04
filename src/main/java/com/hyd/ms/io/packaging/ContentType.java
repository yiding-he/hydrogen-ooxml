package com.hyd.ms.io.packaging;

import com.hyd.utilities.assertion.Assert;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * sample: <br/>
 * <code>type/subtype ; param1=value1 ; param2=value2 ; param3="value3"</code>
 */
public class ContentType {

    private final String originalString;

    private String contentType;

    private String type;

    private String subType;

    private Map<String, String> parameterDictionary;

    public ContentType(String contentTypeStr) {
        Assert.notNull(contentTypeStr, "contentTypeStr");

        this.originalString = contentTypeStr.trim();
        String[] parts = this.originalString.split(";");

        parseTypeAndSubType(parts[0]);
        if (parts.length > 1) {
            parameterDictionary = new HashMap<>();
            parseParameterAndValue(Arrays.copyOfRange(parts, 1, parts.length));
        }
    }

    private void parseParameterAndValue(String[] params) {
        for (String param : params) {
            String[] split = param.trim().split("=");
            Assert.that(split.length == 2, "invalid parameter part '%s'", param);
            Assert.that(isNotEmpty(split[0]) && isNotEmpty(split[1]), "invalid parameter part '%s'", param);

            if (split[1].length() >= 2 && split[1].startsWith("\"") && split[1].endsWith("\"")) {
                split[1] = split[1].substring(1, split[1].length() - 1);
            }
            parameterDictionary.put(split[0], split[1]);
        }
    }

    private void parseTypeAndSubType(String part) {
        String[] split = part.trim().split("/");
        Assert.that(split.length == 2, "invalid content part '%s'", part);
        Assert.that(isNotEmpty(split[0]) && isNotEmpty(split[1]), "invalid content part '%s'", part);

        this.type = split[0];
        this.subType = split[1];
    }

    public boolean areTypeAndSubTypeEqual(ContentType other) {
        // Return false if this content type object has parameters
        if (parameterDictionary != null && !parameterDictionary.isEmpty()) {
            return false;
        }
        // Return false if the content type object passed in has parameters
        if (other.parameterDictionary != null && !other.parameterDictionary.isEmpty()) {
            return false;
        }

        return this.type.equalsIgnoreCase(other.type) && this.subType.equalsIgnoreCase(other.subType);
    }

    public String getOriginalString() {
        return originalString;
    }

    @Override
    public String toString() {
        return getOriginalString();
    }
}
