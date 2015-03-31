package org.hisp.dhis.mobile.datacapture.api.android.events;

public final class DashboardDeleteEvent {
    private int mDashboardDbId;

    public int getDashboardDbId() {
        return mDashboardDbId;
    }

    public void setDashboardDbId(int dashboardId) {
        mDashboardDbId = dashboardId;
    }
}
