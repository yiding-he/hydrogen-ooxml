package com.hyd.xml;

import com.hyd.ms.io.Stream;
import com.hyd.ooxml.OpenXmlNamespace;
import com.hyd.ooxml.framework.metadata.OpenXmlAttribute;
import com.hyd.utilities.assertion.Assert;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class XmlBuilder {

    public class XmlBuilderElement {

        private final String tagName;

        private final List<OpenXmlAttribute> attributes = new ArrayList<>();

        private final List<OpenXmlNamespace> namespaces = new ArrayList<>();

        private final List<XmlBuilderElement> children = new ArrayList<>();

        private String defaultNamespace;

        private String innerText;

        public XmlBuilderElement(String tagName) {
            this.tagName = tagName;
        }

        public XmlBuilderElement setDefaultNamespace(String defaultNamespace) {
            this.defaultNamespace = defaultNamespace;
            return this;
        }

        public XmlBuilderElement addNamespace(OpenXmlNamespace namespace) {
            this.namespaces.add(namespace);
            return this;
        }

        public XmlBuilderElement addAttribute(String attrName, String attrValue) {
            return addAttribute(new OpenXmlAttribute(attrName, attrValue));
        }

        public XmlBuilderElement addAttribute(OpenXmlAttribute attribute) {
            this.attributes.add(attribute);
            return this;
        }

        public XmlBuilderElement setInnerText(String innerText) {
            this.innerText = innerText;
            return this;
        }

        private XmlBuilderElement addChild(XmlBuilderElement child) {
            this.children.add(child);
            return this;
        }

        private void finish() {

            sb.append("<").append(this.tagName);
            if (defaultNamespace != null) {
                sb.append(" xmlns=\"").append(defaultNamespace).append("\"");
            }
            for (OpenXmlNamespace namespace : namespaces) {
                sb.append(" xmlns:").append(namespace.getPrefix()).append("=\"").append(namespace.getUri()).append("\"");
            }
            for (OpenXmlAttribute attribute : attributes) {
                sb.append(" ").append(attribute.getNameWithPrefix()).append("=\"").append(attribute.getValue()).append("\"");
            }
            sb.append(">");
            if (innerText != null) {
                sb.append(innerText);
            }

            if (!children.isEmpty()) {
                for (XmlBuilderElement child : children) {
                    child.finish();
                }
            }

            if (innerText == null && children.isEmpty()) {
                sb.deleteCharAt(sb.length() - 1).append(" />");
            } else {
                sb.append("</").append(this.tagName).append(">");
            }
        }
    }

    //////////////////////////

    private final StringBuilder sb = new StringBuilder();

    private final Stream stream;

    private XmlBuilderElement rootElement;

    private XmlBuilderElement currentElement;

    private Boolean standalone;

    public XmlBuilder(Stream stream) {
        Assert.notNull(stream, "stream");
        this.stream = stream;
    }

    public XmlBuilderElement createRoot(String tagName) {
        rootElement = new XmlBuilderElement(tagName);
        return rootElement;
    }

    public XmlBuilderElement appendChild(XmlBuilderElement parent, String tagName) {
        Assert.notNull(rootElement, "rootElement");
        XmlBuilderElement element = new XmlBuilderElement(tagName);
        parent.addChild(element);
        return element;
    }

    public XmlBuilder setStandalone(boolean standalone) {
        this.standalone = standalone;
        return this;
    }

    public XmlBuilderElement getCurrentElement() {
        return currentElement;
    }

    public void setCurrentElement(XmlBuilderElement currentElement) {
        this.currentElement = currentElement;
    }

    public void finish() {
        if (standalone != null) {
            sb.append("<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"").append(standalone ? "yes" : "no").append("\"?>");
        } else {
            sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        }
        if (rootElement != null) {
            rootElement.finish();
        }
        stream.writeBytes(sb.toString().getBytes(StandardCharsets.UTF_8));
    }
}
