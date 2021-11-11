package com.hyd.ooxml.packaging;

import com.hyd.ooxml.generated.packaging.PresentationPart;
import com.hyd.ooxml.generated.packaging.SlidePart;
import com.hyd.xml.Xml;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

class TestReadPresentationDocument {

    public static final String PATH = "src/test/resources/simple-create-by-ms-office.pptx";
    // public static final String PATH = "F:\\Projects\\ZnXunzhi\\ajia-base\\xz-ooxml\\samples\\multi-pages.pptx";

    @Test
    public void testListAllParts() {
        PresentationDocument presentationDocument = PresentationDocument.open(PATH, false);

        System.out.println("///////////////////////////////////////////////////////////////////");
        for (OpenXmlPart part : presentationDocument.getAllParts()) {
            System.out.println(part.getUri() + " => " + part.getClass().getSimpleName());
        }
        System.out.println("///////////////////////////////////////////////////////////////////");
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
