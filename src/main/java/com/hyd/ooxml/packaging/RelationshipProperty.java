package com.hyd.ooxml.packaging;

import com.hyd.ms.io.packaging.TargetMode;
import lombok.Data;

import java.net.URI;

@Data
public class RelationshipProperty {

    private String Id;

    private String RelationshipType;

    private TargetMode TargetMode;

    private URI TargetUri;
}
