package org.hisp.dhis.mobile.datacapture.api.android.models;

public class DateHolder {
    private final String mLabel;
    private final String mDate;

    public DateHolder(String date, String label) {
        mDate = date;
        mLabel = label;
    }

    public String getLabel() {
        return mLabel;
    }

    public String getDate() {
        return mDate;
    }
}
