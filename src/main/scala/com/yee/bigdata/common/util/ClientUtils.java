package com.yee.bigdata.common.util;

import com.yee.bigdata.common.errors.AppLogicException;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class ClientUtils {
    public static final String NL = System.getProperty("line.separator");

    // This matches URIs of formats: host:port and protocol:\\host:port
    // IPv6 is supported with [ip] pattern
    private static final Pattern HOST_PORT_PATTERN = Pattern.compile(".*?\\[?([0-9a-zA-Z\\-%.:]*)\\]?:([0-9]+)");

    /**
     * Extracts the hostname from a "host:port" address string.
     * @param address address string to parse
     * @return hostname or null if the given address is incorrect
     */
    public static String getHost(String address) {
        Matcher matcher = HOST_PORT_PATTERN.matcher(address);
        return matcher.matches() ? matcher.group(1) : null;
    }

    /**
     * Extracts the port number from a "host:port" address string.
     * @param address address string to parse
     * @return port number or null if the given address is incorrect
     */
    public static Integer getPort(String address) {
        Matcher matcher = HOST_PORT_PATTERN.matcher(address);
        return matcher.matches() ? Integer.parseInt(matcher.group(2)) : null;
    }

    public static List<InetSocketAddress> parseAndValidateAddresses(List<String> urls) {
        List<InetSocketAddress> addresses = new ArrayList<InetSocketAddress>();
        for (String url : urls) {
            if (url != null && url.length() > 0) {
                String host = getHost(url);
                Integer port = getPort(url);
                if (host == null || port == null)
                    System.out.println("invalid host and port!");
                    //throw new ConfigException("Invalid url in " + CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG + ": " + url);
                try {
                    InetSocketAddress address = new InetSocketAddress(host, port);
                    if (address.isUnresolved()) {
                        System.out.println("address is unresolved!");
                        // log.warn("Removing server from " + CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG + " as DNS resolution failed: " + url);
                    } else {
                        addresses.add(address);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("invalid port");
                    //throw new ConfigException("Invalid port in " + CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG + ": " + url);
                }
            }
        }
        if (addresses.size() < 1)
            System.out.println("No resolvable bootstrap urls");
            //throw new ConfigException("No resolvable bootstrap urls given in " + CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG);
        return addresses;
    }

    /**
     * Turn the given UTF8 byte array into a string
     *
     * @param bytes The byte array
     * @return The string
     */
    public static String utf8(byte[] bytes) {
        try {
            return new String(bytes, "UTF8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("This shouldn't happen.", e);
        }
    }

    /**
     * Turn a string into a utf8 byte[]
     *
     * @param string The string
     * @return The byte[]
     */
    public static byte[] utf8(String string) {
        try {
            return string.getBytes("UTF8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("This shouldn't happen.", e);
        }
    }

    /**
     * Get a sorted list representation of a collection.
     * @param collection The collection to sort
     * @param <T> The class of objects in the collection
     * @return An unmodifiable sorted list with the contents of the collection
     */
    public static <T extends Comparable<? super T>> List<T> sorted(Collection<T> collection) {
        List<T> res = new ArrayList<>(collection);
        Collections.sort(res);
        return Collections.unmodifiableList(res);
    }

    /**
     * Read an unsigned integer from the current position in the buffer, incrementing the position by 4 bytes
     *
     * @param buffer The buffer to read from
     * @return The integer read, as a long to avoid signedness
     */
    public static long readUnsignedInt(ByteBuffer buffer) {
        return buffer.getInt() & 0xffffffffL;
    }

    /**
     * Read an unsigned integer from the given position without modifying the buffers position
     *
     * @param buffer the buffer to read from
     * @param index the index from which to read the integer
     * @return The integer read, as a long to avoid signedness
     */
    public static long readUnsignedInt(ByteBuffer buffer, int index) {
        return buffer.getInt(index) & 0xffffffffL;
    }

    /**
     * Read an unsigned integer stored in little-endian format from the {@link InputStream}.
     *
     * @param in The stream to read from
     * @return The integer read (MUST BE TREATED WITH SPECIAL CARE TO AVOID SIGNEDNESS)
     */
    public static int readUnsignedIntLE(InputStream in) throws IOException {
        return (in.read() << 8 * 0)
                | (in.read() << 8 * 1)
                | (in.read() << 8 * 2)
                | (in.read() << 8 * 3);
    }

    /**
     * Get the little-endian value of an integer as a byte array.
     * @param val The value to convert to a little-endian array
     * @return The little-endian encoded array of bytes for the value
     */
    public static byte[] toArrayLE(int val) {
        return new byte[] {
                (byte) (val >> 8 * 0),
                (byte) (val >> 8 * 1),
                (byte) (val >> 8 * 2),
                (byte) (val >> 8 * 3)
        };
    }


    /**
     * Read an unsigned integer stored in little-endian format from a byte array
     * at a given offset.
     *
     * @param buffer The byte array to read from
     * @param offset The position in buffer to read from
     * @return The integer read (MUST BE TREATED WITH SPECIAL CARE TO AVOID SIGNEDNESS)
     */
    public static int readUnsignedIntLE(byte[] buffer, int offset) {
        return (buffer[offset++] << 8 * 0)
                | (buffer[offset++] << 8 * 1)
                | (buffer[offset++] << 8 * 2)
                | (buffer[offset]   << 8 * 3);
    }

    /**
     * Write the given long value as a 4 byte unsigned integer. Overflow is ignored.
     *
     * @param buffer The buffer to write to
     * @param value The value to write
     */
    public static void writeUnsignedInt(ByteBuffer buffer, long value) {
        buffer.putInt((int) (value & 0xffffffffL));
    }

    /**
     * Write the given long value as a 4 byte unsigned integer. Overflow is ignored.
     *
     * @param buffer The buffer to write to
     * @param index The position in the buffer at which to begin writing
     * @param value The value to write
     */
    public static void writeUnsignedInt(ByteBuffer buffer, int index, long value) {
        buffer.putInt(index, (int) (value & 0xffffffffL));
    }

    /**
     * Write an unsigned integer in little-endian format to the {@link OutputStream}.
     *
     * @param out The stream to write to
     * @param value The value to write
     */
    public static void writeUnsignedIntLE(OutputStream out, int value) throws IOException {
        out.write(value >>> 8 * 0);
        out.write(value >>> 8 * 1);
        out.write(value >>> 8 * 2);
        out.write(value >>> 8 * 3);
    }

    /**
     * Write an unsigned integer in little-endian format to a byte array
     * at a given offset.
     *
     * @param buffer The byte array to write to
     * @param offset The position in buffer to write to
     * @param value The value to write
     */
    public static void writeUnsignedIntLE(byte[] buffer, int offset, int value) {
        buffer[offset++] = (byte) (value >>> 8 * 0);
        buffer[offset++] = (byte) (value >>> 8 * 1);
        buffer[offset++] = (byte) (value >>> 8 * 2);
        buffer[offset]   = (byte) (value >>> 8 * 3);
    }

    /**
     * Get the length for UTF8-encoding a string without encoding it first
     *
     * @param s The string to calculate the length for
     * @return The length when serialized
     */
    public static int utf8Length(CharSequence s) {
        int count = 0;
        for (int i = 0, len = s.length(); i < len; i++) {
            char ch = s.charAt(i);
            if (ch <= 0x7F) {
                count++;
            } else if (ch <= 0x7FF) {
                count += 2;
            } else if (Character.isHighSurrogate(ch)) {
                count += 4;
                ++i;
            } else {
                count += 3;
            }
        }
        return count;
    }

    /**
     * Read the given byte buffer into a byte array
     */
    public static byte[] toArray(ByteBuffer buffer) {
        return toArray(buffer, 0, buffer.limit());
    }

    /**
     * Read a byte array from the given offset and size in the buffer
     */
    public static byte[] toArray(ByteBuffer buffer, int offset, int size) {
        byte[] dest = new byte[size];
        if (buffer.hasArray()) {
            System.arraycopy(buffer.array(), buffer.arrayOffset() + offset, dest, 0, size);
        } else {
            int pos = buffer.position();
            buffer.get(dest);
            buffer.position(pos);
        }
        return dest;
    }

    /**
     * Check that the parameter t is not null
     *
     * @param t The object to check
     * @return t if it isn't null
     * @throws NullPointerException if t is null.
     */
    public static <T> T notNull(T t) {
        if (t == null)
            throw new NullPointerException();
        else
            return t;
    }

    /**
     * Sleep for a bit
     * @param ms The duration of the sleep
     */
    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            // this is okay, we just wake up early
        }
    }

    /**
     * Instantiate the class
     */
    public static <T> T newInstance(Class<T> c) {
        try {
            return c.newInstance();
        } catch (IllegalAccessException e) {
            throw new AppLogicException("Could not instantiate class " + c.getName(), e);
        } catch (InstantiationException e) {
            throw new AppLogicException("Could not instantiate class " + c.getName() + " Does it have a public no-argument constructor?", e);
        } catch (NullPointerException e) {
            throw new AppLogicException("Requested class was null", e);
        }
    }

    /**
     * Look up the class by name and instantiate it.
     * @param klass class name
     * @param base super class of the class to be instantiated
     * @param <T>
     * @return the new instance
     */
    public static <T> T newInstance(String klass, Class<T> base) throws ClassNotFoundException {
        return newInstance(Class.forName(klass, true, getContextOrClientUtilsClassLoader()).asSubclass(base));
    }

    /**
     * Get the Context ClassLoader on this thread or, if not present, the ClassLoader that
     * loaded Kafka.
     *
     * This should be used whenever passing a ClassLoader to Class.forName
     */
    public static ClassLoader getContextOrClientUtilsClassLoader() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null)
            return getClientUtilsClassLoader();
        else
            return cl;
    }

    /**
     * Get the ClassLoader which loaded client utils.
     */
    public static ClassLoader getClientUtilsClassLoader() {
        return ClientUtils.class.getClassLoader();
    }

    /**
     * Attempts to move source to target atomically and falls back to a non-atomic move if it fails.
     *
     * @throws IOException if both atomic and non-atomic moves fail
     */
    public static void atomicMoveWithFallback(Path source, Path target) throws IOException {
        try {
            Files.move(source, target, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException outer) {
            try {
                Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Non-atomic move of " + source + " to " + target + " succeeded after atomic move failed due to " + outer.getMessage());
                //log.debug("Non-atomic move of " + source + " to " + target + " succeeded after atomic move failed due to " + outer.getMessage());
            } catch (IOException inner) {
                inner.addSuppressed(outer);
                throw inner;
            }
        }
    }

    /**
     * Closes all the provided closeables.
     * @throws IOException if any of the close methods throws an IOException.
     *         The first IOException is thrown with subsequent exceptions
     *         added as suppressed exceptions.
     */
    public static void closeAll(Closeable... closeables) throws IOException {
        IOException exception = null;
        for (Closeable closeable : closeables) {
            try {
                closeable.close();
            } catch (IOException e) {
                if (exception != null)
                    exception.addSuppressed(e);
                else
                    exception = e;
            }
        }
        if (exception != null)
            throw exception;
    }

    /**
     * Returns the list of files at 'path' recursively. This skips files that are ignored normally
     * by MapReduce.
     */
    public static List<String> listDirectory(File path) throws IOException {
        List<String> result = new ArrayList<>();
        if (path.isDirectory()) {
            for (File f: path.listFiles()) {
                result.addAll(listDirectory(f));
            }
        } else {
            char c = path.getName().charAt(0);
            if (c != '.' && c != '_') {
                result.add(path.getAbsolutePath());
            }
        }
        return result;
    }

    /** Shuffle the elements in the given array. */
    public static <T> T[] shuffle(final T[] array) {
        if (array != null && array.length > 0) {
            for (int n = array.length; n > 1; ) {
                final int randomIndex = ThreadLocalRandom.current().nextInt(n);
                n--;
                if (n != randomIndex) {
                    final T tmp = array[randomIndex];
                    array[randomIndex] = array[n];
                    array[n] = tmp;
                }
            }
        }
        return array;
    }

    /**
     * Find a jar that contains a class of the same name, if any.
     * It will return a jar file, even if that is not the first thing
     * on the class path that has a class with the same name.
     *
     * @param clazz the class to find.
     * @return a jar file that contains the class, or null.
     * @throws IOException
     */
    public static String findContainingJar(Class<?> clazz) {
        ClassLoader loader = clazz.getClassLoader();
        String classFile = clazz.getName().replaceAll("\\.", "/") + ".class";
        try {
            for(final Enumeration<URL> itr = loader.getResources(classFile);
                itr.hasMoreElements();) {
                final URL url = itr.nextElement();
                if ("jar".equals(url.getProtocol())) {
                    String toReturn = url.getPath();
                    if (toReturn.startsWith("file:")) {
                        toReturn = toReturn.substring("file:".length());
                    }
                    toReturn = URLDecoder.decode(toReturn, "UTF-8");
                    return toReturn.replaceAll("!.*$", "");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * Cause the current thread to sleep as close as possible to the provided
     * number of milliseconds. This method will log and ignore any
     * {@link InterruptedException} encountered.
     *
     * @param millis the number of milliseconds for the current thread to sleep
     */
    public static void sleepAtLeastIgnoreInterrupts(long millis) {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < millis) {
            long timeToSleep = millis -
                    (System.currentTimeMillis() - start);
            try {
                Thread.sleep(timeToSleep);
            } catch (InterruptedException ie) {
                //LOG.warn("interrupted while sleeping", ie);
            }
        }
    }

    /**
     * Convenience method that returns a resource as inputstream from the
     * classpath.
     * <p>
     * Uses the Thread's context classloader to load resource.
     *
     * @param resourceName resource to retrieve.
     *
     * @throws IOException thrown if resource cannot be loaded
     * @return inputstream with the resource.
     */
    public static InputStream getResourceAsStream(String resourceName)
            throws IOException {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            throw new IOException("Can not read resource file '" + resourceName +
                    "' because class loader of the current thread is null");
        }
        return getResourceAsStream(cl, resourceName);
    }

    /**
     * Convenience method that returns a resource as inputstream from the
     * classpath using given classloader.
     * <p>
     *
     * @param cl ClassLoader to be used to retrieve resource.
     * @param resourceName resource to retrieve.
     *
     * @throws IOException thrown if resource cannot be loaded
     * @return inputstream with the resource.
     */
    public static InputStream getResourceAsStream(ClassLoader cl,
                                                  String resourceName)
            throws IOException {
        if (cl == null) {
            throw new IOException("Can not read resource file '" + resourceName +
                    "' because given class loader is null");
        }
        InputStream is = cl.getResourceAsStream(resourceName);
        if (is == null) {
            throw new IOException("Can not read resource file '" +
                    resourceName + "'");
        }
        return is;
    }

}
