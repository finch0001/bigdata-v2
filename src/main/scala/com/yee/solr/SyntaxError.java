package com.yee.solr;


/** Simple checked exception for parsing errors */
public class SyntaxError extends Exception {
    public SyntaxError(String msg) {
        super(msg);
    }
    public SyntaxError(String msg, Throwable cause) {
        super(msg, cause);
    }
    public SyntaxError(Throwable cause) {
        super(cause);
    }
}
