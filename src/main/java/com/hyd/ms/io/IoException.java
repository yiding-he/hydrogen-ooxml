package com.hyd.ms.io;

public class IoException extends RuntimeException {

    public IoException() {
    }

    public IoException(String message) {
        super(message);
    }

    public IoException(String message, Throwable cause) {
        super(message, cause);
    }

    public IoException(Throwable cause) {
        super(cause);
    }

    public IoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
