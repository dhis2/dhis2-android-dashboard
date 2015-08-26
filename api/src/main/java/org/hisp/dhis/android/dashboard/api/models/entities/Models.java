package org.hisp.dhis.android.dashboard.api.models.entities;

import org.hisp.dhis.android.dashboard.api.models.entities.dashboard.DashboardElementStore;
import org.hisp.dhis.android.dashboard.api.models.entities.dashboard.DashboardItemContentStore;
import org.hisp.dhis.android.dashboard.api.models.entities.dashboard.DashboardItemStore;
import org.hisp.dhis.android.dashboard.api.models.entities.dashboard.DashboardStore;
import org.hisp.dhis.android.dashboard.api.models.entities.dashboard.IDashboardElementStore;
import org.hisp.dhis.android.dashboard.api.models.entities.dashboard.IDashboardItemContentStore;
import org.hisp.dhis.android.dashboard.api.models.entities.dashboard.IDashboardItemStore;
import org.hisp.dhis.android.dashboard.api.models.entities.dashboard.IDashboardStore;
import org.hisp.dhis.android.dashboard.api.models.entities.user.IUserAccountStore;
import org.hisp.dhis.android.dashboard.api.models.entities.user.UserAccountStore;

/**
 * Created by arazabishov on 8/18/15.
 */
public final class Models {
    private static Models models;

    // Dashboard store objects
    private final IDashboardStore dashboardRepository;
    private final IDashboardItemStore dashboardItemRepository;
    private final IDashboardElementStore dashboardElementRepository;
    private final IDashboardItemContentStore dashboardItemContentStore;

    // User store object
    private final IUserAccountStore userAccountStore;

    public Models() {
        dashboardRepository = new DashboardStore();
        dashboardItemRepository = new DashboardItemStore();
        dashboardElementRepository = new DashboardElementStore();
        dashboardItemContentStore = new DashboardItemContentStore();
        userAccountStore = new UserAccountStore();
    }

    private static Models getInstance() {
        if (models == null) {
            models = new Models();
        }

        return models;
    }

    public static IDashboardStore dashboards() {
        return getInstance().dashboardRepository;
    }

    public static IDashboardItemStore dashboardItems() {
        return getInstance().dashboardItemRepository;
    }

    public static IDashboardElementStore dashboardElements() {
        return getInstance().dashboardElementRepository;
    }

    public static IDashboardItemContentStore dashboardItemContent() {
        return getInstance().dashboardItemContentStore;
    }

    public static IUserAccountStore userAccount() {
        return getInstance().userAccountStore;
    }
}
