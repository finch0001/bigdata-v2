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
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class ClientUtils {
    public static final String NL = System.getProperty("line.separator");

    // This matches URIs of formats: host:port and protocol:\\host:port
    // IPv6 is supported with [ip] pattern
    private static final Pattern HOST_PORT_PATTERN = Pattern.compile(".*?\\[?([0-9a-zA-Z\\-%.:]*)\\]?:([0-9]+)");

    private static final char[] hexchars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

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

    /**
     * equals function that actually compares two buffers.
     *
     * @param onearray First buffer
     * @param twoarray Second buffer
     * @return true if one and two contain exactly the same content, else false.
     */
    public static boolean bufEquals(byte onearray[], byte twoarray[] ) {
        if (onearray == twoarray) return true;
        boolean ret = (onearray.length == twoarray.length);
        if (!ret) {
            return ret;
        }
        for (int idx = 0; idx < onearray.length; idx++) {
            if (onearray[idx] != twoarray[idx]) {
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @param s
     * @return
     */
    public static String toXMLString(String s) {
        if (s == null)
            return "";

        StringBuilder sb = new StringBuilder();
        for (int idx = 0; idx < s.length(); idx++) {
            char ch = s.charAt(idx);
            if (ch == '<') {
                sb.append("&lt;");
            } else if (ch == '&') {
                sb.append("&amp;");
            } else if (ch == '%') {
                sb.append("%25");
            } else if (ch < 0x20) {
                sb.append("%");
                sb.append(hexchars[ch/16]);
                sb.append(hexchars[ch%16]);
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    /**
     *
     * @param s
     * @return
     */
    public static String fromXMLString(String s) {
        StringBuilder sb = new StringBuilder();
        for (int idx = 0; idx < s.length();) {
            char ch = s.charAt(idx++);
            if (ch == '%') {
                char ch1 = s.charAt(idx++);
                char ch2 = s.charAt(idx++);
                char res = (char)(h2c(ch1)*16 + h2c(ch2));
                sb.append(res);
            } else {
                sb.append(ch);
            }
        }

        return sb.toString();
    }

    static private int h2c(char ch) {
        if (ch >= '0' && ch <= '9') {
            return ch - '0';
        } else if (ch >= 'A' && ch <= 'F') {
            return ch - 'A';
        } else if (ch >= 'a' && ch <= 'f') {
            return ch - 'a';
        }
        return 0;
    }

    /**
     *
     * @param s
     * @return
     */
    public static String toCSVString(String s) {
        if (s == null)
            return "";

        StringBuilder sb = new StringBuilder(s.length()+1);
        sb.append('\'');
        int len = s.length();
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            switch(c) {
                case '\0':
                    sb.append("%00");
                    break;
                case '\n':
                    sb.append("%0A");
                    break;
                case '\r':
                    sb.append("%0D");
                    break;
                case ',':
                    sb.append("%2C");
                    break;
                case '}':
                    sb.append("%7D");
                    break;
                case '%':
                    sb.append("%25");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     *
     * @param s
     * @throws java.io.IOException
     * @return
     */
    public static String fromCSVString(String s) throws IOException {
        if (s.charAt(0) != '\'') {
            throw new IOException("Error deserializing string.");
        }
        int len = s.length();
        StringBuilder sb = new StringBuilder(len-1);
        for (int i = 1; i < len; i++) {
            char c = s.charAt(i);
            if (c == '%') {
                char ch1 = s.charAt(i+1);
                char ch2 = s.charAt(i+2);
                i += 2;
                if (ch1 == '0' && ch2 == '0') { sb.append('\0'); }
                else if (ch1 == '0' && ch2 == 'A') { sb.append('\n'); }
                else if (ch1 == '0' && ch2 == 'D') { sb.append('\r'); }
                else if (ch1 == '2' && ch2 == 'C') { sb.append(','); }
                else if (ch1 == '7' && ch2 == 'D') { sb.append('}'); }
                else if (ch1 == '2' && ch2 == '5') { sb.append('%'); }
                else {throw new IOException("Error deserializing string.");}
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     *
     * @param barr
     * @return
     */
    static String toXMLBuffer(byte barr[]) {
        if (barr == null || barr.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(2*barr.length);
        for (int idx = 0; idx < barr.length; idx++) {
            sb.append(Integer.toHexString(barr[idx]));
        }
        return sb.toString();
    }

    /**
     *
     * @param s
     * @throws java.io.IOException
     * @return
     */
    static byte[] fromXMLBuffer(String s)
            throws IOException {
        ByteArrayOutputStream stream =  new ByteArrayOutputStream();
        if (s.length() == 0) { return stream.toByteArray(); }
        int blen = s.length()/2;
        byte[] barr = new byte[blen];
        for (int idx = 0; idx < blen; idx++) {
            char c1 = s.charAt(2*idx);
            char c2 = s.charAt(2*idx+1);
            barr[idx] = Byte.parseByte(""+c1+c2, 16);
        }
        stream.write(barr);
        return stream.toByteArray();
    }

    /**
     *
     * @param barr
     * @return
     */
    public static String toCSVBuffer(byte barr[]) {
        if (barr == null || barr.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(barr.length + 1);
        sb.append('#');
        for(int idx = 0; idx < barr.length; idx++) {
            sb.append(Integer.toHexString(barr[idx]));
        }
        return sb.toString();
    }

    /**
     * Converts a CSV-serialized representation of buffer to a new
     * ByteArrayOutputStream.
     * @param s CSV-serialized representation of buffer
     * @throws java.io.IOException
     * @return Deserialized ByteArrayOutputStream
     */
    public static byte[] fromCSVBuffer(String s)
            throws IOException {
        if (s.charAt(0) != '#') {
            throw new IOException("Error deserializing buffer.");
        }
        ByteArrayOutputStream stream =  new ByteArrayOutputStream();
        if (s.length() == 1) { return stream.toByteArray(); }
        int blen = (s.length()-1)/2;
        byte[] barr = new byte[blen];
        for (int idx = 0; idx < blen; idx++) {
            char c1 = s.charAt(2*idx+1);
            char c2 = s.charAt(2*idx+2);
            barr[idx] = Byte.parseByte(""+c1+c2, 16);
        }
        stream.write(barr);
        return stream.toByteArray();
    }

    public static int compareBytes(byte b1[], int off1, int len1, byte b2[], int off2, int len2) {
        int i;
        for(i=0; i < len1 && i < len2; i++) {
            if (b1[off1+i] != b2[off2+i]) {
                return b1[off1+i] < b2[off2+i] ? -1 : 1;
            }
        }
        if (len1 != len2) {
            return len1 < len2 ? -1 : 1;
        }
        return 0;
    }


    public static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    public static List<String> splitSmart(String s, char separator) {
        ArrayList<String> lst = new ArrayList<>(4);
        splitSmart(s, separator, lst);
        return lst;

    }

    public static List<String> splitSmart(String s, char separator, boolean trimEmpty) {
        List<String> l = splitSmart(s, separator);
        if(trimEmpty){
            if (l.size() > 0 && l.get(0).isEmpty()) l.remove(0);
        }
        return l;
    }

    /**
     * Split a string based on a separator, but don't split if it's inside
     * a string.  Assume '\' escapes the next char both inside and
     * outside strings.
     */
    public static void splitSmart(String s, char separator, List<String> lst) {
        int pos=0, start=0, end=s.length();
        char inString=0;
        char ch=0;
        while (pos < end) {
            char prevChar=ch;
            ch = s.charAt(pos++);
            if (ch=='\\') {    // skip escaped chars
                pos++;
            } else if (inString != 0 && ch==inString) {
                inString=0;
            } else if (ch=='\'' || ch=='"') {
                // If char is directly preceeded by a number or letter
                // then don't treat it as the start of a string.
                // Examples: 50" TV, or can't
                if (!Character.isLetterOrDigit(prevChar)) {
                    inString=ch;
                }
            } else if (ch==separator && inString==0) {
                lst.add(s.substring(start,pos-1));
                start=pos;
            }
        }
        if (start < end) {
            lst.add(s.substring(start,end));
        }

        /***
         if (SolrCore.log.isLoggable(Level.FINEST)) {
         SolrCore.log.trace("splitCommand=" + lst);
         }
         ***/

    }

    /** Splits a backslash escaped string on the separator.
     * <p>
     * Current backslash escaping supported:
     * <br> \n \t \r \b \f are escaped the same as a Java String
     * <br> Other characters following a backslash are produced verbatim (\c =&gt; c)
     *
     * @param s  the string to split
     * @param separator the separator to split on
     * @param decode decode backslash escaping
     * @return not null
     */
    public static List<String> splitSmart(String s, String separator, boolean decode) {
        ArrayList<String> lst = new ArrayList<>(2);
        StringBuilder sb = new StringBuilder();
        int pos=0, end=s.length();
        while (pos < end) {
            if (s.startsWith(separator,pos)) {
                if (sb.length() > 0) {
                    lst.add(sb.toString());
                    sb=new StringBuilder();
                }
                pos+=separator.length();
                continue;
            }

            char ch = s.charAt(pos++);
            if (ch=='\\') {
                if (!decode) sb.append(ch);
                if (pos>=end) break;  // ERROR, or let it go?
                ch = s.charAt(pos++);
                if (decode) {
                    switch(ch) {
                        case 'n' : ch='\n'; break;
                        case 't' : ch='\t'; break;
                        case 'r' : ch='\r'; break;
                        case 'b' : ch='\b'; break;
                        case 'f' : ch='\f'; break;
                    }
                }
            }

            sb.append(ch);
        }

        if (sb.length() > 0) {
            lst.add(sb.toString());
        }

        return lst;
    }

    /**
     * Splits file names separated by comma character.
     * File names can contain comma characters escaped by backslash '\'
     *
     * @param fileNames the string containing file names
     * @return a list of file names with the escaping backslashed removed
     */
    public static List<String> splitFileNames(String fileNames) {
        if (fileNames == null)
            return Collections.<String>emptyList();

        List<String> result = new ArrayList<>();
        for (String file : fileNames.split("(?<!\\\\),")) {
            result.add(file.replaceAll("\\\\(?=,)", ""));
        }

        return result;
    }

    /**
     * Creates a backslash escaped string, joining all the items.
     * @see #escapeTextWithSeparator
     */
    public static String join(Collection<?> items, char separator) {
        if (items == null) return "";
        StringBuilder sb = new StringBuilder(items.size() << 3);
        boolean first=true;
        for (Object o : items) {
            String item = String.valueOf(o);
            if (first) {
                first = false;
            } else {
                sb.append(separator);
            }
            appendEscapedTextToBuilder(sb, item, separator);
        }
        return sb.toString();
    }



    public static List<String> splitWS(String s, boolean decode) {
        ArrayList<String> lst = new ArrayList<>(2);
        StringBuilder sb = new StringBuilder();
        int pos=0, end=s.length();
        while (pos < end) {
            char ch = s.charAt(pos++);
            if (Character.isWhitespace(ch)) {
                if (sb.length() > 0) {
                    lst.add(sb.toString());
                    sb=new StringBuilder();
                }
                continue;
            }

            if (ch=='\\') {
                if (!decode) sb.append(ch);
                if (pos>=end) break;  // ERROR, or let it go?
                ch = s.charAt(pos++);
                if (decode) {
                    switch(ch) {
                        case 'n' : ch='\n'; break;
                        case 't' : ch='\t'; break;
                        case 'r' : ch='\r'; break;
                        case 'b' : ch='\b'; break;
                        case 'f' : ch='\f'; break;
                    }
                }
            }

            sb.append(ch);
        }

        if (sb.length() > 0) {
            lst.add(sb.toString());
        }

        return lst;
    }

    public static List<String> toLower(List<String> strings) {
        ArrayList<String> ret = new ArrayList<>(strings.size());
        for (String str : strings) {
            ret.add(str.toLowerCase(Locale.ROOT));
        }
        return ret;
    }



    /** Return if a string starts with '1', 't', or 'T'
     *  and return false otherwise.
     */
    public static boolean parseBoolean(String s) {
        char ch = s.length()>0 ? s.charAt(0) : 0;
        return (ch=='1' || ch=='t' || ch=='T');
    }

    /** how to transform a String into a boolean... more flexible than
     * Boolean.parseBoolean() to enable easier integration with html forms.
     */
    public static boolean parseBool(String s) {
        if( s != null ) {
            if( s.startsWith("true") || s.startsWith("on") || s.startsWith("yes") ) {
                return true;
            }
            if( s.startsWith("false") || s.startsWith("off") || s.equals("no") ) {
                return false;
            }
        }
        return true;//throw new Exception("invalid boolean value: ");
    }

    /**
     * {@link NullPointerException} and {@link } free version of {@link #parseBool(String)}
     * @return parsed boolean value (or def, if s is null or invalid)
     */
    public static boolean parseBool(String s, boolean def) {
        if( s != null ) {
            if( s.startsWith("true") || s.startsWith("on") || s.startsWith("yes") ) {
                return true;
            }
            if( s.startsWith("false") || s.startsWith("off") || s.equals("no") ) {
                return false;
            }
        }
        return def;
    }

    /**
     * URLEncodes a value, replacing only enough chars so that
     * the URL may be unambiguously pasted back into a browser.
     * <p>
     * Characters with a numeric value less than 32 are encoded.
     * &amp;,=,%,+,space are encoded.
     */
    public static void partialURLEncodeVal(Appendable dest, String val) throws IOException {
        for (int i=0; i<val.length(); i++) {
            char ch = val.charAt(i);
            if (ch < 32) {
                dest.append('%');
                if (ch < 0x10) dest.append('0');
                dest.append(Integer.toHexString(ch));
            } else {
                switch (ch) {
                    case ' ': dest.append('+'); break;
                    case '&': dest.append("%26"); break;
                    case '%': dest.append("%25"); break;
                    case '=': dest.append("%3D"); break;
                    case '+': dest.append("%2B"); break;
                    default : dest.append(ch); break;
                }
            }
        }
    }

    /**
     * Creates a new copy of the string with the separator backslash escaped.
     * @see #join
     */
    public static String escapeTextWithSeparator(String item, char separator) {
        StringBuilder sb = new StringBuilder(item.length() * 2);
        appendEscapedTextToBuilder(sb, item, separator);
        return sb.toString();
    }

    /**
     * writes chars from item to out, backslash escaping as needed based on separator --
     * but does not append the separator itself
     */
    public static void appendEscapedTextToBuilder(StringBuilder out,
                                                  String item,
                                                  char separator) {
        for (int i = 0; i < item.length(); i++) {
            char ch = item.charAt(i);
            if (ch == '\\' || ch == separator) {
                out.append('\\');
            }
            out.append(ch);
        }
    }

    /**Format using MesssageFormat but with the ROOT locale
     */
    public static String formatString(String pattern, Object... args)  {
        return new MessageFormat(pattern, Locale.ROOT).format(args);
    }

    /**
     * Cast value to a an int, or throw an exception
     * if there is an overflow.
     *
     * @param value a long to be casted to an int
     * @return an int that is == to value
     * @throws IllegalArgumentException if value can't be casted to an int
     * @deprecated replaced by {@link java.lang.Math#toIntExact(long)}
     */
    public static int checkedCast(long value) {
        int valueI = (int) value;
        if (valueI != value) {
            throw new IllegalArgumentException(String.format("Overflow casting %d to an int", value));
        }
        return valueI;
    }

    public static String getCallLocationName(int depth) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        if (stackTrace.length <= depth) {
            return "<unknown>";
        }

        StackTraceElement elem = stackTrace[depth];

        return String.format("%s(%s:%d)", elem.getMethodName(), elem.getFileName(), elem.getLineNumber());
    }

    public static String getCallLocationName() {
        return getCallLocationName(4);
    }

}
