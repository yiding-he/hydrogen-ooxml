package com.hyd.ooxml;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

class OpenXmlChildElementsTest {

    private static class E extends OpenXmlCompositeElement {
        private final String id;

        private E(String id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return "E{" +
                "id='" + id + '\'' +
                '}';
        }
    }

    private E createTree() {
        E root = new E("1");
        root.appendChild(new E("1.1")).append(Arrays.asList(new E("1.1.1"), new E("1.1.2")));
        root.appendChild(new E("1.2")).append(Arrays.asList(new E("1.2.1"), new E("1.2.2")));
        root.appendChild(new E("1.3")).append(Arrays.asList(new E("1.3.1"), new E("1.3.2"), new E("1.3.3")));
        return root;
    }

    @Test
    void iterator() {
        E root = createTree();

        for (OpenXmlElement child : root) {
            System.out.println(child);
        }
    }

    @Test
    public void testDescendants() throws Exception {
        E root = createTree();

        for (OpenXmlElement descendant : root.descendants()) {
            System.out.println(descendant);
        }
    }
}
