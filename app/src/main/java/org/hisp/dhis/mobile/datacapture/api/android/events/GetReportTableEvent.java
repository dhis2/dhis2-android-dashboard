package org.hisp.dhis.mobile.datacapture.api.android.events;

public final class GetReportTableEvent {
    private String mId;

    public void setId(String id) {
        mId = id;
    }

    public String getId() {
        return mId;
    }
}
