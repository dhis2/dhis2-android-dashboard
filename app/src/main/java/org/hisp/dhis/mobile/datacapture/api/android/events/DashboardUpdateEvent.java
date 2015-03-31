package org.hisp.dhis.mobile.datacapture.api.android.events;

public class DashboardUpdateEvent {
    private int mDataBaseId;
    private String mName;

    public int getDataBaseId() {
        return mDataBaseId;
    }

    public void setDataBaseId(int dataBaseId) {
        mDataBaseId = dataBaseId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }
}
