package com.hyd.ooxml;

import java.util.Iterator;

public class OpenXmlChildElements extends OpenXmlElementList {

    private final OpenXmlElement container;

    public OpenXmlChildElements(OpenXmlElement container) {
        this.container = container;
    }

    @Override
    public Iterator<OpenXmlElement> iterator() {
        return new Iterator<OpenXmlElement>() {

            private OpenXmlElement current;

            @Override
            public boolean hasNext() {
                if (!container.hasChildren()) {
                    return false;
                }
                if (container.getFirstChild() == null) {
                    return false;
                }
                if (current == null) {
                    return container.getFirstChild() != null;
                } else {
                    return current.getNextSibling() != null;
                }
            }

            @Override
            public OpenXmlElement next() {
                if (current == null) {
                    current = container.getFirstChild();
                } else {
                    current = current.getNextSibling();
                }
                return current;
            }
        };
    }
}
