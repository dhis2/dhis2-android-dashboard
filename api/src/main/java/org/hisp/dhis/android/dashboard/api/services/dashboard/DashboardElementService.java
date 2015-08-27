package org.hisp.dhis.android.dashboard.api.services.dashboard;

import org.hisp.dhis.android.dashboard.api.models.entities.Models;
import org.hisp.dhis.android.dashboard.api.models.entities.common.meta.State;
import org.hisp.dhis.android.dashboard.api.models.entities.dashboard.DashboardElement;
import org.hisp.dhis.android.dashboard.api.models.entities.dashboard.DashboardItem;
import org.hisp.dhis.android.dashboard.api.models.entities.dashboard.DashboardItemContent;

import static org.hisp.dhis.android.dashboard.api.utils.Preconditions.isNull;

/**
 * Created by arazabishov on 8/27/15.
 */
public final class DashboardElementService implements IDashboardElementService {

    private final IDashboardItemService mDashboardItemService;

    public DashboardElementService(IDashboardItemService dashboardItemService) {
        mDashboardItemService = isNull(dashboardItemService, "IDashboardItemService must not be null");
    }

    /**
     * Factory method for creating DashboardElement.
     *
     * @param item    DashboardItem to associate with element.
     * @param content Content from which element will be created.
     * @return new element.
     */
    @Override
    public DashboardElement createDashboardElement(DashboardItem item, DashboardItemContent content) {
        DashboardElement element = new DashboardElement();
        element.setUId(content.getUId());
        element.setName(content.getName());
        element.setCreated(content.getCreated());
        element.setLastUpdated(content.getLastUpdated());
        element.setDisplayName(content.getDisplayName());
        element.setState(State.TO_POST);
        element.setDashboardItem(item);

        return element;
    }

    @Override
    public void deleteDashboardElement(DashboardItem dashboardItem, DashboardElement dashboardElement) {
        if (State.TO_POST.equals(dashboardElement.getState())) {
            Models.dashboardElements().delete(dashboardElement);
        } else {
            dashboardElement.setState(State.TO_DELETE);
            Models.dashboardElements().update(dashboardElement);
        }

        /* if count of elements in item is zero, it means
        we don't need this item anymore */
        if (!(mDashboardItemService.getContentCount(dashboardItem) > 0)) {
            mDashboardItemService.deleteDashboardItem(dashboardItem);
        }
    }
}
