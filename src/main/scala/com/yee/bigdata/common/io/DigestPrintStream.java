package com.yee.bigdata.common.io;

import org.apache.commons.codec.binary.Base64;

import java.io.OutputStream;
import java.security.MessageDigest;

public class DigestPrintStream extends FetchConverter {

    private final MessageDigest digest;

    public DigestPrintStream(OutputStream out, String encoding) throws Exception {
        super(out, false, encoding);
        this.digest = MessageDigest.getInstance("MD5");
    }

    @Override
    protected void process(String out) {
        digest.update(out.getBytes());
    }

    @Override
    public void processFinal() {
        printDirect(new String(Base64.encodeBase64(digest.digest())));
        digest.reset();
    }
}
