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

import android.util.Log;

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
import org.dhis2.android.dashboard.api.persistence.preferences.DateTimeManager;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import static android.text.TextUtils.isEmpty;
import static org.dhis2.android.dashboard.api.utils.Preconditions.isNull;

@Table(databaseName = DbDhis.NAME)
public final class DashboardItem extends BaseModel implements BaseIdentifiableModel {
    private static final String TAG = DashboardItem.class.getSimpleName();

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
    @JsonProperty("type") @Column String type;
    @JsonProperty("shape") @Column String shape;

    @JsonIgnore @Column @ForeignKey(
            references = {
                    @ForeignKeyReference(columnName = DASHBOARD_KEY, columnType = long.class, foreignColumnName = "localId")
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE
    ) Dashboard dashboard;

    // DashboardElements
    @JsonProperty("chart") DashboardElement chart;
    @JsonProperty("eventChart") DashboardElement eventChart;
    @JsonProperty("map") DashboardElement map;
    @JsonProperty("reportTable") DashboardElement reportTable;
    @JsonProperty("eventReport") DashboardElement eventReport;
    @JsonProperty("users") List<DashboardElement> users;
    @JsonProperty("reports") List<DashboardElement> reports;
    @JsonProperty("resources") List<DashboardElement> resources;
    @JsonProperty("reportTables") List<DashboardElement> reportTables;
    @JsonProperty("messages") boolean messages;

    public DashboardItem() {
        state = State.SYNCED;
        shape = SHAPE_NORMAL;
    }

    /**
     * This method will change the state of the model to TO_DELETE if the model was already synced to the server.
     * If model was created only locally, it will delete it from embedded database.
     */
    @JsonIgnore public void softDelete() {
        if (state == State.TO_POST) {
            super.delete();
        } else {
            state = State.TO_DELETE;
            super.save();
        }
    }

    @JsonIgnore public boolean addDashboardElement(ApiResource resource) {
        isNull(resource, "ApiResource object must not be null");

        if (!resource.getType().equals(getType())) {
            throw new IllegalArgumentException("ApiResource is not compatible " +
                    "with this DashboardItem");
        }

        switch (resource.getType()) {
            case ApiResource.TYPE_USERS:
            case ApiResource.TYPE_REPORTS:
            case ApiResource.TYPE_RESOURCES:
            case ApiResource.TYPE_REPORT_TABLES: {
                if (getElementsCount() > 8) {
                    return false;
                }

                DashboardElement element = new DashboardElement();
                element.setId(resource.getId());
                element.setName(resource.getName());
                element.setCreated(resource.getCreated());
                element.setLastUpdated(resource.getLastUpdated());
                element.setDisplayName(resource.getDisplayName());
                element.setState(State.TO_POST);
                element.setDashboardItem(this);
                element.save();

                return true;
            }
            default:
                throw new IllegalArgumentException("You cannot add " + resource.getType() +
                        " resource more that once to DashboardItem");
        }
    }

    @JsonIgnore public boolean removeDashboardElement(DashboardElement element) {
        isNull(element, "DashboardElement object must not be null");

        long elementsCount = getElementsCount();

        DashboardElement assignedElement = new Select()
                .from(DashboardElement.class)
                .where(
                        Condition.column(DashboardElement$Table.DASHBOARDITEM_DASHBOARDITEM).is(localId),
                        Condition.column(DashboardElement$Table.LOCALID).is(element.getLocalId()))
                .querySingle();

        if (assignedElement == null) {
            Log.d(TAG, "Could not find DashboardElement to remove");
            return false;
        }

        if (assignedElement.getState().equals(State.TO_POST)) {
            assignedElement.delete();
        } else {
            assignedElement.setState(State.TO_DELETE);
            assignedElement.save();
        }

        if (elementsCount <= 1) {
            if (getState() == State.TO_POST) {
                delete();
            } else {
                setState(State.TO_DELETE);
                save();
            }
        }

        return true;
    }

    @JsonIgnore private long getElementsCount() {
        return new Select().from(DashboardElement.class)
                .where(
                        Condition.column(DashboardElement$Table.DASHBOARDITEM_DASHBOARDITEM).is(localId),
                        Condition.column(DashboardElement$Table.STATE).isNot(State.TO_DELETE.toString()))
                .count();
    }

    /**
     * @param dashboard Dashboard object to which we want to bind dashboard item
     * @param resource  ApiResource which will be the content of our item.
     * @return DashboardItem
     */
    @JsonIgnore public static DashboardItem createAndSaveDashboardItem(Dashboard dashboard,
                                                                       ApiResource resource) {
        isNull(dashboard, "Dashboard object must not be null");
        isNull(resource, "ApiResource object must not be null");

        /* first we need to create dashboard item itself and tight it to Dashboard */
        DashboardItem item = new DashboardItem();
        DateTime currentTime = DateTimeManager.getInstance()
                .getCurrentDateTimeInServerTimeZone();
        item.setCreated(currentTime);
        item.setLastUpdated(currentTime);
        item.setAccess(Access.provideDefaultAccess());
        item.setType(resource.getType());
        item.setDashboard(dashboard);
        item.setState(State.TO_POST);
        item.setShape(SHAPE_NORMAL);
        item.save();

        /* reach dashboard item requires to contain at least one
        dashboard resource assigned to it */
        DashboardElement assignedElement = new DashboardElement();
        assignedElement.setId(resource.getId());
        assignedElement.setName(resource.getName());
        assignedElement.setCreated(resource.getCreated());
        assignedElement.setLastUpdated(resource.getLastUpdated());
        assignedElement.setDisplayName(resource.getDisplayName());
        assignedElement.setState(State.TO_POST);
        assignedElement.setDashboardItem(item);
        assignedElement.save();

        return item;
    }

    @JsonIgnore public static void readElementsIntoItem(DashboardItem item) {
        if (isEmpty(item.getType())) {
            return;
        }

        switch (item.getType()) {
            case ApiResource.TYPE_CHART: {
                item.setChart(queryRelatedDashboardElementFromDb(item));
                break;
            }
            case ApiResource.TYPE_EVENT_CHART: {
                item.setEventChart(queryRelatedDashboardElementFromDb(item));
                break;
            }
            case ApiResource.TYPE_MAP: {
                item.setMap(queryRelatedDashboardElementFromDb(item));
                break;
            }
            case ApiResource.TYPE_REPORT_TABLE: {
                item.setReportTable(queryRelatedDashboardElementFromDb(item));
                break;
            }
            case ApiResource.TYPE_EVENT_REPORT: {
                item.setEventReport(queryRelatedDashboardElementFromDb(item));
                break;
            }
            case ApiResource.TYPE_USERS: {
                item.setUsers(queryRelatedDashboardElementsFromDb(item));
                break;
            }
            case ApiResource.TYPE_REPORTS: {
                item.setReports(queryRelatedDashboardElementsFromDb(item));
                break;
            }
            case ApiResource.TYPE_RESOURCES: {
                item.setResources(queryRelatedDashboardElementsFromDb(item));
                break;
            }
            case ApiResource.TYPE_REPORT_TABLES: {
                item.setReportTables(queryRelatedDashboardElementsFromDb(item));
                break;
            }
        }
    }

    @JsonIgnore public static List<DashboardElement> getDashboardElementsFromItem(DashboardItem item) {
        isNull(item, "DashboardItem object must not be null");

        List<DashboardElement> elements = new ArrayList<>();
        if (isEmpty(item.getType())) {
            return elements;
        }

        switch (item.getType()) {
            case ApiResource.TYPE_CHART: {
                elements.add(item.getChart());
                break;
            }
            case ApiResource.TYPE_EVENT_CHART: {
                elements.add(item.getEventChart());
                break;
            }
            case ApiResource.TYPE_MAP: {
                elements.add(item.getMap());
                break;
            }
            case ApiResource.TYPE_REPORT_TABLE: {
                elements.add(item.getReportTable());
                break;
            }
            case ApiResource.TYPE_EVENT_REPORT: {
                elements.add(item.getEventReport());
                break;
            }
            case ApiResource.TYPE_USERS: {
                elements.addAll(item.getUsers());
                break;
            }
            case ApiResource.TYPE_REPORTS: {
                elements.addAll(item.getReports());
                break;
            }
            case ApiResource.TYPE_RESOURCES: {
                elements.addAll(item.getResources());
                break;
            }
            case ApiResource.TYPE_REPORT_TABLES: {
                elements.addAll(item.getReportTables());
                break;
            }
        }

        return elements;
    }

    @JsonIgnore public static DashboardElement queryRelatedDashboardElementFromDb(DashboardItem item) {
        return new Select().from(DashboardElement.class)
                .where(Condition.column(DashboardElement$Table
                        .DASHBOARDITEM_DASHBOARDITEM).is(item.getLocalId()))
                .querySingle();
    }

    @JsonIgnore public static List<DashboardElement> queryRelatedDashboardElementsFromDb(DashboardItem item) {
        List<DashboardElement> elements = new Select().from(DashboardElement.class)
                .where(Condition.column(DashboardElement$Table
                        .DASHBOARDITEM_DASHBOARDITEM).is(item.getLocalId()))
                .queryList();
        if (elements == null) {
            elements = new ArrayList<>();
        }

        return elements;
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

    @JsonIgnore @Override
    public long getLocalId() {
        return localId;
    }

    @JsonIgnore @Override
    public void setLocalId(long localId) {
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