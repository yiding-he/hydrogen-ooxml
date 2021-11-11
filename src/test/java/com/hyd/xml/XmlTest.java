package com.hyd.xml;

import org.dom4j.Document;
import org.dom4j.Element;
import org.junit.jupiter.api.Test;

import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class XmlTest {

    @Test
    public void testParseXml() throws Exception {
        Document doc = Xml.parseDocumentAndClose(XmlTest.class.getResourceAsStream("/presentation.xml"));

    }

    @Test
    public void testCreateElementWithNameSpace() throws Exception {
        Document doc = Xml.newDocument();
        Element root = Xml.createElement("p:presentation");
        Xml.addNamespace(root, "a", "http://schemas.openxmlformats.org/drawingml/2006/main");
        Xml.addNamespace(root, "p", "http://schemas.openxmlformats.org/presentationml/2006/main");
        Xml.addNamespace(root, "r", "http://schemas.openxmlformats.org/officeDocument/2006/relationships");
        doc.add(root);

        Element sldMasterIdLst = Xml.createElement("p:sldMasterIdLst");
        root.add(sldMasterIdLst);

        Element sldMasterId = Xml.createElement("p:sldMasterId");
        sldMasterId.addAttribute("id", "2147483648");
        sldMasterId.addAttribute("r:id", "rId1");
        sldMasterIdLst.add(sldMasterId);

        System.out.println(Xml.toString(doc));
    }

    @Test
    public void testXpath() throws Exception {
        Document doc = Xml.parseString("<a><b/></a>");
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

        BiConsumer<Integer, String> tryXpath = (i, xpath) -> {
            System.out.println(i  + ":");
            Xml.lookupElements(doc, xpath).forEach(element -> {
                System.out.println(element.getQualifiedName());
            });
            System.out.println();
        };

        tryXpath.accept(1, "/*");
        tryXpath.accept(2, "/Types");
        tryXpath.accept(3, "/Types/*");
        tryXpath.accept(4, "/Types/*:Default");
        tryXpath.accept(5, "/Types/*:Override");
    }

    @Test
    public void testFirstChild() throws Exception {
        Document doc = Xml.parseString("<a><b/></a>");
        assertEquals("a", doc.getRootElement().getName());

        Element b = Xml.firstChild(doc.getRootElement(), "b");
        assertNotNull(b);
    }
}
