package org.hisp.dhis.mobile.datacapture.api.android.events;

public final class DashboardCreateEvent {
    private String mDashboardName;

    public void setDashboardName(String dashboardName) {
        mDashboardName = dashboardName;
    }

    public String getDashboardName() {
        return mDashboardName;
    }
}
