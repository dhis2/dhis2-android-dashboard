package org.hisp.dhis.android.dashboard.api.api;

import org.hisp.dhis.android.dashboard.api.controllers.common.IDataController;
import org.hisp.dhis.android.dashboard.api.models.dashboard.Dashboard;
import org.hisp.dhis.android.dashboard.api.models.dashboard.DashboardElement;
import org.hisp.dhis.android.dashboard.api.models.dashboard.DashboardItem;
import org.hisp.dhis.android.dashboard.api.models.dashboard.DashboardItemContent;
import org.hisp.dhis.android.dashboard.api.models.dashboard.IDashboardElementService;
import org.hisp.dhis.android.dashboard.api.models.dashboard.IDashboardItemService;
import org.hisp.dhis.android.dashboard.api.models.dashboard.IDashboardService;
import org.hisp.dhis.android.dashboard.api.network.APIException;

/**
 * Created by arazabishov on 8/28/15.
 */
public final class DashboardScope implements IDataController<Dashboard>, IDashboardService, IDashboardItemService, IDashboardElementService {
    private final IDataController<Dashboard> dataController;
    private final IDashboardService dashboardService;
    private final IDashboardItemService dashboardItemService;
    private final IDashboardElementService dashboardElementService;

    public DashboardScope(IDataController<Dashboard> dataController,
                          IDashboardService dashboardService,
                          IDashboardItemService dashboardItemService,
                          IDashboardElementService dashboardElementService) {
        this.dataController = dataController;
        this.dashboardService = dashboardService;
        this.dashboardItemService = dashboardItemService;
        this.dashboardElementService = dashboardElementService;
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
    public void deleteDashboard(Dashboard dashboard) {
        dashboardService.deleteDashboard(dashboard);
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

    @Override
    public DashboardItem createDashboardItem(Dashboard dashboard, DashboardItemContent content) {
        return dashboardItemService.createDashboardItem(dashboard, content);
    }

    @Override
    public void deleteDashboardItem(DashboardItem dashboardItem) {
        dashboardItemService.deleteDashboardItem(dashboardItem);
    }

    @Override
    public int getContentCount(DashboardItem dashboardItem) {
        return dashboardItemService.getContentCount(dashboardItem);
    }

    @Override
    public DashboardElement createDashboardElement(DashboardItem item, DashboardItemContent content) {
        return dashboardElementService.createDashboardElement(item, content);
    }

    @Override
    public void deleteDashboardElement(DashboardItem dashboardItem, DashboardElement dashboardElement) {
        dashboardElementService.deleteDashboardElement(dashboardItem, dashboardElement);
    }
}
