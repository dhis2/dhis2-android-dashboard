package org.hisp.dhis.mobile.datacapture.utils;

import android.text.TextUtils;

import org.joda.time.DateTime;

public class DateTimeTypeAdapter {
    private DateTimeTypeAdapter() {
    }

    public static String serializeDateTime(DateTime value) {
        if (value != null) {
            return value.toString();
        } else {
            return null;
        }
    }

    public static DateTime deserializeDateTime(String value) {
        if (!TextUtils.isEmpty(value)) {
            return DateTime.parse(value);
        } else {
            return null;
        }
    }
}
