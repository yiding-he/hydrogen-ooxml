package com.hyd.ms.io.packaging;

import lombok.Data;

import java.io.Closeable;
import java.time.temporal.Temporal;

@Data
public abstract class PackageProperties implements Closeable {

    private String subject;

    private String revision;

    private Temporal modified;

    private Temporal lastPrinted;

    private String lastModifiedBy;

    private String language;

    private String keywords;

    private String title;

    private String identifier;

    private String creator;

    private Temporal created;

    private String contentType;

    private String contentStatus;

    private String category;

    private String description;

    private String version;

}
