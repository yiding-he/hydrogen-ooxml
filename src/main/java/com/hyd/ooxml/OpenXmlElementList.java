package com.hyd.ooxml;

import java.util.Iterator;

public abstract class OpenXmlElementList implements Iterable<OpenXmlElement> {

    public static final OpenXmlElementList EMPTY = new OpenXmlElementList() {
        @Override
        public Iterator<OpenXmlElement> iterator() {
            return new Iterator<OpenXmlElement>() {
                @Override
                public boolean hasNext() {
                    return false;
                }

                @Override
                public OpenXmlElement next() {
                    return null;
                }
            };
        }
    };
}
