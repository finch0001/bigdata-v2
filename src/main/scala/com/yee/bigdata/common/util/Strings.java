package com.yee.bigdata.common.util;

import java.util.ArrayList;
import java.util.List;

public class Strings {

    /**
     * Splits a backslash escaped string on the separator.
     * <p>
     * Current backslash escaping supported:
     * <br> \n \t \r \b \f are escaped the same as a Java String
     * <br> Other characters following a backslash are produced verbatim (\c =&gt; c)
     *
     * @param s         the string to split
     * @param separator the separator to split on
     * @param decode    decode backslash escaping
     */
    public static List<String> splitSmart(String s, String separator, boolean decode) {
        ArrayList<String> lst = new ArrayList<>(2);
        StringBuilder sb = new StringBuilder();
        int pos = 0, end = s.length();
        while (pos < end) {
            if (s.startsWith(separator, pos)) {
                if (sb.length() > 0) {
                    lst.add(sb.toString());
                    sb = new StringBuilder();
                }
                pos += separator.length();
                continue;
            }

            char ch = s.charAt(pos++);
            if (ch == '\\') {
                if (!decode) sb.append(ch);
                if (pos >= end) break;  // ERROR, or let it go?
                ch = s.charAt(pos++);
                if (decode) {
                    switch (ch) {
                        case 'n':
                            ch = '\n';
                            break;
                        case 't':
                            ch = '\t';
                            break;
                        case 'r':
                            ch = '\r';
                            break;
                        case 'b':
                            ch = '\b';
                            break;
                        case 'f':
                            ch = '\f';
                            break;
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

}
