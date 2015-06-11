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
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.dhis2.android.dashboard.api.persistence.DbDhis;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

@Table(databaseName = DbDhis.NAME)
public final class DashboardItem extends BaseModel implements BaseIdentifiableModel {
    private static final String DASHBOARD_KEY = "dashboard";

    public static final String SHAPE_NORMAL = "normal";
    public static final String SHAPE_DOUBLE_WIDTH = "double_width";
    public static final String SHAPE_FULL_WIDTH = "full_width";

    @JsonIgnore @Column @PrimaryKey(autoincrement = true) long localId;
    @JsonIgnore @Column @NotNull State state;
    @JsonProperty("id") @Column String id;
    @JsonProperty("created") @Column @NotNull DateTime created;
    @JsonProperty("lastUpdated") @Column @NotNull DateTime lastUpdated;
    @JsonProperty("access") @Column @NotNull Access access;
    @JsonProperty("type") @Column @NotNull String type;
    @JsonProperty("shape") @Column String shape;

    @JsonIgnore @Column @ForeignKey(
            references = {
                    @ForeignKeyReference(columnName = DASHBOARD_KEY, columnType = long.class, foreignColumnName = "localId")
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE
    ) Dashboard dashboard;

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

    @JsonIgnore public static void readElementsIntoItem(DashboardItem item) {
        switch (item.getType()) {
            case DashboardElement.TYPE_CHART: {
                item.setChart(querySingleElement(item));
                break;
            }
            case DashboardElement.TYPE_EVENT_CHART: {
                item.setEventChart(querySingleElement(item));
                break;
            }
            case DashboardElement.TYPE_MAP: {
                item.setMap(querySingleElement(item));
                break;
            }
            case DashboardElement.TYPE_REPORT_TABLE: {
                item.setReportTable(querySingleElement(item));
                break;
            }
            case DashboardElement.TYPE_EVENT_REPORT: {
                item.setEventReport(querySingleElement(item));
                break;
            }
            case DashboardElement.TYPE_USERS: {
                item.setUsers(queryElements(item));
                break;
            }
            case DashboardElement.TYPE_REPORTS: {
                item.setReports(queryElements(item));
                break;
            }
            case DashboardElement.TYPE_RESOURCES: {
                item.setResources(queryElements(item));
                break;
            }
            case DashboardElement.TYPE_REPORT_TABLES: {
                item.setReportTables(queryElements(item));
                break;
            }
        }
    }

    @JsonIgnore private static DashboardElement querySingleElement(DashboardItem item) {
        ElementToItemRelation relation = new Select().from(ElementToItemRelation.class)
                .where(Condition.column(ElementToItemRelation$Table
                        .DASHBOARDITEM_DASHBOARDITEM).is(item.getLocalId()))
                .querySingle();
        return relation.getDashboardElement();
    }

    @JsonIgnore private static List<DashboardElement> queryElements(DashboardItem item) {
        List<ElementToItemRelation> relations = new Select().from(ElementToItemRelation.class)
                .where(Condition.column(ElementToItemRelation$Table
                        .DASHBOARDITEM_DASHBOARDITEM).is(item.getLocalId()))
                .queryList();
        List<DashboardElement> dashboardElements = new ArrayList<>();
        for (ElementToItemRelation relation : relations) {
            dashboardElements.add(relation.getDashboardElement());
        }
        return dashboardElements;
    }

    @JsonIgnore public Access getAccess() {
        return access;
    }

    @JsonIgnore public void setAccess(Access access) {
        this.access = access;
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

    @JsonIgnore public Dashboard getDashboard() {
        return dashboard;
    }

    @JsonIgnore public void setDashboard(Dashboard dashboard) {
        this.dashboard = dashboard;
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

    @JsonIgnore public long getLocalId() {
        return localId;
    }

    @JsonIgnore public void setLocalId(long localId) {
        this.localId = localId;
    }

    @JsonIgnore public State getState() {
        return state;
    }

    @JsonIgnore public void setState(State state) {
        this.state = state;
    }


    @JsonIgnore @Override public String getId() {
        return id;
    }

    @JsonIgnore @Override public void setId(String id) {
        this.id = id;
    }

    @JsonIgnore @Override public DateTime getCreated() {
        return created;
    }

    @JsonIgnore @Override public void setCreated(DateTime created) {
        this.created = created;
    }

    @JsonIgnore @Override public DateTime getLastUpdated() {
        return lastUpdated;
    }

    @JsonIgnore @Override public void setLastUpdated(DateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @JsonIgnore @Override public String getName() {
        // stub implementation
        return null;
    }

    @JsonIgnore @Override public void setName(String name) {
        // stub implementation
    }
}