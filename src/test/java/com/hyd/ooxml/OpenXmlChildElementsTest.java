package com.hyd.ooxml;

import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OpenXmlChildElementsTest {

    private static class E extends OpenXmlCompositeElement {

        private final String id;

        private E(String id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return id;
        }
    }

    private E createTree() {
        E root = new E("1");
        root.appendChild(new E("1.1")).appendChild(new E("1.1.1")).appendChild(new E("1.1.1.1")).appendChild(new E("1.1.1.1.1"));
        root.appendChild(new E("1.2")).append(new E("1.2.1"), new E("1.2.2"));
        root.appendChild(new E("1.3")).append(new E("1.3.1"), new E("1.3.2"), new E("1.3.3"));
        return root;
    }

    private String getIdList(Iterable<OpenXmlElement> iterable) {
        return StreamSupport
            .stream(iterable.spliterator(), false)
            .map(Object::toString)
            .collect(Collectors.joining(","));
    }

    ///////////////////////////////////////////////////////////////////

    @Test
    void iterator() {
        E root = createTree();
        String s = getIdList(root);
        assertEquals("1.1,1.2,1.3", s);
    }

    @Test
    public void testDescendants() {
        E root = createTree();
        Iterable<OpenXmlElement> descendants = root.descendants();
        String s = getIdList(descendants);
        assertEquals("1.1,1.1.1,1.1.1.1,1.1.1.1.1,1.2,1.2.1,1.2.2,1.3,1.3.1,1.3.2,1.3.3", s);
    }

    @Test
    public void testDescendantsEmpty() {
        E root = new E("");
        Iterable<OpenXmlElement> descendants = root.descendants();
        String s = getIdList(descendants);
        assertEquals("", s);
    }
}
