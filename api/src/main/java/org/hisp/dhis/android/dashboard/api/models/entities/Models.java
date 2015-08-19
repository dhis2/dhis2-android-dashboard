package org.hisp.dhis.android.dashboard.api.models.entities;

import org.hisp.dhis.android.dashboard.api.models.entities.common.IStore;
import org.hisp.dhis.android.dashboard.api.models.entities.dashboard.Dashboard;
import org.hisp.dhis.android.dashboard.api.models.entities.dashboard.DashboardElement;
import org.hisp.dhis.android.dashboard.api.models.entities.dashboard.DashboardElementStore;
import org.hisp.dhis.android.dashboard.api.models.entities.dashboard.DashboardItem;
import org.hisp.dhis.android.dashboard.api.models.entities.dashboard.DashboardItemStore;
import org.hisp.dhis.android.dashboard.api.models.entities.dashboard.DashboardStore;

/**
 * Created by arazabishov on 8/18/15.
 */
public final class Models {
    private static Models models;

    private final IStore<Dashboard> dashboardRepository;
    private final IStore<DashboardItem> dashboardItemRepository;
    private final IStore<DashboardElement> dashboardElementRepository;

    public Models() {
        dashboardRepository = new DashboardStore();
        dashboardItemRepository = new DashboardItemStore();
        dashboardElementRepository = new DashboardElementStore();
    }

    private static Models getInstance() {
        if (models == null) {
            models = new Models();
        }

        return models;
    }

    public static IStore<Dashboard> dashboards() {
        return getInstance().dashboardRepository;
    }

    public static IStore<DashboardItem> dashboardItems() {
        return getInstance().dashboardItemRepository;
    }

    public static IStore<DashboardElement> dashboardElements() {
        return getInstance().dashboardElementRepository;
    }
}
