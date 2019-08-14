package com.yee.bigdata.common.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class DateTimeUtils {

    private static final ThreadLocal<Map<TimeZone, Calendar>> TIMEZONE_CALENDARS =
            ThreadLocal.withInitial(HashMap::new);

    private static final ThreadLocal<Map<TimeZone, SimpleDateFormat>> TIMEZONE_DATE_FORMATS =
            ThreadLocal.withInitial(HashMap::new);

    private static final ThreadLocal<Map<TimeZone, SimpleDateFormat>> TIMEZONE_TIME_FORMATS =
            ThreadLocal.withInitial(HashMap::new);

    private static final ThreadLocal<Map<TimeZone, SimpleDateFormat>> TIMEZONE_TIMESTAMP_FORMATS =
            ThreadLocal.withInitial(HashMap::new);

    public static Calendar getTimeZoneCalendar(final TimeZone timeZone) {
        return TIMEZONE_CALENDARS.get().computeIfAbsent(timeZone, GregorianCalendar::new);
    }

    public static String formatDate(Date date, TimeZone timeZone) {
        return TIMEZONE_DATE_FORMATS.get().computeIfAbsent(timeZone, aTimeZone -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setTimeZone(aTimeZone);
            return sdf;
        }).format(date);
    }

    public static String formatTime(Date date, TimeZone timeZone) {
        return TIMEZONE_TIME_FORMATS.get().computeIfAbsent(timeZone, aTimeZone -> {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
            sdf.setTimeZone(aTimeZone);
            return sdf;
        }).format(date);
    }

    public static String formatTimestamp(Date date, TimeZone timeZone) {
        return TIMEZONE_TIMESTAMP_FORMATS.get().computeIfAbsent(timeZone, aTimeZone -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            sdf.setTimeZone(aTimeZone);
            return sdf;
        }).format(date);
    }

    private DateTimeUtils() {
    }
}
