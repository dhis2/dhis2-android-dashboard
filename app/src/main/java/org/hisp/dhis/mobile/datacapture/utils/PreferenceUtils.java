package org.hisp.dhis.mobile.datacapture.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtils {
    private static final String USER_DATA = "userData";

    private PreferenceUtils() { }

    public static void put(Context context, String key, String value) {
        SharedPreferences.Editor userData = context.getSharedPreferences(
                USER_DATA, Context.MODE_PRIVATE
        ).edit();

        userData.putString(key, value);
        userData.commit();
    }

    public static String get(Context context, String key) {
        return context.getSharedPreferences(
                USER_DATA, Context.MODE_PRIVATE
        ).getString(key, null);
    }

    public static void remove(Context context, String key) {
        context.getSharedPreferences(
                USER_DATA, Context.MODE_PRIVATE
        ).edit().remove(key).commit();
    }
}
