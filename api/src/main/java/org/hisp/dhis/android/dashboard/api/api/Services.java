package org.hisp.dhis.android.dashboard.api.api;

import android.content.Context;

import org.hisp.dhis.android.dashboard.api.models.dashboard.DashboardElementService;
import org.hisp.dhis.android.dashboard.api.models.dashboard.DashboardItemService;
import org.hisp.dhis.android.dashboard.api.models.dashboard.DashboardService;
import org.hisp.dhis.android.dashboard.api.models.dashboard.IDashboardElementService;
import org.hisp.dhis.android.dashboard.api.models.dashboard.IDashboardItemService;
import org.hisp.dhis.android.dashboard.api.models.dashboard.IDashboardService;
import org.hisp.dhis.android.dashboard.api.models.interpretation.IInterpretationCommentService;
import org.hisp.dhis.android.dashboard.api.models.interpretation.IInterpretationElementService;
import org.hisp.dhis.android.dashboard.api.models.interpretation.IInterpretationService;
import org.hisp.dhis.android.dashboard.api.models.interpretation.InterpretationCommentService;
import org.hisp.dhis.android.dashboard.api.models.interpretation.InterpretationElementService;
import org.hisp.dhis.android.dashboard.api.models.interpretation.InterpretationService;
import org.hisp.dhis.android.dashboard.api.models.user.IUserAccountService;
import org.hisp.dhis.android.dashboard.api.models.user.UserAccountService;

/**
 * Created by arazabishov on 8/28/15.
 */
final class Services {
    private static Services services;

    private final IDashboardService dashboardService;
    private final IDashboardItemService dashboardItemService;
    private final IDashboardElementService dashboardElementService;

    private final IInterpretationService interpretationsService;
    private final IInterpretationElementService interpretationElementService;
    private final IInterpretationCommentService interpretationCommentService;

    private final IUserAccountService userAccountService;

    private Services(Context context) {
        Models.init(context);

        dashboardItemService = new DashboardItemService(Models.dashboardItems(), Models.dashboardElements());
        dashboardElementService = new DashboardElementService(Models.dashboardElements(), dashboardItemService);
        dashboardService = new DashboardService(Models.dashboards(), Models.dashboardItems(),
                Models.dashboardElements(), dashboardItemService, dashboardElementService);

        interpretationElementService = new InterpretationElementService();
        interpretationCommentService = new InterpretationCommentService(Models.interpretationComments());
        interpretationsService = new InterpretationService(Models.interpretations(), interpretationElementService);

        userAccountService = new UserAccountService(Models.userAccount(), Models.modelsStore());
    }

    public static void init(Context context) {
        if (services == null) {
            services = new Services(context);
        }
    }

    private static Services getInstance() {
        if (services == null) {
            throw new IllegalArgumentException("You have to call init() first");
        }

        return services;
    }

    public static IDashboardService dashboards() {
        return getInstance().dashboardService;
    }

    public static IDashboardItemService dashboardItems() {
        return getInstance().dashboardItemService;
    }

    public static IDashboardElementService dashboardElements() {
        return getInstance().dashboardElementService;
    }

    public static IInterpretationService interpretations() {
        return getInstance().interpretationsService;
    }

    public static IInterpretationElementService interpretationElements() {
        return getInstance().interpretationElementService;
    }

    public static IInterpretationCommentService interpretationComments() {
        return getInstance().interpretationCommentService;
    }

    public static IUserAccountService userAccount() {
        return getInstance().userAccountService;
    }
}
