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

package org.dhis2.android.dashboard.api.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import static android.text.TextUtils.isEmpty;

public class DashboardItem extends BaseIdentifiableModel {
    public static final String TYPE_CHART = "chart";
    public static final String TYPE_EVENT_CHART = "eventChart";
    public static final String TYPE_MAP = "map";
    public static final String TYPE_REPORT_TABLE = "reportTable";
    public static final String TYPE_EVENT_REPORT = "eventReport";
    public static final String TYPE_USERS = "users";
    public static final String TYPE_REPORTS = "reports";
    public static final String TYPE_RESOURCES = "resources";
    public static final String TYPE_REPORT_TABLES = "reportTables";
    public static final String TYPE_MESSAGES = "messages";

    // TODO think about using StaggeredView in DashboardFragment
    public static final String SHAPE_NORMAL = "normal";
    public static final String SHAPE_DOUBLE_WIDTH = "double_width";
    public static final String SHAPE_FULL_WIDTH = "full_width";

    @JsonProperty("access") private Access access;
    @JsonProperty("contentCount") private int contentCount;
    @JsonProperty("type") private String type;
    @JsonProperty("shape") private String shape;
    @JsonIgnore() private String dashboardId;

    // DashboardElements
    @JsonProperty("chart") private DashboardElement chart;
    @JsonProperty("eventChart") private DashboardElement eventChart;
    @JsonProperty("map") private DashboardElement map;
    @JsonProperty("reportTable") private DashboardElement reportTable;
    @JsonProperty("eventReport") private DashboardElement eventReport;
    @JsonProperty("users") private List<DashboardElement> users;
    @JsonProperty("reports") private List<DashboardElement> reports;
    @JsonProperty("resources") private List<DashboardElement> resources;
    @JsonProperty("reportTables") private List<DashboardElement> reportTables;
    @JsonProperty("messages") private boolean messages;

    @Override
    public boolean isItemComplete() {
        return super.isItemComplete() &&
                !(isEmpty(getType()) || isEmpty(getShape())
                        || getAccess() == null);
    }

    public Access getAccess() {
        return access;
    }

    public void setAccess(Access access) {
        this.access = access;
    }

    public int getContentCount() {
        return contentCount;
    }

    public void setContentCount(int contentCount) {
        this.contentCount = contentCount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getShape() {
        return shape;
    }

    public void setShape(String shape) {
        this.shape = shape;
    }

    public String getDashboardId() {
        return dashboardId;
    }

    public void setDashboardId(String dashboardId) {
        this.dashboardId = dashboardId;
    }

    public List<DashboardElement> getUsers() {
        return users;
    }

    public void setUsers(List<DashboardElement> users) {
        this.users = users;
    }

    public List<DashboardElement> getReports() {
        return reports;
    }

    public void setReports(List<DashboardElement> reports) {
        this.reports = reports;
    }

    public List<DashboardElement> getResources() {
        return resources;
    }

    public void setResources(List<DashboardElement> resources) {
        this.resources = resources;
    }

    public List<DashboardElement> getReportTables() {
        return reportTables;
    }

    public void setReportTables(List<DashboardElement> reportTables) {
        this.reportTables = reportTables;
    }

    public DashboardElement getChart() {
        return chart;
    }

    public void setChart(DashboardElement chart) {
        this.chart = chart;
    }

    public DashboardElement getEventChart() {
        return eventChart;
    }

    public void setEventChart(DashboardElement eventChart) {
        this.eventChart = eventChart;
    }

    public DashboardElement getReportTable() {
        return reportTable;
    }

    public void setReportTable(DashboardElement reportTable) {
        this.reportTable = reportTable;
    }

    public DashboardElement getMap() {
        return map;
    }

    public void setMap(DashboardElement map) {
        this.map = map;
    }

    public DashboardElement getEventReport() {
        return eventReport;
    }

    public void setEventReport(DashboardElement eventReport) {
        this.eventReport = eventReport;
    }


    public boolean isMessages() {
        return messages;
    }

    public void setMessages(boolean messages) {
        this.messages = messages;
    }
}