package org.hisp.dhis.android.dashboard.api.persistence.preferences;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsManager {
    public static final String CHART_WIDTH = "key:chart_width";
    public static final String CHART_HEIGHT = "key:chart_height";
    public static final String DEFAULT_WIDTH = "480";
    public static final String DEFAULT_HEIGHT = "320";
    private static final String PREFERENCES = "preferences:settings";
    private static SettingsManager mSettingsManager = null;
    private SharedPreferences mPrefs;

    public SettingsManager(Context context) {
        mPrefs = context.getSharedPreferences(SettingsManager.PREFERENCES,
                Context.MODE_PRIVATE);
    }

    public static SettingsManager getInstance(Context context) {
        if (mSettingsManager == null) {
            mSettingsManager = new SettingsManager(context);
        }
        return mSettingsManager;
    }

    public void setPreference(String key, String value) {
        mPrefs.edit().putString(key, value).commit();
    }

    public String getPreference(String key, String defaultValue) {
        return mPrefs.getString(key, defaultValue);
    }


}
