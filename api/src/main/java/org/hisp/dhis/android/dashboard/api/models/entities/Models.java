package org.hisp.dhis.android.dashboard.api.models.entities;

import org.hisp.dhis.android.dashboard.api.models.entities.common.IStore;
import org.hisp.dhis.android.dashboard.api.models.entities.dashboard.DashboardElement;
import org.hisp.dhis.android.dashboard.api.models.entities.dashboard.DashboardElementStore;
import org.hisp.dhis.android.dashboard.api.models.entities.dashboard.DashboardItem;
import org.hisp.dhis.android.dashboard.api.models.entities.dashboard.DashboardItemStore;
import org.hisp.dhis.android.dashboard.api.models.entities.dashboard.DashboardStore;
import org.hisp.dhis.android.dashboard.api.models.entities.dashboard.IDashboardElementStore;
import org.hisp.dhis.android.dashboard.api.models.entities.dashboard.IDashboardItemStore;
import org.hisp.dhis.android.dashboard.api.models.entities.dashboard.IDashboardStore;

/**
 * Created by arazabishov on 8/18/15.
 */
public final class Models {
    private static Models models;

    private final IDashboardStore dashboardRepository;
    private final IDashboardItemStore dashboardItemRepository;
    private final IDashboardElementStore dashboardElementRepository;

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

    public static IDashboardStore dashboards() {
        return getInstance().dashboardRepository;
    }

    public static IDashboardItemStore dashboardItems() {
        return getInstance().dashboardItemRepository;
    }

    public static IStore<DashboardElement> dashboardElements() {
        return getInstance().dashboardElementRepository;
    }
}
