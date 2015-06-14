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
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.dhis2.android.dashboard.api.persistence.DbDhis;
import org.dhis2.android.dashboard.api.persistence.preferences.DateTimeManager;
import org.joda.time.DateTime;

import java.util.List;

@Table(databaseName = DbDhis.NAME)
public final class Dashboard extends BaseModel implements BaseIdentifiableModel, DisplayNameModel {
    @JsonIgnore @Column @PrimaryKey(autoincrement = true) long localId;
    @JsonIgnore @Column @NotNull State state;
    @JsonProperty("id") @Column String id;
    @JsonProperty("created") @Column @NotNull DateTime created;
    @JsonProperty("lastUpdated") @Column @NotNull DateTime lastUpdated;
    @JsonProperty("access") @Column @NotNull Access access;
    @JsonProperty("name") @Column String name;
    @JsonProperty("displayName") @Column String displayName;
    @JsonProperty("dashboardItems") List<DashboardItem> dashboardItems;

    public Dashboard() {
        state = State.SYNCED;
    }

    @JsonIgnore public static List<DashboardItem> queryRelatedDashboardItems(Dashboard dashboard) {
        return new Select().from(DashboardItem.class)
                .where(Condition.column(DashboardItem$Table
                        .DASHBOARD_DASHBOARD).is(dashboard.getLocalId()))
                .queryList();
    }

    /**
     * Note! This method will change the name and state of model.
     * <p/>
     * If the current state of model is State.TO_DELETE or State.TO_POST,
     * state won't be changed.
     */
    @JsonIgnore public void modifyName(String newName) {
        name = newName;
        displayName = newName;

        if (state != State.TO_DELETE && state != State.TO_POST) {
            state = State.TO_UPDATE;
        }

        super.save();
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

    /**
     * @param name Name and display name of new dashboard.
     */
    @JsonIgnore public static Dashboard createAndSaveDashboard(String name) {
        DateTime currentDateTime = DateTimeManager.getInstance()
                .getCurrentDateTimeInServerTimeZone();
        Dashboard dashboard = new Dashboard();
        dashboard.setState(State.TO_POST);
        dashboard.setName(name);
        dashboard.setDisplayName(name);
        dashboard.setCreated(currentDateTime);
        dashboard.setLastUpdated(currentDateTime);
        dashboard.setAccess(Access.provideDefaultAccess());
        dashboard.save();
        return dashboard;
    }

    @JsonIgnore public State getState() {
        return state;
    }

    @JsonIgnore public void setState(State state) {
        this.state = state;
    }

    @JsonIgnore @Override
    public long getLocalId() {
        return localId;
    }

    @JsonIgnore @Override
    public void setLocalId(long localId) {
        this.localId = localId;
    }

    @JsonIgnore @Override
    public DateTime getCreated() {
        return created;
    }

    @JsonIgnore @Override
    public void setCreated(DateTime created) {
        this.created = created;
    }

    @JsonIgnore @Override
    public DateTime getLastUpdated() {
        return lastUpdated;
    }

    @JsonIgnore @Override
    public void setLastUpdated(DateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @JsonIgnore @Override
    public String getId() {
        return id;
    }

    @JsonIgnore @Override
    public void setId(String id) {
        this.id = id;
    }

    @JsonIgnore @Override
    public String getName() {
        return name;
    }

    @JsonIgnore @Override
    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore @Override
    public String getDisplayName() {
        return displayName;
    }

    @JsonIgnore @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @JsonIgnore
    public Access getAccess() {
        return access;
    }

    @JsonIgnore
    public void setAccess(Access access) {
        this.access = access;
    }

    @JsonIgnore
    public List<DashboardItem> getDashboardItems() {
        return dashboardItems;
    }

    @JsonIgnore
    public void setDashboardItems(List<DashboardItem> dashboardItems) {
        this.dashboardItems = dashboardItems;
    }

    @JsonIgnore @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append(" displayName: ");
        builder.append(displayName);

        builder.append(" access: ");
        builder.append(access == null ? "null" : access.toString());

        return builder.toString();
    }
}