package com.yee.bigdata.common.errors;

public class AppLogicException extends RetriableException {
    public AppLogicException(String message, IllegalAccessException cause) {
        super(message, cause);
    }

    public AppLogicException(String message, InstantiationException cause) {
        super(message, cause);
    }

    public AppLogicException(String message, NullPointerException cause) {
        super(message, cause);
    }

    public AppLogicException(String message) { super(message); }
}
