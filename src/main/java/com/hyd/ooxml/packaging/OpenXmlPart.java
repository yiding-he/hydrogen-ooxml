package com.hyd.ooxml.packaging;

import com.hyd.ms.io.Stream;
import com.hyd.ms.io.packaging.PackagePart;
import com.hyd.ms.io.packaging.PackageRelationship;
import com.hyd.ms.io.packaging.TargetMode;
import com.hyd.ooxml.ApplicationType;
import com.hyd.ooxml.OpenXmlPartRootElement;
import com.hyd.utilities.assertion.Assert;
import com.hyd.xml.XmlDocument;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;

import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.Map;

@Slf4j
public abstract class OpenXmlPart extends OpenXmlPartContainer {

    public static final String DEFAULT_TARGET_EXT = "xml";

    private OpenXmlPackage openXmlPackage;

    private PackagePart packagePart;

    private URI uri;

    private OpenXmlPartRootElement rootElement;

    //////////////////////////

    public void load(
        OpenXmlPackage openXmlPackage, OpenXmlPart parent, URI targetUri, String id,
        Map<URI, OpenXmlPart> loadedParts) {

        Assert.notNull(targetUri, "targetUri");
        Assert.notNull(id, "id");
        Assert.not(openXmlPackage == null && parent == null, "argument openXmlPackage and argument parent cannot both be null");
        Assert.not(openXmlPackage != null && parent != null && parent.getOpenXmlPackage() != openXmlPackage, "argument parent belongs to other package");

        if (openXmlPackage == null && parent != null) {
            openXmlPackage = parent.getOpenXmlPackage();
        }
        Assert.not(openXmlPackage == null, "cannot find value for argument openXmlPackage");

        this.openXmlPackage = openXmlPackage;
        this.uri = targetUri;
        this.packagePart = openXmlPackage.__package.getPart(targetUri);

        openXmlPackage.reserveUri(getContentType(), getUri());
        loadDomTree(getRootElementType());

        PackagePartRelationshipPropertyCollection rels = new PackagePartRelationshipPropertyCollection(getPackagePart());
        loadReferencedPartsAndRelationships(openXmlPackage, this, rels, loadedParts);
    }

    public PackagePart getPackagePart() {
        return packagePart;
    }

    public OpenXmlPackage getOpenXmlPackage() {
        return openXmlPackage;
    }

    public URI getUri() {
        return uri;
    }

    public Document getContent() {
        return getPartRootElement().getContent();
    }

    public XmlDocument getXmlDocument() {
        return XmlDocument.fromDocument(getContent());
    }

    public String getContentType() {
        return this.packagePart == null? null: this.packagePart.getContentType();
    }

    @Override
    protected OpenXmlPackage getInternalOpenXmlPackage() {
        return openXmlPackage;
    }

    public Stream getStream() {
        return this.packagePart.getStream();
    }

    public void createInternal2(OpenXmlPackage openXmlPackage, OpenXmlPart parent, String contentType, URI partUri) {
        throw new UnsupportedOperationException("Not implemented yet");
        // TODO implement com.hyd.ooxml.packaging.OpenXmlPart.createInternal2()
    }

    public void feedData(Stream stream) {
        throw new UnsupportedOperationException("Not implemented yet");
        // TODO implement com.hyd.ooxml.packaging.OpenXmlPart.feedData()
    }

    public boolean isRootElementLoaded() {
        return this.getInternalRootElement() != null;
    }

    public String getTargetPathOfWord() {
        return null;
    }

    public String getTargetPathOfExcel() {
        return null;
    }

    public String getTargetPathOfPpt() {
        return null;
    }

    //////////////////////////

    protected OpenXmlPartRootElement getInternalRootElement() {
        return this.rootElement;
    }

    protected void setInternalRootElement(OpenXmlPartRootElement rootElement) {
        this.rootElement = rootElement;
    }

    /**
     * Gets the root element of the current part.
     *
     * @return null if the part is not a defined XML part.
     */
    protected OpenXmlPartRootElement getPartRootElement() {
        return getInternalRootElement();
    }

    protected <T extends OpenXmlPartRootElement> void loadDomTree(Class<T> type) {
        Assert.that(getInternalRootElement() == null, "DOM tree already loaded");
        if (type == null || type == OpenXmlPartRootElement.NONE.class) {
            // Non XML format
            return;
        }
        try {
            T rootElement = type.newInstance();
            rootElement.loadFromPart(this, getStream());
            rootElement.setOpenXmlPart(this);
            setInternalRootElement(rootElement);
        } catch (Exception e) {
            throw new OpenXmlPackageException(e);
        }
    }

