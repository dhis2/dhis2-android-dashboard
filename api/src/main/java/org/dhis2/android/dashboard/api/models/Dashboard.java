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
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.dhis2.android.dashboard.api.persistence.DbDhis;
import org.dhis2.android.dashboard.api.persistence.preferences.DateTimeManager;
import org.joda.time.DateTime;

import java.util.List;

import static org.dhis2.android.dashboard.api.utils.Preconditions.isNull;

@Table(databaseName = DbDhis.NAME)
public final class Dashboard extends BaseIdentifiableObject {

    @JsonIgnore
    @Column(name = "state")
    @NotNull
    State state;

    @JsonProperty("dashboardItems")
    List<DashboardItem> dashboardItems;

    public Dashboard() {
        state = State.SYNCED;
    }

    /////////////////////////////////////////////////////////////////////////
    // Dashboard logic
    /////////////////////////////////////////////////////////////////////////

    /**
     * Creates and saves Dashboard with given name to the local database.
     *
     * @param name Name of new dashboard.
     */
    @JsonIgnore
    public static Dashboard createDashboard(String name) {
        DateTime currentDateTime = DateTimeManager.getInstance()
                .getCurrentDateTimeInServerTimeZone();

        Dashboard dashboard = new Dashboard();
        dashboard.setState(State.TO_POST);
        dashboard.setName(name);
        dashboard.setDisplayName(name);
        dashboard.setCreated(currentDateTime);
        dashboard.setLastUpdated(currentDateTime);
        dashboard.setAccess(provideDefaultAccess());
        dashboard.save();

        return dashboard;
    }

    /**
     * This method will change the name and the State of dashboard.
     * <p/>
     * If the current state of model is State.TO_DELETE or State.TO_POST,
     * state won't be changed. Otherwise, it will be changed to State.TO_UPDATE.
     */
    @JsonIgnore
    public void modifyName(String newName) {
        setName(newName);
        setDisplayName(newName);

        if (state != State.TO_DELETE && state != State.TO_POST) {
            state = State.TO_UPDATE;
        }

        super.save();
    }

    /**
     * This method will change the state of the model to State.TO_DELETE
     * if the model was already synced to the server.
     * <p/>
     * If model was created only locally, it will delete it
     * from local database.
     */
    @JsonIgnore
    public void softDelete() {
        if (state == State.TO_POST) {
            super.delete();
        } else {
            state = State.TO_DELETE;
            super.save();
        }
    }

    @JsonIgnore
    public static List<DashboardItem> queryRelatedDashboardItems(Dashboard dashboard) {
        return new Select().from(DashboardItem.class)
                .where(Condition.column(DashboardItem$Table
                        .DASHBOARD_DASHBOARD).is(dashboard.getId()))
                .queryList();
    }

    /////////////////////////////////////////////////////////////////////////
    // DashboardItem logic
    /////////////////////////////////////////////////////////////////////////

    @JsonIgnore
    public void addDashboardItem(DashboardItemContent resource) {
        isNull(resource, "DashboardItemContent object must not be null");

        switch (resource.getType()) {
            case DashboardItemContent.TYPE_CHART:
            case DashboardItemContent.TYPE_EVENT_CHART:
            case DashboardItemContent.TYPE_MAP:
            case DashboardItemContent.TYPE_EVENT_REPORT:
            case DashboardItemContent.TYPE_REPORT_TABLE: {
                createAndSaveDashboardItem(resource);
                break;
            }
            case DashboardItemContent.TYPE_USERS:
            case DashboardItemContent.TYPE_REPORTS:
            case DashboardItemContent.TYPE_RESOURCES:
            case DashboardItemContent.TYPE_REPORT_TABLES: {
                if (!tryToAddElementToItems(resource)) {
                    createAndSaveDashboardItem(resource);
                }
                break;
            }
        }
    }

    private void createAndSaveDashboardItem(DashboardItemContent resource) {
        DashboardItem item
                = new DashboardItem();
        item.setCreated(DateTimeManager.getInstance()
                .getCurrentDateTimeInServerTimeZone());
        item.setLastUpdated(DateTimeManager.getInstance()
                .getCurrentDateTimeInServerTimeZone());
        item.setState(State.TO_POST);
        item.setDashboard(this);
        item.setAccess(provideDefaultAccess());
        item.setType(resource.getType());
        item.save();

        DashboardElement element = new DashboardElement();
        element.setUId(resource.getUId());
        element.setName(resource.getName());
        element.setCreated(resource.getCreated());
        element.setLastUpdated(resource.getLastUpdated());
        element.setDisplayName(resource.getDisplayName());
        element.setState(State.TO_POST);
        element.setDashboardItem(item);
        element.save();

        System.out.println("TYPE: " + item.getType());
    }

    private boolean tryToAddElementToItems(DashboardItemContent resource) {
        List<DashboardItem> items = new Select()
                .from(DashboardItem.class)
                .where(Condition.column(DashboardItem$Table.DASHBOARD_DASHBOARD).is(getId()))
                .and(Condition.column(DashboardItem$Table.TYPE).is(resource.getType()))
                .queryList();

        if (items == null || items.isEmpty()) {
            return false;
        }

        for (DashboardItem item : items) {
            if (item.addDashboardElement(resource)) {
                return true;
            }
        }

        return false;
    }

    /////////////////////////////////////////////////////////////////////////
    // Getters and setters
    /////////////////////////////////////////////////////////////////////////

    @JsonIgnore
    public State getState() {
        return state;
    }

    @JsonIgnore
    public void setState(State state) {
        this.state = state;
    }

    @JsonIgnore
    public List<DashboardItem> getDashboardItems() {
        return dashboardItems;
    }

    @JsonIgnore
    public void setDashboardItems(List<DashboardItem> dashboardItems) {
        this.dashboardItems = dashboardItems;
    }

    static Access provideDefaultAccess() {
        Access access = new Access();
        access.setManage(true);
        access.setExternalize(true);
        access.setWrite(true);
        access.setUpdate(true);
        access.setRead(true);
        access.setDelete(true);
        return access;
    }
}