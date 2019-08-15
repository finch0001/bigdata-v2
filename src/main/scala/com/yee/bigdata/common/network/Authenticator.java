package com.yee.bigdata.common.network;

import com.yee.bigdata.common.errors.AppLogicException;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.security.Principal;

/**
 * Authentication for Channel
 */
public interface Authenticator extends Closeable {

    /**
     * Configures Authenticator using the provided parameters.
     *
     * @param transportLayer The transport layer used to read or write tokens
     * @param principalBuilder The builder used to construct `Principal`
     * @param configs Additional configuration parameters as key/value pairs
     */
    //void configure(TransportLayer transportLayer, PrincipalBuilder principalBuilder, Map<String, ?> configs);

    /**
     * Implements any authentication mechanism. Use transportLayer to read or write tokens.
     * If no further authentication needs to be done returns.
     */
    void authenticate() throws IOException;

    /**
     * Returns Principal using PrincipalBuilder
     */
    Principal principal() throws AppLogicException;

    /**
     * returns true if authentication is complete otherwise returns false;
     */
    boolean complete();

}