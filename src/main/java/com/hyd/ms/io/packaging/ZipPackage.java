package com.hyd.ms.io.packaging;

import com.hyd.assertion.Assert;
import com.hyd.ms.io.FileAccess;
import com.hyd.ms.io.FileMode;
import com.hyd.ms.io.FileStream;
import com.hyd.ms.io.Stream;
import com.hyd.ms.io.compression.ZipArchive;
import com.hyd.xml.Xml;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * In memory zip package struct
 */
public class ZipPackage extends Package {

    private final FileMode packageFileMode;

    private Stream stream;

    private ZipArchive zipArchive;

    private ContentTypeHelper contentTypeHelper;

    public ZipPackage(String path) {
        this(path, FileMode.OpenOrCreate, FileAccess.ReadWrite);
    }

    /**
     * Constructor
     *
     * @param path              file to create or open
     * @param packageFileMode   intended to be used in FileStream
     * @param packageFileAccess intended to be used in {@link ZipArchive}
     */
    public ZipPackage(String path, FileMode packageFileMode, FileAccess packageFileAccess) {
        this(new FileStream(path), packageFileMode, packageFileAccess);
    }

    /**
     * Constructor
     *
     * @param packageFileMode   intended to be used in FileStream
     * @param packageFileAccess intended to be used in {@link ZipArchive}
     */
    public ZipPackage(Stream stream, FileMode packageFileMode, FileAccess packageFileAccess) {
        super(packageFileAccess);
        Assert.that(packageFileMode.isAvailable(), "mode '%s' is not available for now", packageFileMode);

        if (packageFileMode == FileMode.Open && stream.length() == 0) {
            throw new ZipPackageException("Not a valid archive");
        } else if (packageFileMode == FileMode.CreateNew && stream.length() != 0) {
            throw new ZipPackageException("Stream not empty");
        } else if (packageFileMode == FileMode.Create && stream.length() != 0) {
            stream.setLength(0);
        }

        if (stream.length() != 0) {
            this.zipArchive = new ZipArchive(stream.read());
        } else {
            this.zipArchive = new ZipArchive();
        }

        this.packageFileMode = packageFileMode;
        this.stream = stream;
        this.contentTypeHelper = new ContentTypeHelper(this.zipArchive, packageFileMode, packageFileAccess);
    }

    /////////////////////////////////////////////////////////////////// methods

    @Override
    public void close() throws IOException {
        if (this.packageFileMode != FileMode.Open) {
            this.contentTypeHelper.saveToFile();
            this.zipArchive.write(this.stream.write());
            this.stream.close();
        }
    }

    @Override
    protected PackagePart createPartCore(
        URI partUri, String contentType, CompressionOption compressionOption) {
        return null;
    }

    @Override
    protected PackagePart getPartCore(URI partUri) {
        return null;
    }

    @Override
    protected void deletePartCore(URI partUri) {

    }

    @Override
    protected PackagePart[] getPartsCore() {
        return new PackagePart[0];
    }

    @Override
    protected void flushCore() {
        this.contentTypeHelper.saveToFile();
    }

    /////////////////////////////////////////////////////////////////// inner class

    private static class ContentTypeHelper {

        public static final String CONTENT_TYPES_NS = "http://schemas.openxmlformats.org/package/2006/content-types";

        public static final String ENTRY_NAME = "[Content_Types].xml";

        private final Map<PackUriHelper.ValidatedPartUri, ContentType> overrideDictionary = new HashMap<>();

        private final Map<String, ContentType> defaultDictionary = new HashMap<>();

        private final ZipArchive zipArchive;

        private final FileMode packageFileMode;

        private final FileAccess packageFileAccess;

        ContentTypeHelper(ZipArchive zipArchive, FileMode packageFileMode, FileAccess packageFileAccess) {
            this.zipArchive = zipArchive;
            this.packageFileAccess = packageFileAccess;
            this.packageFileMode = packageFileMode;
        }

        public void saveToFile() {
            Document document = Xml.newDocument();
            document.setXmlStandalone(true);

            Element types = document.createElementNS(CONTENT_TYPES_NS, "Types");
            document.appendChild(types);

            defaultDictionary.forEach((extension, contentType) -> {
                Element def = document.createElement("Default");
                def.setAttribute("Extension", extension);
                def.setAttribute("ContentType", contentType.toString());
                types.appendChild(def);
            });

            overrideDictionary.forEach((uri, contentType) -> {
                Element override = document.createElement("Override");
                override.setAttribute("PartName", uri.toString());
                override.setAttribute("ContentType", contentType.toString());
                types.appendChild(override);
            });

            byte[] bytes = Xml.toBytes(document);
            zipArchive.createEntry(ENTRY_NAME).setContent(bytes);
        }
    }
}
