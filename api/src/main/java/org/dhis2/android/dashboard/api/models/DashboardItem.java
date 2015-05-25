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
    // TODO think about using StaggeredView in DashboardFragment
    public static final String SHAPE_NORMAL = "normal";
    public static final String SHAPE_DOUBLE_WIDTH = "double_width";
    public static final String SHAPE_FULL_WIDTH = "full_width";

    @JsonIgnore private String dashboardId;
    @JsonProperty("access") private Access access;
    @JsonProperty("contentCount") private int contentCount;
    @JsonProperty("type") private String type;
    @JsonProperty("shape") private String shape;

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

    @JsonIgnore @Override
    public boolean isItemComplete() {
        return super.isItemComplete() &&
                !(isEmpty(getType()) || getAccess() == null);
    }

    @JsonIgnore public Access getAccess() {
        return access;
    }

    @JsonIgnore public void setAccess(Access access) {
        this.access = access;
    }

    @JsonIgnore public int getContentCount() {
        return contentCount;
    }

    @JsonIgnore public void setContentCount(int contentCount) {
        this.contentCount = contentCount;
    }

    @JsonIgnore public String getType() {
        return type;
    }

    @JsonIgnore public void setType(String type) {
        this.type = type;
    }

    @JsonIgnore public String getShape() {
        return shape;
    }

    @JsonIgnore public void setShape(String shape) {
        this.shape = shape;
    }

    @JsonIgnore public String getDashboardId() {
        return dashboardId;
    }

    @JsonIgnore public void setDashboardId(String dashboardId) {
        this.dashboardId = dashboardId;
    }

    @JsonIgnore public List<DashboardElement> getUsers() {
        return users;
    }

    @JsonIgnore public void setUsers(List<DashboardElement> users) {
        this.users = users;
    }

    @JsonIgnore public List<DashboardElement> getReports() {
        return reports;
    }

    @JsonIgnore public void setReports(List<DashboardElement> reports) {
        this.reports = reports;
    }

    @JsonIgnore public List<DashboardElement> getResources() {
        return resources;
    }

    @JsonIgnore public void setResources(List<DashboardElement> resources) {
        this.resources = resources;
    }

    @JsonIgnore public List<DashboardElement> getReportTables() {
        return reportTables;
    }

    @JsonIgnore public void setReportTables(List<DashboardElement> reportTables) {
        this.reportTables = reportTables;
    }

    @JsonIgnore public DashboardElement getChart() {
        return chart;
    }

    @JsonIgnore public void setChart(DashboardElement chart) {
        this.chart = chart;
    }

    @JsonIgnore public DashboardElement getEventChart() {
        return eventChart;
    }

    @JsonIgnore public void setEventChart(DashboardElement eventChart) {
        this.eventChart = eventChart;
    }

    @JsonIgnore public DashboardElement getReportTable() {
        return reportTable;
    }

    @JsonIgnore public void setReportTable(DashboardElement reportTable) {
        this.reportTable = reportTable;
    }

    @JsonIgnore public DashboardElement getMap() {
        return map;
    }

    @JsonIgnore public void setMap(DashboardElement map) {
        this.map = map;
    }

    @JsonIgnore public DashboardElement getEventReport() {
        return eventReport;
    }

    @JsonIgnore public void setEventReport(DashboardElement eventReport) {
        this.eventReport = eventReport;
    }

    @JsonIgnore public boolean isMessages() {
        return messages;
    }

    @JsonIgnore public void setMessages(boolean messages) {
        this.messages = messages;
    }
}