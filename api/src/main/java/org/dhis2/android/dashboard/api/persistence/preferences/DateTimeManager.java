package org.dhis2.android.dashboard.api.persistence.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.TimeZone;

import static org.dhis2.android.dashboard.api.utils.Preconditions.isNull;

public final class DateTimeManager {
    private static final String PREFERENCES = "preferences:lastUpdated";
    private static final String METADATA_UPDATE_DATETIME = "key:metaDataUpdateDateTime";
    private static final String SERVER_TIME_ZONE = "key:serverTimeZone";

    private static DateTimeManager mPreferences;
    private final SharedPreferences mPrefs;

    public static void init(Context context) {
        mPreferences = new DateTimeManager(context);
    }

    public static DateTimeManager getInstance() {
        if (mPreferences == null) {
            throw new IllegalArgumentException("You have to call init() method first");
        }

        return mPreferences;
    }

    private DateTimeManager(Context context) {
        isNull(context, "Context object must not be null");
        mPrefs = context.getSharedPreferences(PREFERENCES,
                Context.MODE_PRIVATE);
    }

    /**
     * Method which returns DateTime object in server
     * time zone (if set previously), otherwise returns DateTime in current TimeZone
     *
     * @return DateTime object
     */
    public DateTime getCurrentDateTimeInServerTimeZone() {
        if (isServerTimeZoneSet()) {
            TimeZone serverTimeZone = getServerTimeZone();
            return new DateTime(DateTimeZone.forTimeZone(serverTimeZone));
        }

        return new DateTime();
    }

    public void setLastUpdated(DateTime dateTime) {
        isNull(dateTime, "DateTime object must not be null");

        if (!dateTime.getZone().toTimeZone().hasSameRules(getServerTimeZone())) {
            throw new IllegalArgumentException("TimeZone of lastUpdated field is " +
                    "different from the server's one");
        }

        putString(METADATA_UPDATE_DATETIME, dateTime.toString());
    }

    public DateTime getLastUpdated() {
        String dateTimeString = getString(METADATA_UPDATE_DATETIME);
        if (dateTimeString != null) {
            return DateTime.parse(dateTimeString);
        }
        return null;
    }

    public void deleteLastUpdated() {
        deleteString(METADATA_UPDATE_DATETIME);
    }

    public boolean isLastUpdatedSet() {
        return getLastUpdated() != null;
    }

    public void setServerTimeZone(TimeZone timeZone) {
        isNull(timeZone, "TimeZone object must not be null");
        putString(SERVER_TIME_ZONE, timeZone.toString());
    }

    public TimeZone getServerTimeZone() {
        String timeZone = getString(SERVER_TIME_ZONE);
        if (timeZone != null) {
            return TimeZone.getTimeZone(timeZone);
        }
        return null;
    }

    public void deleteServerTimeZone() {
        deleteString(SERVER_TIME_ZONE);
    }

    public boolean isServerTimeZoneSet() {
        return getServerTimeZone() != null;
    }

    private void putString(String key, String value) {
        mPrefs.edit().putString(key, value).commit();
    }

    private String getString(String key) {
        return mPrefs.getString(key, null);
    }

    private void deleteString(String key) {
        mPrefs.edit().remove(key).commit();
    }
}