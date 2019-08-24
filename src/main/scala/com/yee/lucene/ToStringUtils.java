package com.yee.lucene;



/**
 * Helper methods to ease implementing {@link Object#toString()}.
 */
public final class ToStringUtils {

    private ToStringUtils() {} // no instance

    public static void byteArray(StringBuilder buffer, byte[] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            buffer.append("b[").append(i).append("]=").append(bytes[i]);
            if (i < bytes.length - 1) {
                buffer.append(',');
            }

        }
    }

    private final static char [] HEX = "0123456789abcdef".toCharArray();

    public static String longHex(long x) {
        char [] asHex = new char [16];
        for (int i = 16; --i >= 0; x >>>= 4) {
            asHex[i] = HEX[(int) x & 0x0F];
        }
        return "0x" + new String(asHex);
    }

}
