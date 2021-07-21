package com.hyd.ooxml.packaging;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MarkupCompatibilityProcessSettings {

    private MarkupCompatibilityProcessMode processMode;

    private FileFormatVersions targetFileFormatVersions;
}
