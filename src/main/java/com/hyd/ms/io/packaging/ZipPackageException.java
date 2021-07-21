package com.hyd.ms.io.packaging;

public class ZipPackageException extends RuntimeException {

    public ZipPackageException() {
    }

    public ZipPackageException(String message) {
        super(message);
    }

    public ZipPackageException(String message, Throwable cause) {
        super(message, cause);
    }

    public ZipPackageException(Throwable cause) {
        super(cause);
    }

    public ZipPackageException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
