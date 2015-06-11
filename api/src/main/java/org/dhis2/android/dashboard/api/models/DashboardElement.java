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
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.dhis2.android.dashboard.api.persistence.DbDhis;
import org.joda.time.DateTime;

@Table(databaseName = DbDhis.NAME)
public final class DashboardElement extends BaseModel implements BaseIdentifiableModel, DisplayNameModel {
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

    @JsonProperty("id") @Column @PrimaryKey String id;
    @JsonProperty("created") @Column @NotNull DateTime created;
    @JsonProperty("lastUpdated") @Column @NotNull DateTime lastUpdated;
    @JsonProperty("name") @Column String name;
    @JsonProperty("displayName") @Column String displayName;
    @JsonIgnore @Column @NotNull String type;

    @JsonIgnore public String getDisplayName() {
        return displayName;
    }

    @JsonIgnore public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @JsonIgnore public String getType() {
        return type;
    }

    @JsonIgnore public void setType(String type) {
        this.type = type;
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
        return name;
    }

    @JsonIgnore @Override public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore public static String getResourceName(String type) {
        switch (type) {
            case DashboardElement.TYPE_CHART:
                return "charts";
            case DashboardElement.TYPE_EVENT_CHART:
                return "eventCharts";
            case DashboardElement.TYPE_MAP:
                return "maps";
            case DashboardElement.TYPE_REPORT_TABLE:
                return "reportTables";
            case DashboardElement.TYPE_EVENT_REPORT:
                return "eventReports";
            case DashboardElement.TYPE_USERS:
                return "users";
            case DashboardElement.TYPE_REPORTS:
                return "reports";
            case DashboardElement.TYPE_RESOURCES:
                return "documents";
            case DashboardElement.TYPE_REPORT_TABLES:
                return "reportTables";
            default: {
                throw new IllegalArgumentException("Unsupported DashboardElement type");
            }
        }
    }
}