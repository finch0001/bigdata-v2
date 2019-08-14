package com.yee.bigdata.common.util;

/**
 * Date parser class for Hive.
 */
public class DateParser {

    public DateParser() {
    }

    public Date parseDate(String strValue) {
        Date result = new Date();
        if (parseDate(strValue, result)) {
            return result;
        }
        return null;
    }

    public boolean parseDate(String strValue, Date result) {
        Date parsedVal;
        try {
            parsedVal = Date.valueOf(strValue);
        } catch (IllegalArgumentException e) {
            parsedVal = null;
        }
        if (parsedVal == null) {
            return false;
        }
        result.setTimeInMillis(parsedVal.toEpochMilli());
        return true;
    }
}