    public void createInternal(
        OpenXmlPackage openXmlPackage, OpenXmlPart parent, String contentType, String targetExt
    ) {
        Assert.not(
            openXmlPackage == null && parent == null,
            "openXmlPackage and parent cannot both be null"
        );
        Assert.not(
            openXmlPackage != null && parent != null && parent.getOpenXmlPackage() != openXmlPackage,
            "openXmlPackage and parent should belong to the same package"
        );

        if (parent != null && openXmlPackage == null) {
            openXmlPackage = parent.getOpenXmlPackage();
        }
        Assert.not(openXmlPackage == null, "cannot find openXmlPackage");
        Assert.not(this.packagePart != null, "this part is already initialized");

        this.openXmlPackage = openXmlPackage;

        URI parentUri = parent == null ? URI.create("/") : parent.getUri();
        // noinspection ConstantConditions
        String targetPath = getTargetPath(openXmlPackage, getTargetPath());
        if (targetPath == null) {
            targetPath = ".";
        }

        if (contentType == null) {
            contentType = getAnnotatedContentType();
        }

        String targetFileExt = StringUtils.defaultString(targetExt, getTargetFileExtension());
        this.uri = openXmlPackage.getUniquePartUri(contentType, parentUri, targetPath, getTargetName(), targetFileExt);
        this.packagePart = openXmlPackage.createMetroPart(this.uri, contentType);
    }

    private String getTargetPath(OpenXmlPackage openXmlPackage, String defaultPath) {
        String targetPath = null;
        if (openXmlPackage.getApplicationType() == ApplicationType.Word) {
            targetPath = getTargetPathOfWord();
        } else if (openXmlPackage.getApplicationType() == ApplicationType.Excel) {
            targetPath = getTargetPathOfExcel();
        } else if (openXmlPackage.getApplicationType() == ApplicationType.PowerPoint) {
            targetPath = getTargetPathOfPpt();
        }
        return StringUtils.defaultString(targetPath, defaultPath);
    }

    protected void setDomTree(OpenXmlPartRootElement partRootElement) {
        Assert.that(partRootElement.getOpenXmlPart() == null, "part root already has association");
        partRootElement.setOpenXmlPart(this);

        OpenXmlPartRootElement internalRootElement = getInternalRootElement();
        if (internalRootElement != null && internalRootElement != partRootElement) {
            internalRootElement.setOpenXmlPart(null);
        }

        setInternalRootElement(partRootElement);
    }

    @Override
    protected OpenXmlPart getThisOpenXmlPart() {
        return this;
    }

    private void ensureAnnotated(Class<? extends Annotation> annotationClass) {
        Assert.that(
            getClass().isAnnotationPresent(annotationClass),
            "class not annotated by %s: %s",
            annotationClass.getSimpleName(),
            this.getClass().getCanonicalName()
        );
    }

    public String getTargetPath() {
        ensureAnnotated(XmlPart.class);
        return getClass().getAnnotation(XmlPart.class).targetPath();
    }

    public String getTargetName() {
        ensureAnnotated(XmlPart.class);
        return getClass().getAnnotation(XmlPart.class).targetName();
    }

    public Class<? extends OpenXmlPartRootElement> getRootElementType() {
        ensureAnnotated(XmlPart.class);
        return getClass().getAnnotation(XmlPart.class).rootElementType();
    }

    protected String getTargetFileExtension() {
        if (getClass().isAnnotationPresent(XmlPart.class)) {
            return getClass().getAnnotation(XmlPart.class).targetExtension();
        } else {
            return DEFAULT_TARGET_EXT;
        }
    }

    public String getRelationshipType() {
        ensureAnnotated(RelationshipType.class);
        return getClass().getAnnotation(RelationshipType.class).value();
    }

    public String getAnnotatedContentType() {
        if (getClass().isAnnotationPresent(ContentType.class)) {
            return getClass().getAnnotation(ContentType.class).value();
        } else {
            return null;
        }
    }

    @Override
    protected PackageRelationship createRelationship(
        URI targetUri, TargetMode targetMode, String relationshipType) {
        return packagePart.createRelationship(targetUri, targetMode, relationshipType);
    }

    @Override
    protected PackageRelationship createRelationship(
        URI targetUri, TargetMode targetMode, String relationshipType, String id) {
        return packagePart.createRelationship(targetUri, targetMode, relationshipType, id);
    }

    //////////////////////////
}
