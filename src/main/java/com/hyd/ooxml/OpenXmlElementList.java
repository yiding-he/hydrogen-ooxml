package com.hyd.ooxml;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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

    public List<OpenXmlElement> toList() {
        return StreamSupport.stream(this.spliterator(), false).collect(Collectors.toList());
    }
}
