package org.hisp.dhis.android.dashboard.api.api;

import android.content.Context;

import org.hisp.dhis.android.dashboard.api.controllers.DashboardController;
import org.hisp.dhis.android.dashboard.api.models.Models;
import org.hisp.dhis.android.dashboard.api.models.dashboard.DashboardService;

/**
 * Created by arazabishov on 8/28/15.
 */
public final class Dhis2 {
    private static Dhis2 dhis2;

    private final DashboardHandler dashboardHandler;

    private Dhis2(Context context) {
        Models.init(context);

        dashboardHandler = new DashboardHandler(new DashboardController(null),
                new DashboardService(null, null));
    }

    public static void init(Context context) {
        if (dhis2 == null) {
            dhis2 = new Dhis2(context);
        }
    }

    private static Dhis2 getInstance() {
        if (dhis2 == null) {
            throw new IllegalArgumentException("You have to call init() method first.");
        }

        return dhis2;
    }

    public static DashboardHandler dashboards() {
        return getInstance().dashboardHandler;
    }
}
