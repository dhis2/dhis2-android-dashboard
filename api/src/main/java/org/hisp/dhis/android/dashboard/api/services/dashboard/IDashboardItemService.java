package org.hisp.dhis.android.dashboard.api.services.dashboard;

import org.hisp.dhis.android.dashboard.api.models.entities.dashboard.Dashboard;
import org.hisp.dhis.android.dashboard.api.models.entities.dashboard.DashboardItem;
import org.hisp.dhis.android.dashboard.api.models.entities.dashboard.DashboardItemContent;
import org.hisp.dhis.android.dashboard.api.services.common.IService;

/**
 * Created by arazabishov on 8/27/15.
 */
public interface IDashboardItemService extends IService {
    DashboardItem createDashboardItem(Dashboard dashboard, DashboardItemContent content);

    void deleteDashboardItem(DashboardItem dashboardItem);
}
