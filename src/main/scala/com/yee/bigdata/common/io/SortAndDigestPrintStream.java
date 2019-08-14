package com.yee.bigdata.common.io;


import org.apache.commons.codec.binary.Base64;

import java.io.OutputStream;
import java.security.MessageDigest;

public class SortAndDigestPrintStream extends SortPrintStream {

    private final MessageDigest digest;

    public SortAndDigestPrintStream(OutputStream out, String encoding) throws Exception {
        super(out, encoding);
        this.digest = MessageDigest.getInstance("MD5");
    }

    @Override
    public void processFinal() {
        while (!outputs.isEmpty()) {
            String row = outputs.removeFirst();
            digest.update(row.getBytes());
            printDirect(row);
        }
        printDirect(new String(Base64.encodeBase64(digest.digest())));
        digest.reset();
    }
}
