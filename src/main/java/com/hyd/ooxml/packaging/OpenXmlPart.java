package com.hyd.ooxml.packaging;

import com.hyd.ms.io.Stream;
import com.hyd.ms.io.packaging.PackagePart;
import com.hyd.ms.io.packaging.PackageRelationship;
import com.hyd.ms.io.packaging.TargetMode;
import com.hyd.ooxml.ApplicationType;
import com.hyd.ooxml.OpenXmlPartRootElement;
import com.hyd.utilities.assertion.Assert;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.Map;

public abstract class OpenXmlPart extends OpenXmlPartContainer {

    private static final String DEFAULT_TARGET_EXT = "xml";

    private OpenXmlPackage openXmlPackage;

    private PackagePart packagePart;

    private URI uri;

    //////////////////////////

    public void load(
        OpenXmlPackage openXmlPackage, OpenXmlPart parent, URI targetUri, String id,
        Map<URI, OpenXmlPart> loadedParts) {
        // todo implement OpenXmlPart.load()
    }

    public OpenXmlPackage getOpenXmlPackage() {
        return openXmlPackage;
    }

    public URI getUri() {
        return uri;
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
        return null;
    }

    protected void setInternalRootElement(OpenXmlPartRootElement rootElement) {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets the root element of the current part.
     *
     * @return null if the part is not a defined XML part.
     */
    protected OpenXmlPartRootElement getPartRootElement() {
        return null;
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

    ////////////////////////// target path and file name

    protected String getTargetFileExtension() {
        return DEFAULT_TARGET_EXT;
    }

    //////////////////////////
}
