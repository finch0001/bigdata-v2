package com.yee.bigdata.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLUtil {
    public final static Pattern URL_PREFIX = Pattern.compile("^([a-z]*?://).*");

    public static String removeScheme(String url) {
        Matcher matcher = URL_PREFIX.matcher(url);
        if (matcher.matches()) {
            return url.substring(matcher.group(1).length());
        }

        return url;
    }

    public static boolean hasScheme(String url) {
        Matcher matcher = URL_PREFIX.matcher(url);
        return matcher.matches();
    }

    public static String getScheme(String url) {
        Matcher matcher = URL_PREFIX.matcher(url);
        if (matcher.matches()) {
            return matcher.group(1);
        }

        return null;
    }
}
