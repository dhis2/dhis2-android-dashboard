package org.hisp.dhis.mobile.datacapture.api.android.events;

public final class DashboardItemDeleteEvent {
    private int mDashboardDbId;
    private int mDashboardItemDbId;

    public int getDashboardDbId() {
        return mDashboardDbId;
    }

    public void setDashboardDbId(int dashboardDbId) {
        mDashboardDbId = dashboardDbId;
    }

    public int getDashboardItemDbId() {
        return mDashboardItemDbId;
    }

    public void setDashboardItemDbId(int dashboardItemDbId) {
        mDashboardItemDbId = dashboardItemDbId;
    }
}
