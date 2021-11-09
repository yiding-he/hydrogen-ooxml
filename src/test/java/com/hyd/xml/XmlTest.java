package com.hyd.xml;

import org.junit.jupiter.api.Test;
import org.w3c.dom.*;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import java.util.function.Consumer;

import static com.hyd.xml.Xml.XPATH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class XmlTest {

    @Test
    public void testParseXml() throws Exception {
        Document doc = Xml.parseDocumentAndClose(XmlTest.class.getResourceAsStream("/presentation.xml"));

        Element root = doc.getDocumentElement();
        System.out.println("root.getTagName() = " + root.getTagName());
        System.out.println("root.getLocalName() = " + root.getLocalName());
        System.out.println("root.getPrefix() = " + root.getPrefix());
        System.out.println("root.getNamespaceURI() = " + root.getNamespaceURI());

        NodeList childNodes = root.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            System.out.println("child " + i + " : " +
                node.getNodeType() + ", " + node.getNodeName() + ", " + node.getPrefix());
        }

        NamedNodeMap attributes = root.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attr = attributes.item(i);
            System.out.println("attr " + i + " : " +
                attr.getNodeType() + ", " + attr.getNodeName() + ", " + attr.getPrefix() + ", " + attr.getNodeValue());
        }

        System.out.println();

        Element sldMasterIdLst = (Element) root.getElementsByTagName("p:sldMasterIdLst").item(0);
        Element sldMasterId = (Element) sldMasterIdLst.getElementsByTagName("p:sldMasterId").item(0);

        System.out.println("sldMasterId.getAttribute(\"id\") = " + sldMasterId.getAttribute("id"));
        System.out.println("sldMasterId.getAttribute(\"r:id\") = " + sldMasterId.getAttribute("r:id"));
    }

    @Test
    public void testCreateElementWithNameSpace() throws Exception {
        Document doc = Xml.newDocument();
        Element root = doc.createElement("p:presentation");
        Xml.addNamespace(root, "a", "http://schemas.openxmlformats.org/drawingml/2006/main");
        Xml.addNamespace(root, "p", "http://schemas.openxmlformats.org/presentationml/2006/main");
        Xml.addNamespace(root, "r", "http://schemas.openxmlformats.org/officeDocument/2006/relationships");
        doc.appendChild(root);

        Element sldMasterIdLst = doc.createElement("p:sldMasterIdLst");
        root.appendChild(sldMasterIdLst);

        Element sldMasterId = doc.createElement("p:sldMasterId");
        sldMasterId.setAttribute("id", "2147483648");
        sldMasterId.setAttribute("r:id", "rId1");
        sldMasterIdLst.appendChild(sldMasterId);

        System.out.println(Xml.toString(doc));
    }

    @Test
    public void testXpath() throws Exception {
        Document doc = Xml.parseString("<a><b/></a>");
        NodeList evaluate;

        evaluate = (NodeList) XPATH.evaluate("/a", doc, XPathConstants.NODESET);
        System.out.println("evaluate.getClass() = " + evaluate.getClass());
        System.out.println("evaluate.getLength() = " + evaluate.getLength());

        evaluate = (NodeList) XPATH.evaluate("b", doc.getDocumentElement(), XPathConstants.NODESET);
        System.out.println("evaluate.getClass() = " + evaluate.getClass());
        System.out.println("evaluate.getLength() = " + evaluate.getLength());
    }

    @Test
    public void testLookupElements() throws Exception {
        Document doc = Xml.parseString("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
            "<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">" +
            "<Default ContentType=\"application/vnd.openxmlformats-officedocument.presentationml.presentation.main+xml\" Extension=\"xml\"/>" +
            "<Default ContentType=\"application/vnd.openxmlformats-package.relationships+xml\" Extension=\"rels\"/>" +
            "<Override ContentType=\"application/vnd.openxmlformats-package.relationships+xml\" PartName=\"/ppt/slidelayouts/_rels/slidelayout1.xml.rels\"/>" +
            "<Override ContentType=\"application/vnd.openxmlformats-officedocument.presentationml.slideLayout+xml\" PartName=\"/ppt/slidelayouts/slidelayout1.xml\"/>" +
            "<Override ContentType=\"application/vnd.openxmlformats-package.relationships+xml\" PartName=\"/ppt/_rels/presentation.xml.rels\"/>" +
            "<Override ContentType=\"application/vnd.openxmlformats-officedocument.presentationml.slide+xml\" PartName=\"/ppt/slides/slide1.xml\"/>" +
            "<Override ContentType=\"application/vnd.openxmlformats-officedocument.theme+xml\" PartName=\"/ppt/slidelayouts/slidemasters/theme/theme1.xml\"/>" +
            "<Override ContentType=\"application/vnd.openxmlformats-officedocument.presentationml.slideMaster+xml\" PartName=\"/ppt/slidelayouts/slidemasters/slidemaster1.xml\"/>" +
            "<Override ContentType=\"application/vnd.openxmlformats-package.relationships+xml\" PartName=\"/ppt/slidelayouts/slidemasters/_rels/slidemaster1.xml.rels\"/>" +
            "<Override ContentType=\"application/vnd.openxmlformats-package.relationships+xml\" PartName=\"/ppt/slides/_rels/slide1.xml.rels\"/>" +
            "</Types>");

        Consumer<String> tryXpath = xpath -> {
            try {
                NodeList nodeList = (NodeList) XPATH.evaluate(xpath, doc, XPathConstants.NODESET);
                System.out.println("'" + xpath + "' result elements : " + nodeList.getLength());
                for (int i = 0; i < nodeList.getLength(); i++) {
                    System.out.println("    " + nodeList.item(i).getLocalName());
                }
            } catch (XPathExpressionException e) {
                e.printStackTrace();
            }
        };

        tryXpath.accept("/*");
        tryXpath.accept("/*[local-name()='Types']/*[local-name()='Default']");
    }

    @Test
    public void testFirstChild() throws Exception {
        Document doc = Xml.parseString("<a><b/></a>");
        assertEquals("a", doc.getDocumentElement().getTagName());

        Element b = Xml.firstChild(doc.getDocumentElement(), "b");
        assertNotNull(b);
    }
}
