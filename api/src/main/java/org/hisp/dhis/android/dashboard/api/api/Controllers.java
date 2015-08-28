package org.hisp.dhis.android.dashboard.api.api;

import org.hisp.dhis.android.dashboard.api.controllers.DashboardController;

/**
 * Created by arazabishov on 8/28/15.
 */
final class Controllers {
    private static Controllers controllers;

    private final DashboardController dashboardController;

    private Controllers() {
        // empty constructor
        dashboardController = new DashboardController(null);
    }

    private static Controllers getInstance() {
        if (controllers == null) {
            controllers = new Controllers();
        }

        return controllers;
    }


}
