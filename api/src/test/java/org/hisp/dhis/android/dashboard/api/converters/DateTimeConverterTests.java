package org.hisp.dhis.android.core.converters;

import static junit.framework.Assert.assertTrue;

import org.hisp.dhis.android.core.commons.DateTestUtils;
import org.hisp.dhis.android.dashboard.api.persistence.converters.DateTimeConverter;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.TimeZone;

public class DateTimeConverterTests {

    public static final String DATETIME_AS_STRING = "2016-04-21T15:37:07.740Z";

    DateTimeConverter dateTimeConverter = new DateTimeConverter();

    DateTime dateTime;

    @Before
    public void setUp() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.set(Calendar.YEAR, 2016);
        calendar.set(Calendar.MONTH, 04);
        calendar.set(Calendar.DAY_OF_MONTH, 21);
        calendar.set(Calendar.HOUR_OF_DAY, 15);
        calendar.set(Calendar.MINUTE, 37);
        calendar.set(Calendar.SECOND, 07);
        calendar.set(Calendar.MILLISECOND, 740);
        dateTime = new DateTime(calendar);
    }

    @Test
    public void convert_datetime_string_to_object() throws Exception {
        DateTime convertedDate = dateTimeConverter.getModelValue(DATETIME_AS_STRING);

        assertTrue(DateTestUtils.compareParsedDateWithStringDate(convertedDate, DATETIME_AS_STRING));
    }

    @Test
    public void convert_datetime_object_to_string() throws Exception {
        String converterDate = dateTimeConverter.getDBValue(dateTime);

        assertTrue(DateTestUtils.compareParsedDateWithStringDate(dateTime, converterDate));
    }

}
