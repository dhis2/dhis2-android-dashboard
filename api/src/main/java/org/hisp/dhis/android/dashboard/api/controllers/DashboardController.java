/*
 * Copyright (c) 2015, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.dashboard.api.controllers;

import static org.hisp.dhis.android.dashboard.api.models.BaseIdentifiableObject.merge;
import static org.hisp.dhis.android.dashboard.api.models.BaseIdentifiableObject.toListIds;
import static org.hisp.dhis.android.dashboard.api.models.BaseIdentifiableObject.toMap;
import static org.hisp.dhis.android.dashboard.api.utils.NetworkUtils.findLocationHeader;
import static org.hisp.dhis.android.dashboard.api.utils.NetworkUtils.handleApiException;
import static org.hisp.dhis.android.dashboard.api.utils.NetworkUtils.unwrapResponse;

import android.net.Uri;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.sql.language.Where;

import org.hisp.dhis.android.dashboard.api.models.Dashboard;
import org.hisp.dhis.android.dashboard.api.models.Dashboard$Table;
import org.hisp.dhis.android.dashboard.api.models.DashboardElement;
import org.hisp.dhis.android.dashboard.api.models.DashboardElement$Table;
import org.hisp.dhis.android.dashboard.api.models.DashboardItem;
import org.hisp.dhis.android.dashboard.api.models.DashboardItem$Table;
import org.hisp.dhis.android.dashboard.api.models.DashboardItemContent;
import org.hisp.dhis.android.dashboard.api.models.DashboardItemContent$Table;
import org.hisp.dhis.android.dashboard.api.models.SystemInfo;
import org.hisp.dhis.android.dashboard.api.models.meta.DbOperation;
import org.hisp.dhis.android.dashboard.api.models.meta.State;
import org.hisp.dhis.android.dashboard.api.network.APIException;
import org.hisp.dhis.android.dashboard.api.network.DhisApi;
import org.hisp.dhis.android.dashboard.api.persistence.preferences.DateTimeManager;
import org.hisp.dhis.android.dashboard.api.persistence.preferences.ResourceType;
import org.hisp.dhis.android.dashboard.api.utils.DbUtils;
import org.hisp.dhis.android.dashboard.api.utils.SyncStrategy;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import retrofit.client.Header;
import retrofit.client.Response;

final class DashboardController {
    final DhisApi mDhisApi;

    public DashboardController(DhisApi dhisApi) {
        mDhisApi = dhisApi;
    }

    /* this method subtracts content of bList from aList */
    private static List<String> subtract(List<String> aList, List<String> bList) {
        List<String> aListCopy = new ArrayList<>(aList);
        if (bList != null && !bList.isEmpty()) {
            for (String bItem : bList) {
                if (aListCopy.contains(bItem)) {
                    int index = aListCopy.indexOf(bItem);
                    aListCopy.remove(index);
                }
            }
        }
        return aListCopy;
    }

    private static List<Dashboard> queryDashboards() {
        return new Select().from(Dashboard.class)
                .where(Condition.column(Dashboard$Table
                        .STATE).isNot(State.TO_POST.toString()))
                .queryList();
    }

    public static List<DashboardElement> queryAllDashboardElement() {
        return new Select().from(DashboardElement.class)
                .queryList();
    }

    private static List<DashboardItem> queryDashboardItems(Dashboard dashboard) {
        Where<DashboardItem> where = new Select().from(DashboardItem.class)
                .where(Condition.column(DashboardItem$Table
                        .STATE).isNot(State.TO_POST));
        if (dashboard != null) {
            where = where.and(Condition.column(DashboardItem$Table
                    .DASHBOARD_DASHBOARD).is(dashboard.getId()));
        }

        return where.queryList();
    }

    private static List<DashboardElement> queryDashboardElements(DashboardItem item) {
        return new Select().from(DashboardElement.class)
                .where(Condition.column(DashboardElement$Table
                        .DASHBOARDITEM_DASHBOARDITEM).is(item.getId()))
                .and(Condition.column(DashboardElement$Table
                        .STATE).isNot(State.TO_POST.toString()))
                .queryList();
    }

    public void syncDashboards(SyncStrategy syncStrategy) throws APIException {
        /* first we need to fetch all changes from server
        and apply them to local database */
        getDashboardDataFromServer(syncStrategy);

        /* now we can try to send changes made locally to server */
        sendLocalChanges();
    }

    private void getDashboardDataFromServer(SyncStrategy syncStrategy) throws APIException {
        DateTime lastUpdated = DateTimeManager.getInstance()
                .getLastUpdated(ResourceType.DASHBOARDS);
        SystemInfo systemInfo = mDhisApi.getSystemInfo();
        systemInfo.save();
        DateTime serverDateTime = systemInfo
                .getServerDate();

        List<Dashboard> dashboards;
        if (syncStrategy == SyncStrategy.DOWNLOAD_ONLY_NEW) {
            dashboards = updateDashboards(lastUpdated);
        } else {
            dashboards = updateDashboards(null);
        }
        List<DashboardItem> dashboardItems = updateDashboardItems(dashboards, lastUpdated);

        Queue<DbOperation> operations = new LinkedList<>();
        operations.addAll(DbUtils.createOperations(queryDashboards(), dashboards));
        operations.addAll(DbUtils.createOperations(queryDashboardItems(null), dashboardItems));
        operations.addAll(createOperations(dashboardItems));

        DbUtils.applyBatch(operations);
        DateTimeManager.getInstance()
                .setLastUpdated(ResourceType.DASHBOARDS, serverDateTime);
    }

    private List<Dashboard> updateDashboards(DateTime lastUpdated) throws APIException {
        final Map<String, String> QUERY_MAP_BASIC = new HashMap<>();
        final Map<String, String> QUERY_MAP_FULL = new HashMap<>();
        final String BASE = "id,created,lastUpdated,name,displayName,access,publicAccess";

        QUERY_MAP_BASIC.put("fields", "id");
        QUERY_MAP_FULL.put("fields", BASE + ",dashboardItems" +
                "[" + BASE + ",type,shape,messages," +
                "x,y,w,h,originalHeight,width,height,favorite," +
                "chart" + "[" + BASE + "]," +
                "eventChart" + "[" + BASE + "]" +
                "map" + "[" + BASE + "]," +
                "reportTable" + "[" + BASE + "]," +
                "eventReport" + "[" + BASE + "]," +
                "users" + "[" + BASE + "]," +
                "reports" + "[" + BASE + "]," +
                "resources" + "[" + BASE + "]" +
                "]");

        if (lastUpdated != null) {
            QUERY_MAP_FULL.put("filter",
                    "lastUpdated:gt:" + lastUpdated.toLocalDateTime().toString());
        }

        // List of dashboards with UUIDs (without content). This list is used
        // only to determine what was removed on server.
        List<Dashboard> actualDashboards = unwrapResponse(mDhisApi
                .getDashboards(QUERY_MAP_BASIC), "dashboards");

        // List of updated dashboards with content.
        List<Dashboard> updatedDashboards = unwrapResponse(mDhisApi
                .getDashboards(QUERY_MAP_FULL), "dashboards");

        // Building dashboard item to dashboard relationship.
        if (updatedDashboards != null && !updatedDashboards.isEmpty()) {
            for (Dashboard dashboard : updatedDashboards) {
                if (dashboard == null || dashboard.getDashboardItems().isEmpty()) {
                    continue;
                }

                int i=0;
                for (DashboardItem item : dashboard.getDashboardItems()) {
                    item.setDashboard(dashboard);
                    item.setOrderPosition(i);
                    i++;
                }
            }
        }

        // List of persisted dashboards.
        List<Dashboard> persistedDashboards = queryDashboards();
        if (persistedDashboards != null && !persistedDashboards.isEmpty()) {
            for (Dashboard dashboard : persistedDashboards) {
                List<DashboardItem> items = queryDashboardItems(dashboard);
                if (items == null || items.isEmpty()) {
                    continue;
                }

                for (DashboardItem item : items) {
                    item.setDashboardElements(queryDashboardElements(item));
                }
                dashboard.setDashboardItems(items);
            }
        }

        return merge(actualDashboards, updatedDashboards, persistedDashboards);
    }

    private List<DashboardItem> updateDashboardItems(List<Dashboard> dashboards, DateTime lastUpdated) throws APIException {
        final Map<String, String> QUERY_MAP_BASIC = new HashMap<>();
        QUERY_MAP_BASIC.put("fields", "id,created,lastUpdated,shape" +
                "x,y,w,h,originalHeight,width,height,favorite,");

        if (lastUpdated != null) {
            QUERY_MAP_BASIC.put("filter", "lastUpdated:gt:" + lastUpdated.toLocalDateTime().toString());
        }

        // List of actual dashboard items.
        List<DashboardItem> actualItems = new ArrayList<>();
        if (dashboards != null && !dashboards.isEmpty()) {
            for (Dashboard dashboard : dashboards) {
                if (dashboard.getDashboardItems() != null) {
                    actualItems.addAll(dashboard.getDashboardItems());
                }
            }
        }

        // List of persisted dashboard items
        Map<String, DashboardItem> persistedDashboardItems
                = toMap(queryDashboardItems(null));

        // List of updated dashboard items. We need this only to get
        // information about updates of item shape.
        List<DashboardItem> updatedItems = unwrapResponse(mDhisApi
                .getDashboardItems(QUERY_MAP_BASIC), "dashboardItems");
        // Map of items where keys are UUIDs.
        Map<String, DashboardItem> updatedItemsMap = toMap(updatedItems);

        // merging updated items with actual
        for (DashboardItem actualItem : actualItems) {
            DashboardItem updatedItem = updatedItemsMap.get(actualItem.getUId());
            DashboardItem persistedItem = persistedDashboardItems.get(actualItem.getUId());

            if (persistedItem != null) {
                actualItem.setId(persistedItem.getId());
            }

            if (updatedItem != null) {
                actualItem.setCreated(updatedItem.getCreated());
                actualItem.setLastUpdated(updatedItem.getLastUpdated());
                actualItem.setShape(updatedItem.getShape());
            }

            if (actualItem.getDashboardElements() == null ||
                    actualItem.getDashboardElements().isEmpty()) {
                continue;
            }

            // building dashboard element to item relationship.
            for (DashboardElement element : actualItem.getDashboardElements()) {
                element.setDashboardItem(actualItem);
            }
        }

        return actualItems;
    }

    private List<DbOperation> createOperations(List<DashboardItem> refreshedItems) {
        List<DbOperation> dbOperations = new ArrayList<>();
        for (DashboardItem refreshedItem : refreshedItems) {
            List<DashboardElement> persistedElementList
                    = queryDashboardElements(refreshedItem);
            List<DashboardElement> refreshedElementList =
                    refreshedItem.getDashboardElements();

            if (persistedElementList == null) {
                persistedElementList = new ArrayList<>();
            }

            if (refreshedElementList == null) {
                refreshedElementList = new ArrayList<>();
            }

            List<String> persistedElementIds = toListIds(persistedElementList);
            List<String> refreshedElementIds = toListIds(refreshedElementList);

            List<String> itemIdsToInsert = subtract(refreshedElementIds, persistedElementIds);
            List<String> itemIdsToDelete = subtract(persistedElementIds, refreshedElementIds);

            for (String elementToDelete : itemIdsToDelete) {
                int index = persistedElementIds.indexOf(elementToDelete);
                DashboardElement element = persistedElementList.get(index);
                dbOperations.add(DbOperation.delete(element));

                persistedElementIds.remove(index);
                persistedElementList.remove(index);
            }

            for (String elementToInsert : itemIdsToInsert) {
                int index = refreshedElementIds.indexOf(elementToInsert);
                DashboardElement dashboardElement = refreshedElementList.get(index);
                dbOperations.add(DbOperation.insert(dashboardElement));

                refreshedElementIds.remove(index);
                refreshedElementList.remove(index);
            }
        }

        return dbOperations;
    }

    private void sendLocalChanges() throws APIException {
        sendDashboardChanges();
        sendDashboardItemChanges();
        sendDashboardElements();
    }

    private void sendDashboardChanges() throws APIException {
        // we need to sort dashboards in natural order.
        // In order they were inserted in local database.
        List<Dashboard> dashboards = new Select()
                .from(Dashboard.class)
                .where(Condition.column(Dashboard$Table
                        .STATE).isNot(State.SYNCED.toString()))
                .orderBy(true, Dashboard$Table.ID)
                .queryList();

        if (dashboards == null || dashboards.isEmpty()) {
            return;
        }

        for (Dashboard dashboard : dashboards) {
            switch (dashboard.getState()) {
                case TO_POST: {
                    postDashboard(dashboard);
                    break;
                }
                case TO_UPDATE: {
                    putDashboard(dashboard);
                    break;
                }
                case TO_DELETE: {
                    deleteDashboard(dashboard);
                    break;
                }
            }
        }
    }

    private void postDashboard(Dashboard dashboard) throws APIException {
        try {
            Response response = mDhisApi.postDashboard(dashboard);
            // also, we will need to find UUID of newly created dashboard,
            // which is contained inside of HTTP Location header
            Header header = findLocationHeader(response.getHeaders());
            // parse the value of header as URI and extract the id
            String dashboardId = Uri.parse(header.getValue()).getLastPathSegment();
            // set UUID, change state and save dashboard
            dashboard.setUId(dashboardId);
            dashboard.setState(State.SYNCED);
            dashboard.save();

            updateDashboardTimeStamp(dashboard);
        } catch (APIException apiException) {
            handleApiException(apiException);
        }
    }

    private void putDashboard(Dashboard dashboard) throws APIException {
        try {
            mDhisApi.putDashboard(dashboard.getUId(), dashboard);

            dashboard.setState(State.SYNCED);
            dashboard.save();

            updateDashboardTimeStamp(dashboard);
        } catch (APIException apiException) {
            handleApiException(apiException, dashboard);
        }
    }

    private void deleteDashboard(Dashboard dashboard) throws APIException {
        try {
            mDhisApi.deleteDashboard(dashboard.getUId());
            dashboard.delete();
        } catch (APIException apiException) {
            handleApiException(apiException, dashboard);
        }
    }

    private void sendDashboardItemChanges() throws APIException {
        List<DashboardItem> dashboardItems = new Select()
                .from(DashboardItem.class)
                .where(Condition.column(DashboardItem$Table
                        .STATE).isNot(State.SYNCED))
                .orderBy(true, DashboardItem$Table.ID)
                .queryList();

        if (dashboardItems == null || dashboardItems.isEmpty()) {
            return;
        }

        for (DashboardItem dashboardItem : dashboardItems) {
            switch (dashboardItem.getState()) {
                case TO_POST: {
                    postDashboardItem(dashboardItem);
                    break;
                }
                case TO_DELETE: {
                    deleteDashboardItem(dashboardItem);
                    break;
                }
            }
        }
    }

    private void postDashboardItem(DashboardItem dashboardItem) throws APIException {
        Dashboard dashboard = dashboardItem.getDashboard();

        if (dashboard != null && dashboard.getState() != null) {
            boolean isDashboardSynced = (dashboard.getState().equals(State.SYNCED) ||
                    dashboard.getState().equals(State.TO_UPDATE));

            if (!isDashboardSynced) {
                return;
            }

            List<DashboardElement> elements = new Select().from(DashboardElement.class)
                    .where(Condition.column(DashboardElement$Table
                            .DASHBOARDITEM_DASHBOARDITEM).is(dashboardItem.getId()))
                    .and(Condition.column(DashboardItem$Table
                            .STATE).is(State.TO_POST.toString()))
                    .queryList();

            if (elements == null || elements.isEmpty()) {
                return;
            }

            try {
                DashboardElement element = elements.get(0);
                Response response = mDhisApi.postDashboardItem(dashboard.getUId(),
                        dashboardItem.getType(), element.getUId(), "");

                Header locationHeader = findLocationHeader(response.getHeaders());
                String dashboardItemUId = Uri.parse(locationHeader
                        .getValue()).getLastPathSegment();
                dashboardItem.setUId(dashboardItemUId);
                dashboardItem.setState(State.SYNCED);
                dashboardItem.save();

                element.setState(State.SYNCED);
                element.save();

                // we have to update timestamp of dashboard after adding new item.
                updateDashboardTimeStamp(dashboardItem.getDashboard());
            } catch (APIException apiException) {
                handleApiException(apiException, dashboardItem);
            }
        }
    }

    private void deleteDashboardItem(DashboardItem dashboardItem) throws APIException {
        Dashboard dashboard = dashboardItem.getDashboard();

        if (dashboard != null && dashboard.getState() != null) {
            boolean isDashboardSynced = (dashboard.getState().equals(State.SYNCED) ||
                    dashboard.getState().equals(State.TO_UPDATE));

            if (!isDashboardSynced) {
                return;
            }

            try {
                mDhisApi.deleteDashboardItem(dashboard.getUId(),
                        dashboardItem.getUId());
                dashboardItem.delete();

                // we have to update timestamp of dashboard after adding new item.
                updateDashboardTimeStamp(dashboardItem.getDashboard());
            } catch (APIException apiException) {
                handleApiException(apiException, dashboardItem);
            }
        }
    }

    private void sendDashboardElements() throws APIException {
        List<DashboardElement> elements = new Select()
                .from(DashboardElement.class)
                .where(Condition.column(DashboardElement$Table
                        .STATE).isNot(State.SYNCED))
                .orderBy(true, DashboardElement$Table.ID)
                .queryList();

        if (elements == null || elements.isEmpty()) {
            return;
        }

        for (DashboardElement element : elements) {
            switch (element.getState()) {
                case TO_POST: {
                    postDashboardElement(element);
                    break;
                }
                case TO_DELETE: {
                    deleteDashboardElement(element);
                    break;
                }
            }
        }
    }

    private void postDashboardElement(DashboardElement element) throws APIException {
        DashboardItem item = element.getDashboardItem();
        if (item == null || item.getState() == null) {
            return;
        }

        Dashboard dashboard = item.getDashboard();
        if (dashboard == null || dashboard.getState() == null) {
            return;
        }

        // we need to make sure that associated DashboardItem
        // and parent Dashboard are already synced to the server.
        boolean isDashboardSynced = (dashboard.getState().equals(State.SYNCED) ||
                dashboard.getState().equals(State.TO_UPDATE));
        boolean isItemSynced = item.getState().equals(State.SYNCED) ||
                item.getState().equals(State.TO_UPDATE);
        if (isDashboardSynced && isItemSynced) {

            try {
                mDhisApi.postDashboardItem(dashboard.getUId(),
                        item.getType(), element.getUId(), "");
                element.setState(State.SYNCED);
                element.save();

                updateDashboardTimeStamp(item.getDashboard());
            } catch (APIException apiException) {
                handleApiException(apiException, element);
            }
        }
    }

    private void deleteDashboardElement(DashboardElement element) throws APIException {
        DashboardItem item = element.getDashboardItem();
        if (item == null || item.getState() == null) {
            return;
        }

        Dashboard dashboard = item.getDashboard();
        if (dashboard == null || dashboard.getState() == null) {
            return;
        }

        // we need to make sure associated DashboardItem
        // and parent Dashboard are already synced to server
        boolean isDashboardSynced = (dashboard.getState().equals(State.SYNCED) ||
                dashboard.getState().equals(State.TO_UPDATE));
        boolean isItemSynced = item.getState().equals(State.SYNCED) ||
                item.getState().equals(State.TO_UPDATE);
        if (isDashboardSynced && isItemSynced) {
            try {
                mDhisApi.deleteDashboardItemContent(dashboard.getUId(),
                        item.getUId(), element.getUId());

                element.delete();

                // removal of elements changes
                // dashboard's timestamp on server. In order to stay in sync,
                // we need to get dashboard from server.
                updateDashboardTimeStamp(item.getDashboard());
            } catch (APIException apiException) {
                handleApiException(apiException, element);
            }
        }
    }

    private void updateDashboardTimeStamp(Dashboard dashboard) throws APIException {
        try {
            final Map<String, String> QUERY_PARAMS = new HashMap<>();
            QUERY_PARAMS.put("fields", "created,lastUpdated,publicAccess");
            Dashboard updatedDashboard = mDhisApi
                    .getDashboard(dashboard.getUId(), QUERY_PARAMS);

            // merging updated timestamp to local dashboard model
            dashboard.setCreated(updatedDashboard.getCreated());
            dashboard.setLastUpdated(updatedDashboard.getLastUpdated());
            dashboard.save();
        } catch (APIException apiException) {
            handleApiException(apiException);
        }
    }

    public void syncDashboardContent() throws APIException {
        DateTime lastUpdated = DateTimeManager.getInstance()
                .getLastUpdated(ResourceType.DASHBOARDS_CONTENT);
        DateTime serverDateTime = mDhisApi
                .getSystemInfo().getServerDate();

        /* first we need to update api resources, dashboards
        and dashboard items */
        List<DashboardItemContent> dashboardItemContent =
                updateApiResources(lastUpdated);
        Queue<DbOperation> operations = new LinkedList<>();
        operations.addAll(DbUtils.createOperations(new Select()
                .from(DashboardItemContent.class).queryList(), dashboardItemContent));
        DbUtils.applyBatch(operations);



        DateTimeManager.getInstance()
                .setLastUpdated(ResourceType.DASHBOARDS_CONTENT, serverDateTime);
    }

    private List<DashboardItemContent> updateApiResources(DateTime lastUpdated) throws APIException {
        List<DashboardItemContent> dashboardItemContent = new ArrayList<>();
        dashboardItemContent.addAll(updateApiResourceByType(
                DashboardItemContent.TYPE_CHART, lastUpdated));
        dashboardItemContent.addAll(updateApiResourceByType(
                DashboardItemContent.TYPE_EVENT_CHART, lastUpdated));
        dashboardItemContent.addAll(updateApiResourceByType(
                DashboardItemContent.TYPE_MAP, lastUpdated));
        dashboardItemContent.addAll(updateApiResourceByType(
                DashboardItemContent.TYPE_REPORT_TABLE, lastUpdated));
        dashboardItemContent.addAll(updateApiResourceByType(
                DashboardItemContent.TYPE_EVENT_REPORT, lastUpdated));
        dashboardItemContent.addAll(updateApiResourceByType(
                DashboardItemContent.TYPE_USERS, lastUpdated));
        dashboardItemContent.addAll(updateApiResourceByType(
                DashboardItemContent.TYPE_REPORTS, lastUpdated));
        dashboardItemContent.addAll(updateApiResourceByType(
                DashboardItemContent.TYPE_RESOURCES, lastUpdated));
        dashboardItemContent.addAll(updateApiResourceByType(
                DashboardItemContent.TYPE_MESSAGES, lastUpdated));
        return dashboardItemContent;
    }

    private List<DashboardItemContent> updateApiResourceByType(final String type,
                                                               final DateTime lastUpdated) throws APIException {
        final Map<String, String> QUERY_MAP_BASIC = new HashMap<>();
        final Map<String, String> QUERY_MAP_FULL = new HashMap<>();

        QUERY_MAP_BASIC.put("fields", "id");
        QUERY_MAP_FULL.put("fields", "id,created,lastUpdated,name,displayName");

        if (lastUpdated != null) {
            QUERY_MAP_FULL.put("filter", "lastUpdated:gt:" + lastUpdated.toString());
        }

        List<DashboardItemContent> actualItems
                = getApiResourceByType(type, QUERY_MAP_BASIC);

        List<DashboardItemContent> updatedItems =
                getApiResourceByType(type, QUERY_MAP_FULL);

        if (updatedItems != null && !updatedItems.isEmpty()) {
            for (DashboardItemContent element : updatedItems) {
                element.setType(type);
            }
        }

        List<DashboardItemContent> persistedItems = new Select()
                .from(DashboardItemContent.class)
                .where(Condition.column(DashboardItemContent$Table
                        .TYPE).is(type))
                .queryList();

        return merge(actualItems, updatedItems, persistedItems);
    }

    private List<DashboardItemContent> getApiResourceByType(String type, Map<String, String> queryParams) throws APIException {
        switch (type) {
            case DashboardItemContent.TYPE_CHART:
                return unwrapResponse(mDhisApi.getCharts(queryParams), "charts");
            case DashboardItemContent.TYPE_EVENT_CHART:
                return unwrapResponse(mDhisApi.getEventCharts(queryParams), "eventCharts");
            case DashboardItemContent.TYPE_MAP:
                return unwrapResponse(mDhisApi.getMaps(queryParams), "maps");
            case DashboardItemContent.TYPE_REPORT_TABLE:
                return unwrapResponse(mDhisApi.getReportTables(queryParams), "reportTables");
            case DashboardItemContent.TYPE_EVENT_REPORT:
                return unwrapResponse(mDhisApi.getEventReports(queryParams), "eventReports");
            case DashboardItemContent.TYPE_USERS:
                return unwrapResponse(mDhisApi.getUsers(queryParams), "users");
            case DashboardItemContent.TYPE_REPORTS:
                return unwrapResponse(mDhisApi.getReports(queryParams), "reports");
            case DashboardItemContent.TYPE_RESOURCES:
                return unwrapResponse(mDhisApi.getResources(queryParams), "documents");
            case DashboardItemContent.TYPE_MESSAGES:
                return unwrapResponse(mDhisApi.getResources(queryParams), "documents");
            default:
                throw new IllegalArgumentException("Unsupported DashboardItemContent type");
        }
    }
}