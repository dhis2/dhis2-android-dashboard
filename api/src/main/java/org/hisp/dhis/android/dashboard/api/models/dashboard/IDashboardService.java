package org.hisp.dhis.android.dashboard.api.models.dashboard;

import org.hisp.dhis.android.dashboard.api.models.dashboard.Dashboard;
import org.hisp.dhis.android.dashboard.api.models.dashboard.DashboardItem;
import org.hisp.dhis.android.dashboard.api.models.dashboard.DashboardItemContent;
import org.hisp.dhis.android.dashboard.api.models.common.IService;

/**
 * Created by arazabishov on 8/27/15.
 */
public interface IDashboardService extends IService {
    Dashboard createDashboard(String name);

    void updateDashboardName(Dashboard dashboard, String name);

    boolean addDashboardContent(Dashboard dashboard, DashboardItemContent content);

    DashboardItem getAvailableItemByType(Dashboard dashboard, String type);
}
