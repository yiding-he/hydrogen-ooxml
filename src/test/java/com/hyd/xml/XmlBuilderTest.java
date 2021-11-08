package com.hyd.xml;

import com.hyd.ms.io.MemoryStream;
import com.hyd.ooxml.OpenXmlNamespace;
import com.hyd.ooxml.framework.metadata.OpenXmlAttribute;
import com.hyd.xml.XmlBuilder.XmlBuilderElement;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class XmlBuilderTest {

    @Test
    public void testBuild() throws Exception {
        MemoryStream stream = new MemoryStream();

        XmlBuilder builder = new XmlBuilder(stream);
        XmlBuilderElement root = builder.createRoot("a").addNamespace(OpenXmlNamespace.Presentation).addAttribute(new OpenXmlAttribute("k1", "v1"));
        builder.appendChild(root, "b").addNamespace(OpenXmlNamespace.Relationship);
        builder.appendChild(root, "c").addAttribute(new OpenXmlAttribute("k2", "v2"));
        builder.appendChild(root, "d").setInnerText("haha");
        builder.finish();

        assertEquals(
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<a xmlns:p=\"http://schemas.openxmlformats.org/presentationml/2006/main\" k1=\"v1\">" +
                "<b xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\"></b>" +
                "<c k2=\"v2\"></c><d>haha</d></a>",
            stream.toString()
        );
    }
}
