/*
 * Copyright (c) 2015, dhis2
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.dhis2.android.dashboard.api.controllers;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.dhis2.android.dashboard.api.DhisManager;
import org.dhis2.android.dashboard.api.models.DashboardItemContent;
import org.dhis2.android.dashboard.api.models.DashboardItemContent$Table;
import org.dhis2.android.dashboard.api.models.meta.DbOperation;
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

import static org.dhis2.android.dashboard.api.utils.MergeUtils.merge;
import static org.dhis2.android.dashboard.api.utils.NetworkUtils.unwrapResponse;

/**
 * @author Araz Abishov <araz.abishov.gsoc@gmail.com>.
 */
public final class ContentController implements IController<Object> {
    private final DhisApi mDhisApi;

    public ContentController(DhisManager dhisManager) {
        mDhisApi = RepoManager.createService(dhisManager.getServerUrl(),
                dhisManager.getUserCredentials());
    }

    @Override
    public Object run() throws APIException {
        DateTime lastUpdated = DateTimeManager.getInstance()
                .getLastUpdated(ResourceType.CONTENT);
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
                .setLastUpdated(ResourceType.CONTENT, serverDateTime);
        return new Object();
    }

    private List<DashboardItemContent> updateApiResources(DateTime lastUpdated) throws RetrofitError {
        List<DashboardItemContent> dashboardItemContent = new ArrayList<>();
        dashboardItemContent.addAll(updateApiResourceByType(
                DashboardItemContent.TYPE_CHART, lastUpdated));
        dashboardItemContent.addAll(updateApiResourceByType(
                DashboardItemContent.TYPE_EVENT_CHART, lastUpdated));
        dashboardItemContent.addAll(updateApiResourceByType(
                DashboardItemContent.TYPE_MAP, lastUpdated));
        dashboardItemContent.addAll(updateApiResourceByType(
                DashboardItemContent.TYPE_REPORT_TABLES, lastUpdated));
        dashboardItemContent.addAll(updateApiResourceByType(
                DashboardItemContent.TYPE_EVENT_REPORT, lastUpdated));
        dashboardItemContent.addAll(updateApiResourceByType(
                DashboardItemContent.TYPE_USERS, lastUpdated));
        dashboardItemContent.addAll(updateApiResourceByType(
                DashboardItemContent.TYPE_REPORTS, lastUpdated));
        dashboardItemContent.addAll(updateApiResourceByType(
                DashboardItemContent.TYPE_RESOURCES, lastUpdated));
        return dashboardItemContent;
    }

    private List<DashboardItemContent> updateApiResourceByType(final String type,
                                                               final DateTime lastUpdated) throws RetrofitError {
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

    private List<DashboardItemContent> getApiResourceByType(String type, Map<String, String> queryParams) throws RetrofitError {
        switch (type) {
            case DashboardItemContent.TYPE_CHART:
                return unwrapResponse(mDhisApi.getCharts(queryParams), "charts");
            case DashboardItemContent.TYPE_EVENT_CHART:
                return unwrapResponse(mDhisApi.getEventCharts(queryParams), "eventCharts");
            case DashboardItemContent.TYPE_MAP:
                return unwrapResponse(mDhisApi.getMaps(queryParams), "maps");
            case DashboardItemContent.TYPE_REPORT_TABLES:
                return unwrapResponse(mDhisApi.getReportTables(queryParams), "reportTables");
            case DashboardItemContent.TYPE_EVENT_REPORT:
                return unwrapResponse(mDhisApi.getEventReports(queryParams), "eventReports");
            case DashboardItemContent.TYPE_USERS:
                return unwrapResponse(mDhisApi.getUsers(queryParams), "users");
            case DashboardItemContent.TYPE_REPORTS:
                return unwrapResponse(mDhisApi.getReports(queryParams), "reports");
            case DashboardItemContent.TYPE_RESOURCES:
                return unwrapResponse(mDhisApi.getResources(queryParams), "documents");
            default:
                throw new IllegalArgumentException("Unsupported DashboardItemContent type");
        }
    }
}
