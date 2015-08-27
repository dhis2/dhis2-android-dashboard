package org.hisp.dhis.android.dashboard.api.services.dashboard;

import org.hisp.dhis.android.dashboard.api.models.dashboard.Dashboard;
import org.hisp.dhis.android.dashboard.api.models.dashboard.DashboardItem;
import org.hisp.dhis.android.dashboard.api.models.dashboard.DashboardItemContent;
import org.hisp.dhis.android.dashboard.api.services.common.IService;

/**
 * Created by arazabishov on 8/27/15.
 */
public interface IDashboardItemService extends IService {
    DashboardItem createDashboardItem(Dashboard dashboard, DashboardItemContent content);

    void deleteDashboardItem(DashboardItem dashboardItem);

    int getContentCount(DashboardItem dashboardItem);
}
