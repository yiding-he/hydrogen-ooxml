package com.hyd.ooxml.packaging;

import com.hyd.ms.io.packaging.PackagePart;
import com.hyd.ooxml.generated.packaging.PresentationPart;
import com.hyd.ooxml.generated.packaging.SlidePart;
import com.hyd.utilities.Uris;
import com.hyd.xml.Xml;
import com.hyd.xml.XmlDocument;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

class TestReadPresentationDocument {

    public static final String PATH = "src/test/resources/simple-create-by-ms-office.pptx";

    @Test
    public void testListAllParts() {
        PresentationDocument presentationDocument = PresentationDocument.open(PATH, false);

        System.out.println("///////////////////////////////////////////////////////////////////");
        for (OpenXmlPart part : presentationDocument.getAllParts()) {
            System.out.println(part.getUri() + " => " +
                part.getClass().getSimpleName() + " => " +
                part.getPackagePart().getContentType());
        }
        System.out.println("///////////////////////////////////////////////////////////////////");
    }

    @Test
    public void testExportImages() throws Exception {
        PresentationDocument doc = PresentationDocument.open(PATH, false);
        for (OpenXmlPart part : doc.getAllParts()) {
            PackagePart packagePart = part.getPackagePart();

            if (packagePart.getContentType().startsWith("image/")) {
                String uri = packagePart.getUri().getUri().toString();
                String outputFileName = "target/" + Uris.getBaseName(uri) + "." + Uris.getExtension(uri);

                IOUtils.copy(packagePart.getStream().read(), Files.newOutputStream(Paths.get(outputFileName)));
                System.out.println(outputFileName + " saved.");
            }
        }
    }

    @Test
    public void testFindSlideTextContent() throws Exception {
        PresentationDocument ppt = PresentationDocument.open(PATH);
        PresentationPart root = (PresentationPart) ppt.getRootPart();

        for (SlidePart slidePart : root.getPartsOfType(SlidePart.class)) {
            System.out.println("////////////////////////// " + slidePart.getUri());
            XmlDocument doc = slidePart.getXmlDocument();
            doc.lookupElements("//p:sp//p:txBody").forEach(element -> {
                String text = doc.lookupElements("a:p/a:r/a:t", element).iterator().next().getText();
                System.out.println(text);
            });
        }
    }

    @Test
    public void testPrintPartsTree() throws Exception {
        PresentationDocument presentationDocument = PresentationDocument.open(PATH, false);
        OpenXmlPart rootPart = presentationDocument.getRootPart();
        final List<String> lines = new ArrayList<>();

        class TreePrinter {

            final Set<String> visited = new HashSet<>();

            void print(OpenXmlPart src, OpenXmlPart tgt) {
                if (tgt == null) {
                    for (IdPartPair child : src.parts()) {
                        print(src, child.openXmlPart);
                    }
                } else {
                    String relationship = "[" + src.getUri() + "] --> [" + tgt.getUri() + "]";
                    boolean added = visited.add(relationship);
                    if (added) {
                        lines.add(relationship);
                        print(tgt, null);
                    }
                }
            }
        }
        Collections.sort(lines);

        lines.add("@startuml");
        lines.add("left to right direction");
        new TreePrinter().print(rootPart, null);
        lines.add("@enduml");

        Files.write(Paths.get("target/0.plantuml"), lines);
        System.out.println("File created.");
    }

    @Test
    public void testListSlides() throws Exception {
        PresentationDocument presentationDocument = PresentationDocument.open(PATH, false);
        PresentationPart presentationPart = (PresentationPart) presentationDocument.getRootPart();

        SlidePart slide1 = presentationPart.getPartsOfType(SlidePart.class).iterator().next();
        System.out.println(Xml.toString(slide1.getContent(), true));
    }
}
