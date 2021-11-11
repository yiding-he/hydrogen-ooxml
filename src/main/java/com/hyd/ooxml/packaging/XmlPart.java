package com.hyd.ooxml.packaging;

import com.hyd.ooxml.OpenXmlPartRootElement;
import com.hyd.ooxml.generated.BasicPartRootElement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface XmlPart {

    String targetName();

    String targetPath();

    String targetExtension() default OpenXmlPart.DEFAULT_TARGET_EXT;

    Class<? extends OpenXmlPartRootElement> rootElementType() default BasicPartRootElement.class;
}
