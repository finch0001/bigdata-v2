package com.yee.bigdata.common.errors;

/**
 * Any API exception that is part of the public protocol and should be a subclass of this class and be part of this
 * package.
 */
public class ApiException extends  RuntimeException{

    private static final long serialVersionUID = 1L;

    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApiException(String message) {
        super(message);
    }

    public ApiException(Throwable cause) {
        super(cause);
    }

    public ApiException() {
        super();
    }

    /* avoid the expensive and useless stack trace for api exceptions */
    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

}