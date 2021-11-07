package com.hyd.ooxml.packaging;

import com.hyd.ms.io.Stream;
import com.hyd.ms.io.packaging.PackagePart;
import com.hyd.ooxml.ApplicationType;
import com.hyd.ooxml.OpenXmlPartRootElement;
import com.hyd.utilities.assertion.Assert;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;

public abstract class OpenXmlPart extends OpenXmlPartContainer {

    private static final String DEFAULT_TARGET_EXT = "xml";

    private OpenXmlPackage openXmlPackage;

    private PackagePart packagePart;

    private URI uri;

    //////////////////////////

    public OpenXmlPackage getOpenXmlPackage() {
        return openXmlPackage;
    }

    public URI getUri() {
        return uri;
    }

    public String getContentType() {
        return this.packagePart.getContentType();
    }

    @Override
    protected OpenXmlPackage getInternalOpenXmlPackage() {
        return openXmlPackage;
    }

    public Stream getStream() {
        return this.packagePart.getStream();
    }

    public void createInternal2(OpenXmlPackage openXmlPackage, OpenXmlPart parent, String contentType, URI partUri) {
        // TODO implement com.hyd.ooxml.packaging.OpenXmlPart.createInternal2()
    }

    public void feedData(Stream stream) {
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

        URI parentUri = parent == null ? URI.create("/") : parent.getUri();
        // noinspection ConstantConditions
        String targetPath = getTargetPath(openXmlPackage, getTargetPath());
        if (targetPath == null) {
            targetPath = ".";
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
        if (internalRootElement != null) {
            internalRootElement.setOpenXmlPart(null);
        }

        setInternalRootElement(partRootElement);
    }

    @Override
    protected OpenXmlPart getThisOpenXmlPart() {
        return this;
    }

    ////////////////////////// target path and file name

    public abstract String getTargetPath();

    public abstract String getTargetName();

    protected String getTargetFileExtension() {
        return DEFAULT_TARGET_EXT;
    }

    //////////////////////////

    public abstract String getRelationshipType();
}
