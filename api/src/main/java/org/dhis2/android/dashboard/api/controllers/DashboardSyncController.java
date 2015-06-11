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

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.dhis2.android.dashboard.api.DhisManager;
import org.dhis2.android.dashboard.api.models.Dashboard;
import org.dhis2.android.dashboard.api.models.DashboardElement;
import org.dhis2.android.dashboard.api.models.DashboardElement$Table;
import org.dhis2.android.dashboard.api.models.DashboardItem;
import org.dhis2.android.dashboard.api.models.DashboardItem$Table;
import org.dhis2.android.dashboard.api.models.DbOperation;
import org.dhis2.android.dashboard.api.models.ElementToItemRelation;
import org.dhis2.android.dashboard.api.models.State;
import org.dhis2.android.dashboard.api.network.APIException;
import org.dhis2.android.dashboard.api.network.DhisApi;
import org.dhis2.android.dashboard.api.network.RepoManager;
import org.dhis2.android.dashboard.api.persistence.DbHelper;
import org.dhis2.android.dashboard.api.persistence.preferences.LastUpdatedPreferences;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import retrofit.RetrofitError;

import static android.text.TextUtils.isEmpty;
import static org.dhis2.android.dashboard.api.utils.DbUtils.toMap;
import static org.dhis2.android.dashboard.api.utils.NetworkUtils.unwrapResponse;

public final class DashboardSyncController implements IController<Object> {
    private final DhisManager mDhisManager;
    private final LastUpdatedPreferences mLastUpdatedPreferences;
    private final DhisApi mDhisApi;

    public DashboardSyncController(DhisManager dhisManager, LastUpdatedPreferences preferences) {
        mDhisManager = dhisManager;
        mLastUpdatedPreferences = preferences;
        mDhisApi = RepoManager.createService(dhisManager.getServerUrl(),
                dhisManager.getUserCredentials());
    }

