package org.hisp.dhis.android.dashboard.api.api;

import org.hisp.dhis.android.dashboard.api.controllers.common.IDataController;
import org.hisp.dhis.android.dashboard.api.models.dashboard.Dashboard;
import org.hisp.dhis.android.dashboard.api.models.dashboard.DashboardItem;
import org.hisp.dhis.android.dashboard.api.models.dashboard.DashboardItemContent;
import org.hisp.dhis.android.dashboard.api.network.APIException;
import org.hisp.dhis.android.dashboard.api.models.dashboard.IDashboardService;

/**
 * Created by arazabishov on 8/28/15.
 */
public final class DashboardHandler implements IDataController<Dashboard>, IDashboardService {
    private final IDataController<Dashboard> dataController;
    private final IDashboardService dashboardService;

    public DashboardHandler(IDataController<Dashboard> dataController,
                            IDashboardService dashboardService) {
        this.dataController = dataController;
        this.dashboardService = dashboardService;
    }

    @Override
    public Dashboard createDashboard(String name) {
        return dashboardService.createDashboard(name);
    }

    @Override
    public void updateDashboardName(Dashboard dashboard, String name) {
        dashboardService.updateDashboardName(dashboard, name);
    }

    @Override
    public boolean addDashboardContent(Dashboard dashboard, DashboardItemContent content) {
        return dashboardService.addDashboardContent(dashboard, content);
    }

    @Override
    public DashboardItem getAvailableItemByType(Dashboard dashboard, String type) {
        return dashboardService.getAvailableItemByType(dashboard, type);
    }

    @Override
    public void sync() throws APIException {
        dataController.sync();
    }
}
