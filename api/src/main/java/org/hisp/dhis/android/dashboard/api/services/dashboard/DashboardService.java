package org.hisp.dhis.android.dashboard.api.services.dashboard;

import android.support.annotation.Nullable;

import org.hisp.dhis.android.dashboard.api.models.entities.Models;
import org.hisp.dhis.android.dashboard.api.models.entities.common.Access;
import org.hisp.dhis.android.dashboard.api.models.entities.common.meta.State;
import org.hisp.dhis.android.dashboard.api.models.entities.dashboard.Dashboard;
import org.hisp.dhis.android.dashboard.api.models.entities.dashboard.DashboardElement;
import org.hisp.dhis.android.dashboard.api.models.entities.dashboard.DashboardItem;
import org.hisp.dhis.android.dashboard.api.models.entities.dashboard.DashboardItemContent;
import org.hisp.dhis.android.dashboard.api.persistence.preferences.DateTimeManager;
import org.hisp.dhis.android.dashboard.api.persistence.preferences.ResourceType;
import org.joda.time.DateTime;

import java.util.List;

import static org.hisp.dhis.android.dashboard.api.utils.Preconditions.isNull;

/**
 * Created by arazabishov on 8/27/15.
 */
public final class DashboardService implements IDashboardService {
    private final IDashboardItemService mDashboardItemService;
    private final IDashboardElementService mDashboardElementService;

    public DashboardService(IDashboardItemService dashboardItemService,
                            IDashboardElementService dashboardElementService) {
        mDashboardItemService = isNull(dashboardItemService, "IDashboardItemService must not be null");
        mDashboardElementService = isNull(dashboardElementService, "IDashboardElementService must not be null");
    }

    /**
     * Creates and returns new Dashboard with given name.
     *
     * @param name Name of new dashboard.
     * @return a dashboard.
     */
    @Override
    public Dashboard createDashboard(String name) {
        DateTime lastUpdatedDateTime = DateTimeManager.getInstance()
                .getLastUpdated(ResourceType.DASHBOARDS);

        Dashboard dashboard = new Dashboard();
        dashboard.setState(State.TO_POST);
        dashboard.setName(name);
        dashboard.setDisplayName(name);
        dashboard.setCreated(lastUpdatedDateTime);
        dashboard.setLastUpdated(lastUpdatedDateTime);
        dashboard.setAccess(Access.provideDefaultAccess());

        return dashboard;
    }

    /**
     * This method will change the name of dashboard along with the State.
     * <p>
     * If the current state of model is State.TO_DELETE or State.TO_POST,
     * state won't be changed. Otherwise, it will be set to State.TO_UPDATE.
     *
     * @param name New name for dashboard.
     */
    @Override
    public void updateDashboardName(Dashboard dashboard, String name) {
        dashboard.setName(name);
        dashboard.setDisplayName(name);

        if (dashboard.getState() != State.TO_DELETE &&
                dashboard.getState() != State.TO_POST) {
            dashboard.setState(State.TO_UPDATE);
        }

        Models.dashboards().update(dashboard);
    }

    /**
     * Will try to append DashboardItemContent to current dashboard.
     * If the type of DashboardItemContent is embedded (chart, eventChart, map, eventReport, reportTable),
     * method will create a new item and append it to dashboard.
     * <p>
     * If the type of DashboardItemContent is link type (users, reports, resources),
     * method will try to append content to existing item. Otherwise it will create a new dashboard item.
     * <p>
     * If the overall count of items in dashboard is bigger that Dashboard.MAX_ITEMS, method will not
     * add content and return false;
     *
     * @param content
     * @return false if item count is bigger than MAX_ITEMS.
     */
    @Override
    public boolean addDashboardContent(Dashboard dashboard, DashboardItemContent content) {
        isNull(content, "DashboardItemContent object must not be null");

        DashboardItem item;
        DashboardElement element;
        int itemsCount = getDashboardItemCount(dashboard);

        if (isItemContentTypeEmbedded(content)) {
            item = mDashboardItemService.createDashboardItem(dashboard, content);
            element = mDashboardElementService.createDashboardElement(item, content);
            itemsCount += 1;
        } else {
            item = getAvailableItemByType(dashboard, content.getType());
            if (item == null) {
                item = mDashboardItemService.createDashboardItem(dashboard, content);
                itemsCount += 1;
            }
            element = mDashboardElementService.createDashboardElement(item, content);
        }

        if (itemsCount > Dashboard.MAX_ITEMS) {
            return false;
        }

        item.save();
        element.save();

        return true;
    }


    /**
     * Returns an item from this dashboard of the given type which number of
     * content is less than max. Returns null if no item matches the criteria.
     *
     * @param type the type of content to return.
     * @return an item.
     */
    @Override
    @Nullable
    public DashboardItem getAvailableItemByType(Dashboard dashboard, String type) {
        List<DashboardItem> items = Models.dashboardItems()
                .filter(dashboard, State.TO_DELETE);

        if (items == null || items.isEmpty()) {
            return null;
        }

        for (DashboardItem item : items) {
            if (type.equals(item.getType()) &&
                    mDashboardItemService.getContentCount(item) < DashboardItem.MAX_CONTENT) {
                return item;
            }
        }

        return null;
    }

    private static int getDashboardItemCount(Dashboard dashboard) {
        List<DashboardItem> items
                = Models.dashboardItems().filter(dashboard, State.TO_DELETE);
        return items == null ? 0 : items.size();
    }

    private static boolean isItemContentTypeEmbedded(DashboardItemContent content) {
        switch (content.getType()) {
            case DashboardItemContent.TYPE_CHART:
            case DashboardItemContent.TYPE_EVENT_CHART:
            case DashboardItemContent.TYPE_MAP:
            case DashboardItemContent.TYPE_EVENT_REPORT:
            case DashboardItemContent.TYPE_REPORT_TABLE: {
                return true;
            }
            case DashboardItemContent.TYPE_USERS:
            case DashboardItemContent.TYPE_REPORTS:
            case DashboardItemContent.TYPE_RESOURCES: {
                return false;
            }
        }

        throw new IllegalArgumentException("Unsupported DashboardItemContent type");
    }
}
