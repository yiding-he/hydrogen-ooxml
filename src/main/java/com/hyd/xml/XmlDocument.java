package com.hyd.xml;

import com.hyd.ms.io.Stream;
import com.hyd.utilities.IterableTreeWalker;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.jaxen.JaxenException;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class XmlDocument {

    public static XmlDocument fromXmlString(String xml) {
        return new XmlDocument(Xml.parseString(xml));
    }

    public static XmlDocument fromStream(Stream stream) {
        return new XmlDocument(Xml.parseDocumentAndClose(stream.read()));
    }

    public static XmlDocument fromDocument(Document document) {
        return new XmlDocument(document);
    }

    //////////////////////////

    private final Document document;

    private final Map<String, String> namespaces = new HashMap<>();

    private XmlDocument(Document document) {
        this.document = document;
        initialize();
    }

    private void initialize() {
        initNamespaces();
    }

    private void initNamespaces() {
        for (Element element : allElements()) {
            if (element.getNamespace() != null) {
                String prefix = StringUtils.defaultIfBlank(element.getNamespacePrefix(), "_");
                String uri = element.getNamespaceURI();
                this.namespaces.put(prefix, uri);
            }
        }
    }

    public Iterable<Element> allElements() {
        return new IterableTreeWalker<>(this.document.getRootElement(), Element::elements);
    }

    public List<Element> lookupElements(String xpath) {
        return lookupElements(this.document, xpath);
    }

    public List<Element> lookupElements(Node parent, String xpath) {
        try {
            XPath xPath = createXpath(xpath);
            List<?> list = xPath.selectNodes(parent);

            return list
                .stream()
                .filter(o -> o instanceof Element)
                .map(o -> (Element) o)
                .collect(Collectors.toList());

        } catch (JaxenException e) {
            throw new XmlException(e);
        }
    }

    public Optional<Element> lookupElement(Node parent, String xpath) {
        List<Element> elements = lookupElements(parent, xpath);
        return elements.isEmpty() ? Optional.empty() : Optional.of(elements.get(0));
    }

    public Optional<Attribute> lookupAttribute(String xpath) {
        return lookupAttribute(this.document, xpath);
    }

    public Optional<Attribute> lookupAttribute(Node parent, String xpath) {
        try {
            XPath xPath = createXpath(xpath);
            List<?> list = xPath.selectNodes(parent);

            return list
                .stream()
                .filter(o -> o instanceof Attribute)
                .map(o -> (Attribute) o)
                .findFirst();

        } catch (JaxenException e) {
            throw new XmlException(e);
        }
    }

    public Optional<String> lookupAttributeValue(String xpath) {
        return lookupAttributeValue(this.document, xpath);
    }

    public Optional<String> lookupAttributeValue(Node parent, String xpath) {
        return lookupAttribute(parent, xpath).map(Attribute::getValue);
    }

    private XPath createXpath(String xpath) throws JaxenException {
        XPath xPath = new Dom4jXPath(xpath);
        xPath.setNamespaceContext(new SimpleNamespaceContext(this.namespaces));
        return xPath;
    }
}
