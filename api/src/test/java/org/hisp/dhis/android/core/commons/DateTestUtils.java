package org.hisp.dhis.android.core.commons;

import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateTestUtils {
    public final static String DHIS2_GMT_NEW_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    public static Date parseDate(String date, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            return sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean compareParsedDateWithStringDate(DateTime date, String isDate) {
        return date.toDate().getTime() == (parseDate(isDate, DHIS2_GMT_NEW_DATE_FORMAT).getTime());
    }
}
