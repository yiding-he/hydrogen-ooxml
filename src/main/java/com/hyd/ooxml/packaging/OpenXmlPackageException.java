package com.hyd.ooxml.packaging;

public class OpenXmlPackageException extends RuntimeException {

    public OpenXmlPackageException() {
    }

    public OpenXmlPackageException(String message) {
        super(message);
    }

    public OpenXmlPackageException(String message, Throwable cause) {
        super(message, cause);
    }

    public OpenXmlPackageException(Throwable cause) {
        super(cause);
    }

    public OpenXmlPackageException(
        String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
