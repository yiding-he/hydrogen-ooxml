package com.hyd.ms.io.packaging;

import com.hyd.ms.io.FileAccess;
import com.hyd.ms.io.FileMode;
import com.hyd.ms.io.FileStream;
import com.hyd.ms.io.Stream;
import com.hyd.ms.io.compression.ZipArchive;
import com.hyd.ms.io.compression.ZipArchiveEntry;
import com.hyd.ms.io.compression.ZipArchiveMode;
import com.hyd.utilities.Uris;
import com.hyd.utilities.assertion.Assert;
import com.hyd.xml.Xml;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.Element;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * In memory zip package struct
 */
@Slf4j
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
    public void dispose(boolean disposing) {
        try {
            if (this.packageFileMode != FileMode.Open) {
                this.contentTypeHelper.saveToFile();
                this.zipArchive.write(this.stream.write());
                this.stream.close();
            }
        } finally {
            super.dispose(disposing);
        }
    }

    @Override
    protected PackagePart createPartCore(
        URI partUri, String contentType, CompressionOption compressionOption
    ) {
        Assert.notBlank(contentType, "ContentType");
        PackUriHelper.ValidatedPartUri validatedPartUri = PackUriHelper.validatePartUri(partUri);

        // We need to remove the leading "/" character at the beginning of the part name.
        String zipItemName = validatedPartUri.getUri().toString().substring(1);
        ZipArchiveEntry zipArchiveEntry = this.zipArchive.createEntry(zipItemName);

        this.contentTypeHelper.addContentType(validatedPartUri, new ContentType(contentType));
        return new ZipPackagePart(this, zipArchive, zipArchiveEntry, validatedPartUri, contentType);
    }

    /**
     * This method is for custom implementation specific to the file format.
     * Returns the part after reading the actual physical bits. The method
     * returns a null to indicate that the part corresponding to the specified
     * Uri was not found in the container.
     * This method does not throw an exception if a part does not exist.
     */
    @Override
    protected PackagePart getPartCore(URI partUri) {
        // Currently, the design has two aspects which makes it possible to return
        // a null from this method -
        //  1. All the parts are loaded at Package.Open time and as such, this
        //     method would not be invoked, unless the user is asking for -
        //     i. a part that does not exist - we can safely return null
        //     ii.a part(interleaved/non-interleaved) that was added to the
        //        underlying package by some other means, and the user wants to
        //        access the updated part. This is currently not possible as the
        //        underlying zip i/o layer does not allow for FileShare.ReadWrite.
        //  2. Also, its not a straightforward task to determine if a new part was
        //     added as we need to look for atomic as well as interleaved parts and
        //     this has to be done in a case-sensitive manner. So, effectively
        //     we will have to go through the entire list of zip items to determine
        //     if there are any updates.
        //  If ever the design changes, then this method must be updated accordingly
        return null;
    }

    @Override
    protected void deletePartCore(URI partUri) {

    }

    @Override
    protected PackagePart[] getPartsCore() {
        List<PackagePart> parts = new ArrayList<>();
        for (ZipArchiveEntry zipArchiveEntry : this.zipArchive.entries()) {
            if (isZipItemValidOpcPartOrPiece(zipArchiveEntry.getName())) {
                String partUri = getOpcNameFromZipItemName(zipArchiveEntry.getName());
                try {
                    PackUriHelper.ValidatedPartUri validatedPartUri = PackUriHelper.validatePartUri(partUri);
                    ContentType contentType = contentTypeHelper.getContentType(validatedPartUri);
                    if (contentType != null) {
                        parts.add(new ZipPackagePart(
                            this, zipArchiveEntry.getArchive(), zipArchiveEntry, validatedPartUri, contentType.getOriginalString()
                        ));
                    }
                } catch (Exception e) {
                    // ignore invalid uri
                }
            }
        }
        return parts.toArray(new PackagePart[0]);
    }

    private String getOpcNameFromZipItemName(String entryName) {
        return "/" + entryName;
    }

    @SuppressWarnings("RedundantIfStatement")
    private boolean isZipItemValidOpcPartOrPiece(String entryName) {
        Assert.notNull(entryName, "entryName");
        if (entryName.toLowerCase().startsWith(ContentTypeHelper.ENTRY_NAME.toLowerCase())) {
            return false;
        } else {
            // Could be an empty zip folder
            // We decided to ignore zip items that contain a "/" as this could be a folder in a zip archive
            // Some tools support this and some don't. There is no way ensure that the zip item never have
            // a leading "/", although this is a requirement we impose on items created through our API
            // Therefore we ignore them at the packaging api level.
            // ------------------
            // This will ignore the folder entries found in the zip package created by some zip tool
            // PartNames ending with a "/" slash is also invalid, so we are skipping these entries,
            // this will also prevent the PackUriHelper.CreatePartUri from throwing when it encounters a
            // part name ending with a "/"
            if (entryName.startsWith("/") || entryName.endsWith("/")) {
                return false;
            } else {
                return true;
            }
        }
    }

    @Override
    protected void flushCore() {
        this.contentTypeHelper.saveToFile();
    }

    /////////////////////////////////////////////////////////////////// inner class

    private static class ContentTypeHelper {

        public static final String CONTENT_TYPES_NS = "http://schemas.openxmlformats.org/package/2006/content-types";

        public static final String ENTRY_NAME = "[Content_Types].xml";

        // uri -> contentType
        // TODO can be lazy initialized
        private final Map<PackUriHelper.ValidatedPartUri, ContentType> overrideDictionary = new HashMap<>();

        // extension -> contentType
        private final Map<String, ContentType> defaultDictionary = new HashMap<>();

        private final ZipArchive zipArchive;

        private final FileMode packageFileMode;

        private final FileAccess packageFileAccess;

        private boolean dirty;

        ContentTypeHelper(ZipArchive zipArchive, FileMode packageFileMode, FileAccess packageFileAccess) {
            this.zipArchive = zipArchive;
            this.packageFileAccess = packageFileAccess;
            this.packageFileMode = packageFileMode;

            if (zipArchive.getMode() == ZipArchiveMode.Read || zipArchive.getMode() == ZipArchiveMode.Update) {
                ZipArchiveEntry contentTypeEntry = zipArchive.getEntry(ENTRY_NAME);
                Assert.notNull(contentTypeEntry, "contentTypeEntry");
                parseContentTypeFile(contentTypeEntry);
            }
        }

        private void parseContentTypeFile(ZipArchiveEntry contentTypeEntry) {
            String xml = new String(contentTypeEntry.getContent(), StandardCharsets.UTF_8);
            Document doc = Xml.parseString(xml);
            Xml.lookupElements(doc, "/*[local-name()='Types']/*[local-name()='Default']").forEach(def -> {
                addDefaultElement(def.attributeValue("Extension"), new ContentType(def.attributeValue("ContentType")));
            });
            Xml.lookupElements(doc, "/*[local-name()='Types']/*[local-name()='Override']").forEach(override ->
                addOverrideElement(
                    PackUriHelper.validatePartUri(override.attributeValue("PartName")),
                    new ContentType(override.attributeValue("ContentType"))
                )
            );
        }

        public void saveToFile() {
            if (!this.dirty) {
                return;
            }

            Document document = Xml.newDocument();

            Element types = Xml.createElement(CONTENT_TYPES_NS, "Types");
            document.add(types);

            defaultDictionary.forEach((extension, contentType) -> {
                Element def = Xml.createElement("Default");
                def.addAttribute("Extension", extension);
                def.addAttribute("ContentType", contentType.getOriginalString());
                types.add(def);
            });

            overrideDictionary.forEach((uri, contentType) -> {
                Element override = Xml.createElement("Override");
                override.addAttribute("PartName", uri.getUri().toString());
                override.addAttribute("ContentType", contentType.getOriginalString());
                types.add(override);
            });

            byte[] bytes = Xml.toBytes(document);
            zipArchive.createEntry(ENTRY_NAME).setContent(bytes);
        }

        public void addContentType(PackUriHelper.ValidatedPartUri validatedPartUri, ContentType contentType) {
            String extension = validatedPartUri.partUriExtension();
            if (extension.isEmpty()) {
                addOverrideElement(validatedPartUri, contentType);
            } else if (!defaultDictionary.containsKey(extension)) {
                addDefaultElement(extension, contentType);
            } else if (!defaultDictionary.get(extension).areTypeAndSubTypeEqual(contentType)) {
                addOverrideElement(validatedPartUri, contentType);
            } else if (!overrideDictionary.containsKey(validatedPartUri)) {
                addOverrideElement(validatedPartUri, contentType);
            }
        }

        public void deleteContentType(PackUriHelper.ValidatedPartUri validatedPartUri) {
            if (this.overrideDictionary.remove(validatedPartUri) != null) {
                this.dirty = true;
            }
        }

        private void addDefaultElement(String extension, ContentType contentType) {
            this.defaultDictionary.put(extension, contentType);
            this.dirty = true;
        }

        private void addOverrideElement(PackUriHelper.ValidatedPartUri validatedPartUri, ContentType contentType) {
            this.deleteContentType(validatedPartUri);
            this.overrideDictionary.put(validatedPartUri, contentType);
            this.dirty = true;
        }

        public ContentType getContentType(PackUriHelper.ValidatedPartUri validatedPartUri) {
            if (overrideDictionary.containsKey(validatedPartUri)) {
                return overrideDictionary.get(validatedPartUri);
            }

            String extension = Uris.getExtension(validatedPartUri.toString());
            if (defaultDictionary.containsKey(extension)) {
                return defaultDictionary.get(extension);
            }

            return null;
        }
    }
}
