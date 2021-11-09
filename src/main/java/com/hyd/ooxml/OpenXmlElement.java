package com.hyd.ooxml;

import com.hyd.ooxml.framework.metadata.OpenXmlAttribute;
import com.hyd.utilities.assertion.Assert;
import com.hyd.xml.XmlBuilder;

import java.util.*;

public abstract class OpenXmlElement implements Iterable<OpenXmlElement> {

    private OpenXmlElement next;

    private OpenXmlElement parent;

    private List<OpenXmlAttribute> attributes = new ArrayList<>();

    ///////////////////////////////////////////////////////////////////

    public OpenXmlElement getParent() {
        return parent;
    }

    protected void setParent(OpenXmlElement parent) {
        this.parent = parent;
    }

    public OpenXmlElement getNext() {
        return next;
    }

    public void setNext(OpenXmlElement next) {
        this.next = next;
    }

    public OpenXmlElement getNextSibling() {
        if (parent == null) {
            return null;
        }
        if (getNext() == parent.getFirstChild()) {
            return null;
        }
        return getNext();
    }

    public void appendIterable(Iterable<OpenXmlElement> children) {
        Assert.notNull(children, "children");
        for (OpenXmlElement child : children) {
            appendChild(child);
        }
    }

    public void append(OpenXmlElement... children) {
        appendIterable(Arrays.asList(children));
    }

    @Override
    public Iterator<OpenXmlElement> iterator() {
        return new OpenXmlChildElements(this).iterator();
    }

    public Iterable<OpenXmlAttribute> attributes() {
        return this.attributes;
    }

    private void ensureAnnotated() {
        Assert.that(
            this.getClass().isAnnotationPresent(XmlElement.class),
            "class not annotated by XmlElement: %s", this.getClass().getCanonicalName()
        );
    }

    public String getLocalName() {
        ensureAnnotated();
        return this.getClass().getAnnotation(XmlElement.class).localName();
    }

    public String getPrefix() {
        ensureAnnotated();
        return this.getClass().getAnnotation(XmlElement.class).prefix();
    }

    public OpenXmlNamespace[] getNamespaces() {
        ensureAnnotated();
        return this.getClass().getAnnotation(XmlElement.class).namespaces();
    }

    public void setAttribute(OpenXmlNamespace namespace, String name, String value) {
        getOrCreateAttribute(namespace, name).setValue(value);
    }

    public void setAttribute(String name, String value) {
        getOrCreateAttribute(null, name).setValue(value);
    }

    private OpenXmlAttribute getOrCreateAttribute(OpenXmlNamespace namespace, String name) {
        String nameWithPrefix = namespace == null ? name : (namespace.getPrefix() + ":" + name);
        OpenXmlAttribute attribute = this.attributes.stream()
            .filter(a -> a.getNameWithPrefix().equals(nameWithPrefix)).findFirst().orElse(null);
        if (attribute == null) {
            attribute = new OpenXmlAttribute(namespace, name, null);
            this.attributes.add(attribute);
        }
        return attribute;
    }

    public <T extends OpenXmlElement> T appendChild(T newChild) {
        throw new UnsupportedOperationException("Non-composite elements do not have child elements");
    }

    public OpenXmlElement getFirstChild() {
        return null;
    }

    public OpenXmlElement getLastChild() {
        return null;
    }

    public boolean hasChildren() {
        return false;
    }

    public Iterable<OpenXmlElement> descendants() {
        final Stack<OpenXmlElement> stack = new Stack<>();
        stack.push(this);

        return () -> new Iterator<OpenXmlElement>() {
            private OpenXmlElement current;
            @Override
            public boolean hasNext() {
                if (stack.isEmpty()) {
                    return false;
                }
                OpenXmlElement top = stack.peek();
                OpenXmlElement child = top.getFirstChild();
                if (child != null) {
                    stack.push(child);
                    current = child;
                    return true;
                } else {
                    while (!stack.isEmpty()) {
                        top = stack.pop();
                        OpenXmlElement nextSibling = top.getNextSibling();
                        if (nextSibling != null) {
                            stack.push(nextSibling);
                            current = nextSibling;
                            return true;
                        }
                    }
                }
                return false;
            }

            @Override
            public OpenXmlElement next() {
                return current;
            }
        };
    }

    public void writeTo(XmlBuilder builder) {
        XmlBuilder.XmlBuilderElement parentElement = builder.getCurrentElement();
        XmlBuilder.XmlBuilderElement thisElement = builder.appendChild(parentElement, getXmlTagName());

        for (OpenXmlAttribute attribute : attributes()) {
            thisElement.addAttribute(attribute);
        }

        if (hasChildren() || !getInnerText().isEmpty()) {
            builder.setCurrentElement(thisElement);
            writeContentTo(builder);
        }
    }

    protected String getXmlTagName() {
        String prefix = getPrefix();
        String localName = getLocalName();
        return prefix.isEmpty() ? localName : (prefix + ":" + localName);
    }

    public String getInnerText() {
        return "";
    }

    ///////////////////////////////////////////////////////////////////

    protected void writeContentTo(XmlBuilder xmlBuilder) {
        // nothing to do for non-composite elements
    }
}