    @Override
    public Object run() throws APIException {
        boolean isUpdating = mLastUpdatedPreferences.getLastUpdated() != null;
        DateTime lastUpdated = DateTime.now(DateTimeZone
                .forTimeZone(mLastUpdatedPreferences.getServerTimeZone()));

        List<Dashboard> dashboards =
                updateDashboards(lastUpdated, isUpdating);
        List<DashboardItem> dashboardItems =
                updateDashboardItems(lastUpdated, isUpdating);
        buildDashboardToItemRelations(dashboards, dashboardItems);
        List<DashboardElement> dashboardElements =
                updateDashboardElements(lastUpdated, isUpdating);
        List<ElementToItemRelation> elementToItemRelations =
                buildElementToItemRelation(dashboardItems);

        Queue<DbOperation> operations = new LinkedList<>();
        operations.addAll(DbHelper.createOperations(new Select()
                .from(Dashboard.class).queryList(), dashboards));
        operations.addAll(DbHelper.createOperations(new Select()
                .from(DashboardItem.class).queryList(), dashboardItems));
        operations.addAll(DbHelper.createOperations(new Select()
                .from(DashboardElement.class).queryList(), dashboardElements));
        operations.addAll(DbHelper.syncRelationModels(new Select()
                .from(ElementToItemRelation.class).queryList(), elementToItemRelations));

        DbHelper.applyBatch(operations);

        mLastUpdatedPreferences.setLastUpdated(lastUpdated);
        return new Object();
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
                    DashboardItem dashboardItem = itemsMap.get(item.getId());
                    if (dashboardItem != null) {
                        dashboardItem.setDashboard(dashboard);
                    }
                }
            }
        }
    }

    private List<ElementToItemRelation> buildElementToItemRelation(List<DashboardItem> dashboardItems) {
        List<ElementToItemRelation> relations = new ArrayList<>();

        if (dashboardItems == null || dashboardItems.isEmpty()) {
            return relations;
        }

        for (DashboardItem item : dashboardItems) {
            switch (item.getType()) {
                case DashboardElement.TYPE_CHART: {
                    relations.add(createElementToItemRelation(item,
                            item.getChart()));
                    break;
                }
                case DashboardElement.TYPE_EVENT_CHART: {
                    relations.add(createElementToItemRelation(item,
                            item.getEventChart()));
                    break;
                }
                case DashboardElement.TYPE_MAP: {
                    relations.add(createElementToItemRelation(item,
                            item.getMap()));
                    break;
                }
                case DashboardElement.TYPE_REPORT_TABLE: {
                    relations.add(createElementToItemRelation(item,
                            item.getReportTable()));
                    break;
                }
                case DashboardElement.TYPE_EVENT_REPORT: {
                    relations.add(createElementToItemRelation(item,
                            item.getEventReport()));
                    break;
                }
                case DashboardElement.TYPE_USERS: {
                    relations.addAll(createElementToItemRelations(item,
                            item.getUsers()));
                    break;
                }
                case DashboardElement.TYPE_REPORTS: {
                    relations.addAll(createElementToItemRelations(item,
                            item.getReports()));
                    break;
                }
                case DashboardElement.TYPE_RESOURCES: {
                    relations.addAll(createElementToItemRelations(item,
                            item.getResources()));
                    break;
                }
                case DashboardElement.TYPE_REPORT_TABLES: {
                    relations.addAll(createElementToItemRelations(item,
                            item.getReportTables()));
                    break;
                }
            }
        }

        return relations;
    }

    private ElementToItemRelation createElementToItemRelation(DashboardItem item, DashboardElement element) {
        ElementToItemRelation relation = new ElementToItemRelation();
        relation.setDashboardItem(item);
        relation.setDashboardElement(element);
        relation.setState(State.SYNCED);
        return relation;
    }

    private List<ElementToItemRelation> createElementToItemRelations(DashboardItem item, List<DashboardElement> elements) {
        List<ElementToItemRelation> relations = new ArrayList<>();

        if (elements == null || elements.isEmpty()) {
            return relations;
        }

        for (DashboardElement element : elements) {
            relations.add(createElementToItemRelation(item, element));
        }

        return relations;
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

            @Override public List<Dashboard> getExistingItems() {
                return unwrapResponse(mDhisApi.getDashboards(QUERY_MAP_BASIC), "dashboards");
            }

            @Override public List<Dashboard> getUpdatedItems() {
                List<Dashboard> dashboards = unwrapResponse(mDhisApi
                        .getDashboards(QUERY_MAP_FULL), "dashboards");
                if (dashboards != null && !dashboards.isEmpty()) {
                    for (Dashboard dashboard : dashboards) {
                        dashboard.setState(State.SYNCED);
                    }
                }
                return dashboards;
            }

            @Override public List<Dashboard> getPersistedItems() {
                List<Dashboard> dashboards = new Select()
                        .from(Dashboard.class).queryList();
                if (dashboards != null && !dashboards.isEmpty()) {
                    for (Dashboard dashboard : dashboards) {
                        List<DashboardItem> dashboardItems = new Select()
                                .from(DashboardItem.class)
                                .where(Condition.column(DashboardItem$Table
                                        .DASHBOARD_DASHBOARD).is(dashboard.getLocalId()))
                                .queryList();
                        dashboard.setDashboardItems(dashboardItems);
                    }
                }
                return dashboards;
            }
        }.run();
    }

    private List<DashboardItem> updateDashboardItems(DateTime lastUpdated,
                                                     boolean isUpdating) throws RetrofitError {
        final Map<String, String> QUERY_MAP_BASIC = new HashMap<>();
        final Map<String, String> QUERY_MAP_FULL = new HashMap<>();

        QUERY_MAP_BASIC.put("fields", "id");
        QUERY_MAP_FULL.put("fields", "id,created,lastUpdated,access," +
                "type,shape,messages," +
                "chart[id],eventChart[id],map[id]," +
                "reportTable[id],eventReport[id],users[id],reports[id]," +
                "resources[id],reportTables[id]");

        if (isUpdating) {
            QUERY_MAP_FULL.put("filter", "lastUpdated:gt:" + lastUpdated.toString());
        }

        List<DashboardItem> items = new AbsBaseController<DashboardItem>() {

            @Override public List<DashboardItem> getExistingItems() {
                return unwrapResponse(mDhisApi.getDashboardItems(
                        QUERY_MAP_BASIC), "dashboardItems");
            }

            @Override public List<DashboardItem> getUpdatedItems() {
                List<DashboardItem> dashboardItems = unwrapResponse(mDhisApi
                        .getDashboardItems(QUERY_MAP_FULL), "dashboardItems");
                if (dashboardItems != null && !dashboardItems.isEmpty()) {
                    for (DashboardItem dashboardItem : dashboardItems) {
                        dashboardItem.setState(State.SYNCED);
                    }
                }
                return dashboardItems;
            }

            @Override public List<DashboardItem> getPersistedItems() {
                List<DashboardItem> dashboardItems = new Select()
                        .from(DashboardItem.class).queryList();
                if (dashboardItems != null && !dashboardItems.isEmpty()) {
                    for (DashboardItem dashboardItem : dashboardItems) {
                        DashboardItem.readElementsIntoItem(dashboardItem);
                    }
                }
                return dashboardItems;
            }
        }.run();

        /* In some cases we can get dashboard item with null type from server */
        List<DashboardItem> filteredItems = new ArrayList<>();
        if (items != null && !items.isEmpty()) {
            for (DashboardItem item : items) {
                if (!isEmpty(item.getType())) {
                    filteredItems.add(item);
                }
            }
        }
        return filteredItems;
    }

    private List<DashboardElement> updateDashboardElements(DateTime lastUpdated,
                                                           boolean isUpdating) throws RetrofitError {
        List<DashboardElement> dashboardElements = new ArrayList<>();
        dashboardElements.addAll(updateDashboardElementsByType(
                DashboardElement.TYPE_CHART, lastUpdated, isUpdating));
        dashboardElements.addAll(updateDashboardElementsByType(
                DashboardElement.TYPE_EVENT_CHART, lastUpdated, isUpdating));
        dashboardElements.addAll(updateDashboardElementsByType(
                DashboardElement.TYPE_MAP, lastUpdated, isUpdating));
        dashboardElements.addAll(updateDashboardElementsByType(
                DashboardElement.TYPE_REPORT_TABLES, lastUpdated, isUpdating));
        dashboardElements.addAll(updateDashboardElementsByType(
                DashboardElement.TYPE_EVENT_REPORT, lastUpdated, isUpdating));
        dashboardElements.addAll(updateDashboardElementsByType(
                DashboardElement.TYPE_USERS, lastUpdated, isUpdating));
        dashboardElements.addAll(updateDashboardElementsByType(
                DashboardElement.TYPE_REPORTS, lastUpdated, isUpdating));
        dashboardElements.addAll(updateDashboardElementsByType(
                DashboardElement.TYPE_RESOURCES, lastUpdated, isUpdating));
        return dashboardElements;
    }

    private List<DashboardElement> updateDashboardElementsByType(final String type, final DateTime lastUpdated,
                                                                 final boolean isUpdating) throws RetrofitError {
        final Map<String, String> QUERY_MAP_BASIC = new HashMap<>();
        final Map<String, String> QUERY_MAP_FULL = new HashMap<>();

        QUERY_MAP_BASIC.put("fields", "id");
        QUERY_MAP_FULL.put("fields", "id,created,lastUpdated,name,displayName");

        if (isUpdating) {
            QUERY_MAP_FULL.put("filter", "lastUpdated:gt:" + lastUpdated.toString());
        }

        return new AbsBaseController<DashboardElement>() {

            @Override public List<DashboardElement> getExistingItems() {
                return getDashboardElementsByType(type, QUERY_MAP_BASIC);
            }

            @Override public List<DashboardElement> getUpdatedItems() {
                List<DashboardElement> elements =
                        getDashboardElementsByType(type, QUERY_MAP_FULL);
                if (elements != null && !elements.isEmpty()) {
                    for (DashboardElement element : elements) {
                        element.setType(type);
                    }
                }
                return elements;
            }

            @Override public List<DashboardElement> getPersistedItems() {
                return new Select().from(DashboardElement.class)
                        .where(Condition.column(DashboardElement$Table.TYPE).is(type))
                        .queryList();
            }
        }.run();
    }

    private List<DashboardElement> getDashboardElementsByType(String type,
                                                              Map<String, String> queryParams) throws RetrofitError {
        switch (type) {
            case DashboardElement.TYPE_CHART:
                return unwrapResponse(mDhisApi.getCharts(queryParams), "charts");
            case DashboardElement.TYPE_EVENT_CHART:
                return unwrapResponse(mDhisApi.getEventCharts(queryParams), "eventCharts");
            case DashboardElement.TYPE_MAP:
                return unwrapResponse(mDhisApi.getMaps(queryParams), "maps");
            case DashboardElement.TYPE_REPORT_TABLES:
                return unwrapResponse(mDhisApi.getReportTables(queryParams), "reportTables");
            case DashboardElement.TYPE_EVENT_REPORT:
                return unwrapResponse(mDhisApi.getEventReports(queryParams), "eventReports");
            case DashboardElement.TYPE_USERS:
                return unwrapResponse(mDhisApi.getUsers(queryParams), "users");
            case DashboardElement.TYPE_REPORTS:
                return unwrapResponse(mDhisApi.getReports(queryParams), "reports");
            case DashboardElement.TYPE_RESOURCES:
                return unwrapResponse(mDhisApi.getResources(queryParams), "documents");
            default:
                throw new IllegalArgumentException("Unsupported DashboardElement type");
        }
    }
}