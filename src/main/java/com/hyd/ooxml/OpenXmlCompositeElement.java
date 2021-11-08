package com.hyd.ooxml;

import com.hyd.utilities.assertion.Assert;
import com.hyd.xml.XmlBuilder;

public abstract class OpenXmlCompositeElement extends OpenXmlElement {

    private OpenXmlElement lastChild;

    @Override
    protected void writeContentTo(XmlBuilder xmlBuilder) {
        for (OpenXmlElement childElement : getChildElements()) {
            childElement.writeTo(xmlBuilder);
        }
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    @Override
    public <T extends OpenXmlElement> T appendChild(T newChild) {
        Assert.notNull(newChild, "newChild");
        Assert.that(newChild.getParent() == null, "newChild is already part of a tree");

        final OpenXmlElement prevNode = lastChild;
        final OpenXmlElement nextNode = newChild;

        if (prevNode == null) {
            nextNode.setNext(nextNode);
        } else {
            nextNode.setNext(prevNode.getNext());
            prevNode.setNext(nextNode);
        }
        lastChild = nextNode;

        newChild.setParent(this);
        return newChild;
    }

    @Override
    public OpenXmlElement getLastChild() {
        return lastChild;
    }

    @Override
    public OpenXmlElement getFirstChild() {
        return lastChild == null ? null : lastChild.getNext();
    }

    @Override
    public boolean hasChildren() {
        return this.lastChild != null;
    }

    public OpenXmlElementList getChildElements() {
        return hasChildren()? new OpenXmlChildElements(this) : OpenXmlElementList.EMPTY;
    }
}
