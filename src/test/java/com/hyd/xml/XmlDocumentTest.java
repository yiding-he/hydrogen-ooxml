package com.hyd.xml;

import org.dom4j.Element;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

class XmlDocumentTest {

    public static final String XML1 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
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
        "</Types>";

    public static final String XML2 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
        "<p:sld xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\"\n" +
        "       xmlns:p=\"http://schemas.openxmlformats.org/presentationml/2006/main\">\n" +
        "       <p:cSld>\n" +
        "              <p:spTree>\n" +
        "                     <p:nvGrpSpPr>\n" +
        "                            <p:cNvPr id=\"1\" name=\"\"/>\n" +
        "                            <p:cNvGrpSpPr/>\n" +
        "                            <p:nvPr/>\n" +
        "                     </p:nvGrpSpPr>\n" +
        "                     <p:grpSpPr>\n" +
        "                            <a:xfrm>\n" +
        "                                   <a:off x=\"0\" y=\"0\"/>\n" +
        "                                   <a:ext cx=\"0\" cy=\"0\"/>\n" +
        "                                   <a:chOff x=\"0\" y=\"0\"/>\n" +
        "                                   <a:chExt cx=\"0\" cy=\"0\"/>\n" +
        "                            </a:xfrm>\n" +
        "                     </p:grpSpPr>\n" +
        "                     <p:sp>\n" +
        "                            <p:nvSpPr>\n" +
        "                                   <p:cNvPr id=\"2\" name=\"标题 1\"/>\n" +
        "                                   <p:cNvSpPr>\n" +
        "                                          <a:spLocks noGrp=\"1\"/>\n" +
        "                                   </p:cNvSpPr>\n" +
        "                                   <p:nvPr>\n" +
        "                                          <p:ph type=\"title\"/>\n" +
        "                                   </p:nvPr>\n" +
        "                            </p:nvSpPr>\n" +
        "                            <p:spPr/>\n" +
        "                            <p:txBody>\n" +
        "                                   <a:bodyPr/>\n" +
        "                                   <a:lstStyle/>\n" +
        "                                   <a:p>\n" +
        "                                          <a:r>\n" +
        "                                                 <a:rPr lang=\"zh-CN\" altLang=\"en-US\" sz=\"5865\" dirty=\"0\" smtClean=\"0\"/>\n" +
        "                                                 <a:t>卖火柴的小女孩</a:t>\n" +
        "                                          </a:r>\n" +
        "                                          <a:endParaRPr lang=\"zh-CN\" altLang=\"en-US\" sz=\"5865\" dirty=\"0\"/>\n" +
        "                                   </a:p>\n" +
        "                            </p:txBody>\n" +
        "                     </p:sp>\n" +
        "              </p:spTree>\n" +
        "       </p:cSld>\n" +
        "       <p:clrMapOvr>\n" +
        "              <a:masterClrMapping/>\n" +
        "       </p:clrMapOvr>\n" +
        "       <p:transition>\n" +
        "              <p:checker dir=\"vert\"/>\n" +
        "       </p:transition>\n" +
        "       <p:timing>\n" +
        "              <p:tnLst>\n" +
        "                     <p:par>\n" +
        "                            <p:cTn id=\"1\" dur=\"indefinite\" restart=\"never\" nodeType=\"tmRoot\"/>\n" +
        "                     </p:par>\n" +
        "              </p:tnLst>\n" +
        "       </p:timing>\n" +
        "</p:sld>";

    @Test
    public void testAllElements() throws Exception {
        XmlDocument xmlDocument = XmlDocument.fromXmlString(XML1);
        for (Element element : xmlDocument.allElements()) {
            System.out.println(element.getQualifiedName() + " = " +
                element.getNamespacePrefix() + " + " + element.getName() +
                " [" + element.getNamespaceURI() + "]"
            );
        }
    }

    @Test
    public void testLookupElements() throws Exception {
        XmlDocument xmlDocument = XmlDocument.fromXmlString(XML1);
        Consumer<Element> print = element -> {
            System.out.println(element.getQualifiedName());
        };

        xmlDocument.lookupElements("/_:Types").forEach(print);
        xmlDocument.lookupElements("/_:Types/_:Override").forEach(print);
        xmlDocument.lookupElements("/_:Types/_:Default").forEach(print);
    }
}
