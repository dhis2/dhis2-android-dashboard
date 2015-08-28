package org.hisp.dhis.android.dashboard.api.models.dashboard;

import org.hisp.dhis.android.dashboard.api.models.dashboard.DashboardElement;
import org.hisp.dhis.android.dashboard.api.models.dashboard.DashboardItem;
import org.hisp.dhis.android.dashboard.api.models.dashboard.DashboardItemContent;
import org.hisp.dhis.android.dashboard.api.models.common.IService;

/**
 * Created by arazabishov on 8/27/15.
 */
public interface IDashboardElementService extends IService {
    DashboardElement createDashboardElement(DashboardItem item, DashboardItemContent content);

    void deleteDashboardElement(DashboardItem dashboardItem, DashboardElement dashboardElement);
}
