package com.yee.bigdata.common.util;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * DateUtils. Thread-safe class
 *
 */
public class DateUtils {

    private static final ThreadLocal<SimpleDateFormat> dateFormatLocal = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            simpleDateFormat.setLenient(false);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            return simpleDateFormat;
        }
    };

    public static SimpleDateFormat getDateFormat() {
        return dateFormatLocal.get();
    }

    public static final int NANOS_PER_SEC = 1000000000;
    public static final BigDecimal MAX_INT_BD = new BigDecimal(Integer.MAX_VALUE);
    public static final BigDecimal NANOS_PER_SEC_BD = new BigDecimal(NANOS_PER_SEC);

    public static int parseNumericValueWithRange(String fieldName,
                                                 String strVal, int minValue, int maxValue) throws IllegalArgumentException {
        int result = 0;
        if (strVal != null) {
            result = Integer.parseInt(strVal);
            if (result < minValue || result > maxValue) {
                throw new IllegalArgumentException(String.format("%s value %d outside range [%d, %d]",
                        fieldName, result, minValue, maxValue));
            }
        }
        return result;
    }

    // From java.util.Calendar
    private static final String[] FIELD_NAME = {
            "ERA", "YEAR", "MONTH", "WEEK_OF_YEAR", "WEEK_OF_MONTH", "DAY_OF_MONTH",
            "DAY_OF_YEAR", "DAY_OF_WEEK", "DAY_OF_WEEK_IN_MONTH", "AM_PM", "HOUR",
            "HOUR_OF_DAY", "MINUTE", "SECOND", "MILLISECOND", "ZONE_OFFSET",
            "DST_OFFSET"
    };

    /**
     * Returns the name of the specified calendar field.
     *
     * @param field the calendar field
     * @return the calendar field name
     * @exception IndexOutOfBoundsException if <code>field</code> is negative,
     * equal to or greater then <code>FIELD_COUNT</code>.
     */
    public static String getFieldName(int field) {
        return FIELD_NAME[field];
    }
}

