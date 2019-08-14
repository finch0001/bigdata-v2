package com.yee.bigdata.common.util;

import java.io.DataInputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Properties;

public class Random {

    /** length in bytes of an ID */
    public static final int ID_LENGTH = 16;
    private static final Object idLock = new Object();

    // Holds 128 bit unsigned value:
    private static BigInteger nextId;
    private static final BigInteger mask128;

    static {
        // 128 bit unsigned mask
        byte[] maskBytes128 = new byte[16];
        Arrays.fill(maskBytes128, (byte) 0xff);
        mask128 = new BigInteger(1, maskBytes128);

        String prop = System.getProperty("tests.seed");

        // State for xorshift128:
        long x0;
        long x1;

        if (prop != null) {
            // So if there is a test failure that somehow relied on this id,
            // we remain reproducible based on the test seed:
            if (prop.length() > 8) {
                prop = prop.substring(prop.length() - 8);
            }
            x0 = Long.parseLong(prop, 16);
            x1 = x0;
        } else {
            // seed from /dev/urandom, if its available
            try (DataInputStream is = new DataInputStream(Files.newInputStream(Paths.get("/dev/urandom")))) {
                x0 = is.readLong();
                x1 = is.readLong();
            } catch (Exception unavailable) {
                // may not be available on this platform
                // fall back to lower quality randomness from 3 different sources:
                x0 = System.nanoTime();
                x1 = Random.class.hashCode() << 32;

                StringBuilder sb = new StringBuilder();
                // Properties can vary across JVM instances:
                try {
                    Properties p = System.getProperties();
                    for (String s : p.stringPropertyNames()) {
                        sb.append(s);
                        sb.append(p.getProperty(s));
                    }
                    x1 |= sb.toString().hashCode();
                } catch (SecurityException notallowed) {
                    // getting Properties requires wildcard read-write: may not be allowed
                    x1 |= StringBuffer.class.hashCode();
                }
            }
        }

        // Use a few iterations of xorshift128 to scatter the seed
        // in case multiple Lucene instances starting up "near" the same
        // nanoTime, since we use ++ (mod 2^128) for full period cycle:
        for (int i = 0; i < 10; i++) {
            long s1 = x0;
            long s0 = x1;
            x0 = s0;
            s1 ^= s1 << 23; // a
            x1 = s1 ^ s0 ^ (s1 >>> 17) ^ (s0 >>> 26); // b, c
        }

        // 64-bit unsigned mask
        byte[] maskBytes64 = new byte[8];
        Arrays.fill(maskBytes64, (byte) 0xff);
        BigInteger mask64 = new BigInteger(1, maskBytes64);

        // First make unsigned versions of x0, x1:
        BigInteger unsignedX0 = BigInteger.valueOf(x0).and(mask64);
        BigInteger unsignedX1 = BigInteger.valueOf(x1).and(mask64);

        // Concatentate bits of x0 and x1, as unsigned 128 bit integer:
        nextId = unsignedX0.shiftLeft(64).or(unsignedX1);
    }

    /**
     * Helper method to render an ID as a string, for debugging
     * <p>
     * Returns the string {@code (null)} if the id is null.
     * Otherwise, returns a string representation for debugging.
     * Never throws an exception. The returned string may
     * indicate if the id is definitely invalid.
     */
    public static String idToString(byte id[]) {
        if (id == null) {
            return "(null)";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(new BigInteger(1, id).toString(Character.MAX_RADIX));
            if (id.length != ID_LENGTH) {
                sb.append(" (INVALID FORMAT)");
            }
            return sb.toString();
        }
    }

    /** Generates a non-cryptographic globally unique id. */
    public static byte[] randomId() {

        // NOTE: we don't use Java's UUID.randomUUID() implementation here because:
        //
        //   * It's overkill for our usage: it tries to be cryptographically
        //     secure, whereas for this use we don't care if someone can
        //     guess the IDs.
        //
        //   * It uses SecureRandom, which on Linux can easily take a long time
        //     (I saw ~ 10 seconds just running a Lucene test) when entropy
        //     harvesting is falling behind.
        //
        //   * It loses a few (6) bits to version and variant and it's not clear
        //     what impact that has on the period, whereas the simple ++ (mod 2^128)
        //     we use here is guaranteed to have the full period.

        byte bits[];
        synchronized(idLock) {
            bits = nextId.toByteArray();
            nextId = nextId.add(BigInteger.ONE).and(mask128);
        }

        // toByteArray() always returns a sign bit, so it may require an extra byte (always zero)
        if (bits.length > ID_LENGTH) {
            assert bits.length == ID_LENGTH + 1;
            assert bits[0] == 0;
            return ArrayUtil.copyOfSubArray(bits, 1, bits.length);
        } else {
            byte[] result = new byte[ID_LENGTH];
            System.arraycopy(bits, 0, result, result.length - bits.length, bits.length);
            return result;
        }
    }

    /**
     * Generate a random id with a timestamp, in the format:
     * <code>hex(timestamp) + 'T' + randomId</code>.
     * @param time value representing timestamp
     */
    public static final String timeRandomId(long time) {
        StringBuilder sb = new StringBuilder(Long.toHexString(time));
        sb.append('T');
        sb.append(idToString(randomId()));
        return sb.toString();
    }
}
