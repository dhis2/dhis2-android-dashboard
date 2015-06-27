/*
 * Copyright (c) 2015, University of Oslo
 * All rights reserved.
 *
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

package org.dhis2.android.dashboard.api.controllers;

import android.net.Uri;
import android.util.Log;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.dhis2.android.dashboard.api.DhisManager;
import org.dhis2.android.dashboard.api.models.Dashboard;
import org.dhis2.android.dashboard.api.models.Dashboard$Table;
import org.dhis2.android.dashboard.api.models.DashboardElement;
import org.dhis2.android.dashboard.api.models.DashboardElement$Table;
import org.dhis2.android.dashboard.api.models.DashboardItem;
import org.dhis2.android.dashboard.api.models.DashboardItem$Table;
import org.dhis2.android.dashboard.api.models.meta.DbOperation;
import org.dhis2.android.dashboard.api.models.meta.State;
import org.dhis2.android.dashboard.api.network.APIException;
import org.dhis2.android.dashboard.api.network.DhisApi;
import org.dhis2.android.dashboard.api.network.RepoManager;
import org.dhis2.android.dashboard.api.persistence.preferences.DateTimeManager;
import org.dhis2.android.dashboard.api.persistence.preferences.DateTimeManager.ResourceType;
import org.dhis2.android.dashboard.api.utils.DbUtils;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import retrofit.RetrofitError;
import retrofit.client.Header;
import retrofit.client.Response;

import static org.dhis2.android.dashboard.api.utils.CollectionUtils.toListIds;
import static org.dhis2.android.dashboard.api.utils.CollectionUtils.toMap;
import static org.dhis2.android.dashboard.api.utils.NetworkUtils.unwrapResponse;

public final class DashboardController implements IController<Object> {
    private final DhisApi mDhisApi;

    public DashboardController(DhisManager dhisManager) {
        mDhisApi = RepoManager.createService(dhisManager.getServerUrl(),
                dhisManager.getUserCredentials());
    }

    @Override
    public Object run() throws APIException {
        /* first we need to fetch all changes in server
        and apply them to local database */
        getDashboardDataFromServer();

        /* now we can try to send changes made locally to server */
        sendLocalChanges();

        return new Object();
    }

    private void sendLocalChanges() throws RetrofitError {
        sendDashboardChanges();
        sendDashboardItemChanges();
        sendDashboardElements();
    }

    private void sendDashboardChanges() throws RetrofitError {
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

    private void postDashboard(Dashboard dashboard) throws RetrofitError {
        Response response = mDhisApi.postDashboard(dashboard);

        // we need to make sure that server has successfully created new dashboard
        if (isSuccess(response.getStatus())) {
            // also, we will need to find UUID of newly created dashboard,
            // which is contained inside of HTTP Location header
            Header header = findLocationHeader(response.getHeaders());
            // parse the value of header as URI and extract the id
            String dashboardId = Uri.parse(header.getValue()).getLastPathSegment();
            // set UUID, change state and save dashboard
            dashboard.setUId(dashboardId);
            dashboard.setState(State.SYNCED);
            dashboard.save();
        }
    }

    private void putDashboard(Dashboard dashboard) throws RetrofitError {
        Response response = mDhisApi.putDashboard(dashboard);

        if (isSuccess(response.getStatus())) {
            dashboard.setState(State.SYNCED);
            dashboard.save();
        }
    }

    private void deleteDashboard(Dashboard dashboard) throws RetrofitError {
        Response response = mDhisApi.deleteDashboard(dashboard.getUId());

        // if the network operation is performed successfully,
        // we can remove local copy of dashboard
        if (isSuccess(response.getStatus())) {
            dashboard.delete();
        }
    }

    private void sendDashboardItemChanges() throws RetrofitError {
        List<DashboardItem> dashboardItems = new Select()
                .from(DashboardItem.class)
                .where(Condition.column(DashboardItem$Table.STATE).isNot(State.SYNCED))
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

    private void postDashboardItem(DashboardItem dashboardItem) throws RetrofitError {
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

            DashboardElement element = elements.get(0);
            Response response = mDhisApi.postDashboardItem(dashboard.getUId(),
                    dashboardItem.getType(), element.getUId());

            if (isSuccess(response.getStatus())) {
                Header locationHeader = findLocationHeader(response.getHeaders());
                String dashboardItemUId = Uri.parse(locationHeader
                        .getValue()).getLastPathSegment();
                dashboardItem.setUId(dashboardItemUId);
                dashboardItem.setState(State.SYNCED);
                dashboardItem.save();

                element.setState(State.SYNCED);
                element.save();
            }
        }
    }

    private void deleteDashboardItem(DashboardItem dashboardItem) throws RetrofitError {
        Dashboard dashboard = dashboardItem.getDashboard();

        if (dashboard != null && dashboard.getState() != null) {
            boolean isDashboardSynced = (dashboard.getState().equals(State.SYNCED) ||
                    dashboard.getState().equals(State.TO_UPDATE));

            if (!isDashboardSynced) {
                return;
            }

            Response response = mDhisApi.deleteDashboardItem(dashboard.getUId(),
                    dashboardItem.getUId());
            if (isSuccess(response.getStatus())) {
                dashboardItem.delete();
            }
        }
    }

    private void sendDashboardElements() throws RetrofitError {
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

    private void postDashboardElement(DashboardElement element) throws RetrofitError {
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
            Response response = mDhisApi.postDashboardItem(
                    dashboard.getUId(), item.getType(), element.getUId());

            if (isSuccess(response.getStatus())) {
                element.setState(State.SYNCED);
                element.save();
            }
        }
    }

    private void deleteDashboardElement(DashboardElement element) throws RetrofitError {
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
            Response response = mDhisApi.deleteDashboardItemContent(
                    dashboard.getUId(), item.getUId(), element.getUId());

            if (isSuccess(response.getStatus())) {
                element.delete();
            }
        }
    }

    private static boolean isSuccess(int status) {
        return status >= 200 && status < 300;
    }

    private static Header findLocationHeader(List<Header> headers) {
        final String LOCATION = "location";
        if (headers != null && !headers.isEmpty()) {
            for (Header header : headers) {
                if (header.getName().equalsIgnoreCase(LOCATION)) {
                    return header;
                }
            }
        }

        return null;
    }

    private void getDashboardDataFromServer() throws RetrofitError {
        boolean isUpdating = DateTimeManager.getInstance().getLastUpdated(
                ResourceType.DASHBOARDS) != null;
        DateTime lastUpdated = DateTimeManager.getInstance()
                .getCurrentDateTimeInServerTimeZone();

        List<Dashboard> dashboards =
                updateDashboards(lastUpdated, isUpdating);
        List<DashboardItem> dashboardItems =
                updateDashboardItems(lastUpdated, isUpdating);

        /* build relation ships between dashboards - items */
        buildDashboardToItemRelations(dashboards, dashboardItems);
        /* build relation ships between dashboards items - elements */
        buildItemToElementRelations(dashboardItems);

        Queue<DbOperation> operations = new LinkedList<>();
        operations.addAll(DbUtils.createOperations(new Select()
                .from(Dashboard.class).queryList(), dashboards));
        operations.addAll(DbUtils.createOperations(new Select()
                .from(DashboardItem.class).queryList(), dashboardItems));
        operations.addAll(createDashboardElementOperations(dashboardItems));

        DbUtils.applyBatch(operations);
        DateTimeManager.getInstance()
                .setLastUpdated(ResourceType.DASHBOARDS, lastUpdated);
    }

    private List<Dashboard> updateDashboards(DateTime lastUpdated,
                                             boolean isUpdating) throws RetrofitError {
        final Map<String, String> QUERY_MAP_BASIC = new HashMap<>();
        final Map<String, String> QUERY_MAP_FULL = new HashMap<>();

        QUERY_MAP_BASIC.put("fields", "id");
        QUERY_MAP_FULL.put("fields", "id,created,lastUpdated,name," +
                "displayName,access,dashboardItems[id]");

        if (isUpdating) {
            QUERY_MAP_FULL.put("filter", "lastUpdated:gt:" + lastUpdated.toString());
        }

        return new AbsBaseController<Dashboard>() {

            @Override
            public List<Dashboard> getExistingItems() {
                return unwrapResponse(mDhisApi.getDashboards(QUERY_MAP_BASIC), "dashboards");
            }

            @Override
            public List<Dashboard> getUpdatedItems() {
                return unwrapResponse(mDhisApi
                        .getDashboards(QUERY_MAP_FULL), "dashboards");
            }

            @Override
            public List<Dashboard> getPersistedItems() {
                List<Dashboard> dashboards = new Select()
                        .from(Dashboard.class).queryList();
                if (dashboards != null && !dashboards.isEmpty()) {
                    for (Dashboard dashboard : dashboards) {
                        dashboard.setDashboardItems(dashboard
                                .queryRelatedDashboardItems());
                    }
                }
                return dashboards;
            }
        }.run();
    }

    private List<DashboardItem> updateDashboardItems(DateTime lastUpdated, boolean isUpdating) throws RetrofitError {
        final Map<String, String> QUERY_MAP_BASIC = new HashMap<>();
        final Map<String, String> QUERY_MAP_FULL = new HashMap<>();

        QUERY_MAP_BASIC.put("fields", "id");
        QUERY_MAP_FULL.put("fields", "id,created,lastUpdated,access," +
                "type,shape,messages," +
                "chart[id,created,lastUpdated,name,displayName]," +
                "eventChart[id,created,lastUpdated,name,displayName]," +
                "map[id,created,lastUpdated,name,displayName]," +
                "reportTable[id,created,lastUpdated,name,displayName]," +
                "eventReport[id,created,lastUpdated,name,displayName]," +
                "users[id,created,lastUpdated,name,displayName]," +
                "reports[id,created,lastUpdated,name,displayName]," +
                "resources[id,created,lastUpdated,name,displayName]," +
                "reportTables[id,created,lastUpdated,name,displayName]");

        if (isUpdating) {
            QUERY_MAP_FULL.put("filter", "lastUpdated:gt:" + lastUpdated.toString());
        }

        return new AbsBaseController<DashboardItem>() {

            @Override
            public List<DashboardItem> getExistingItems() {
                return unwrapResponse(mDhisApi.getDashboardItems(
                        QUERY_MAP_BASIC), "dashboardItems");
            }

            @Override
            public List<DashboardItem> getUpdatedItems() {
                return unwrapResponse(mDhisApi
                        .getDashboardItems(QUERY_MAP_FULL), "dashboardItems");
            }

            @Override
            public List<DashboardItem> getPersistedItems() {
                List<DashboardItem> dashboardItems = new Select()
                        .from(DashboardItem.class).queryList();
                if (dashboardItems != null && !dashboardItems.isEmpty()) {
                    for (DashboardItem dashboardItem : dashboardItems) {
                        List<DashboardElement> elements
                                = dashboardItem.queryRelatedDashboardElements();
                        dashboardItem.setDashboardElements(elements);
                    }
                }
                return dashboardItems;
            }
        }.run();
    }

    private void buildDashboardToItemRelations(List<Dashboard> dashboards, List<DashboardItem> items) {
        Map<String, DashboardItem> itemsMap = toMap(items);

        if (dashboards != null && !dashboards.isEmpty()) {
            for (Dashboard dashboard : dashboards) {
                if (dashboard.getDashboardItems() == null ||
                        dashboard.getDashboardItems().isEmpty()) {
                    continue;
                }

                for (DashboardItem item : dashboard.getDashboardItems()) {
                    DashboardItem dashboardItem = itemsMap.get(item.getUId());
                    if (dashboardItem != null) {
                        dashboardItem.setDashboard(dashboard);
                    }
                }
            }
        }
    }

    private void buildItemToElementRelations(List<DashboardItem> items) {
        if (items == null || items.isEmpty()) {
            return;
        }

        for (DashboardItem item : items) {
            List<DashboardElement> elements =
                    item.getDashboardElements();
            if (elements == null || elements.isEmpty()) {
                continue;
            }

            for (DashboardElement element : elements) {
                element.setDashboardItem(item);
            }
        }
    }

    private List<DbOperation> createDashboardElementOperations(List<DashboardItem> refreshedItems) {
        List<DbOperation> dbOperations = new ArrayList<>();
        for (DashboardItem refreshedItem : refreshedItems) {
            List<DashboardElement> persistedElementList =
                    refreshedItem.queryRelatedDashboardElements();
            List<DashboardElement> refreshedElementList =
                    refreshedItem.getDashboardElements();

            List<String> persistedElementIds = toListIds(persistedElementList);
            List<String> refreshedElementIds = toListIds(refreshedElementList);

            System.out.println("REFRESHED_ITEMS IDS: " + refreshedElementIds);
            System.out.println("PERSISTED_ITEMS IDS: " + persistedElementIds);

            List<String> itemIdsToInsert = subtract(refreshedElementIds, persistedElementIds);
            List<String> itemIdsToDelete = subtract(persistedElementIds, refreshedElementIds);

            System.out.println("ITEMS_TO_INSERT IDS: " + itemIdsToInsert);
            System.out.println("ITEMS_TO_DELETE IDS: " + itemIdsToDelete);

            for (String elementToDelete : itemIdsToDelete) {
                int index = persistedElementIds.indexOf(elementToDelete);
                DashboardElement element = persistedElementList.get(index);
                dbOperations.add(DbOperation.delete(element));
            }

            for (String elementToInsert : itemIdsToInsert) {
                int index = refreshedElementIds.indexOf(elementToInsert);
                DashboardElement dashboardElement = refreshedElementList.get(index);
                dbOperations.add(DbOperation.insert(dashboardElement));
            }

            /* for (int i = 0; i < persistedElementIds.size(); i++) {
                String persistedElementId = persistedElementIds.get(i);
                DashboardElement persistedElement = persistedElementList.get(i);

                // if there is no id of dashboard element in refreshed items,
                // it means it was removed on the server
                if (!refreshedElementIds.contains(persistedElementId)) {
                    // if element was not uploaded to server yet, it will be
                    // automatically removed from persisted items. We don't want that to happen.
                    if (!State.TO_POST.equals(persistedElement.getState())) {
                        dbOperations.add(DbOperation.delete(persistedElement));
                    }
                    continue;
                }

                int indexOfElement = refreshedElementIds.indexOf(persistedElementId);

                refreshedElementIds.remove(indexOfElement);
                refreshedElementList.remove(indexOfElement);
            } */

            /* all remained objects in refreshedElementList are elements to be inserted */
            /* for (DashboardElement dashboardElement : refreshedElementList) {
                dbOperations.add(DbOperation.insert(dashboardElement));
            } */
        }

        Log.e("ELEMENTS", dbOperations.size() + "");
        return dbOperations;
    }

    /* this method subtracts bList from another aList */
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
}