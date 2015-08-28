package org.hisp.dhis.android.dashboard.api.api;

import org.hisp.dhis.android.dashboard.api.controllers.DashboardController;
import org.hisp.dhis.android.dashboard.api.controllers.InterpretationController;
import org.hisp.dhis.android.dashboard.api.controllers.common.IDataController;
import org.hisp.dhis.android.dashboard.api.controllers.user.IUserAccountController;
import org.hisp.dhis.android.dashboard.api.controllers.user.UserAccountController;
import org.hisp.dhis.android.dashboard.api.models.dashboard.Dashboard;
import org.hisp.dhis.android.dashboard.api.models.interpretation.Interpretation;
import org.hisp.dhis.android.dashboard.api.network.DhisApi;

/**
 * Created by arazabishov on 8/28/15.
 */
final class Controllers {
    private static Controllers controllers;

    private final IDataController<Dashboard> dashboardController;
    private final IDataController<Interpretation> interpretationController;
    private final IUserAccountController userAccountController;

    private Controllers(DhisApi dhisApi) {
        dashboardController = new DashboardController(dhisApi, Models.dashboards(),
                Models.dashboardItems(), Models.dashboardElements());
        interpretationController = new InterpretationController(dhisApi,
                Services.interpretations(), Services.userAccount());
        userAccountController = new UserAccountController(dhisApi, Models.userAccount());
    }

    public static void init(DhisApi dhisApi) {
        controllers = new Controllers(dhisApi);
    }

    public static void reset() {
        controllers = null;
    }

    private static Controllers getInstance() {
        if (controllers == null) {
            throw new IllegalArgumentException("You have to call init() method first");
        }

        return controllers;
    }

    public static IDataController<Dashboard> dashboards() {
        return getInstance().dashboardController;
    }

    public static IDataController<Interpretation> interpretations() {
        return getInstance().interpretationController;
    }

    public static IUserAccountController userAccount() {
        return getInstance().userAccountController;
    }
}
