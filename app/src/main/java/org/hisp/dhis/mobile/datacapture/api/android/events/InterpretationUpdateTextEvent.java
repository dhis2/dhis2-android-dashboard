package org.hisp.dhis.mobile.datacapture.api.android.events;

public class InterpretationUpdateTextEvent {
    private String mText;
    private int mDbId;

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public int getDbId() {
        return mDbId;
    }

    public void setDbId(int dbId) {
        mDbId = dbId;
    }
}
