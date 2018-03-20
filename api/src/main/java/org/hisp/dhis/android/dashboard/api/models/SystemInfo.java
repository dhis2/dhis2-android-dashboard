package org.hisp.dhis.android.dashboard.api.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.hisp.dhis.android.dashboard.api.models.meta.DbDhis;
import org.joda.time.DateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(databaseName = DbDhis.NAME)
public final class SystemInfo extends BaseModel {

    @Column(name = "id")
    @PrimaryKey
    int id = 1; //there should only be one row of this which overwrites every time its reloaded

    @JsonProperty("buildTime")
    @Column(name = "buildTime")
    DateTime buildTime;

    @JsonProperty("serverDate")
    @Column(name = "serverDate")
    DateTime serverDate;

    @JsonProperty("calendar")
    @Column(name = "calendar")
    String calendar;

    @JsonProperty("dateFormat")
    @Column(name = "dateFormat")
    String dateFormat;

    @JsonProperty("intervalSinceLastAnalyticsTableSuccess")
    @Column(name = "intervalSinceLastAnalyticsTableSuccess")
    String intervalSinceLastAnalyticsTableSuccess;

    @JsonProperty("lastAnalyticsTableSuccess")
    @Column(name = "lastAnalyticsTableSuccess")
    String lastAnalyticsTableSuccess;

    @JsonProperty("revision")
    @Column(name = "revision")
    String revision;

    @JsonProperty("version")
    @Column(name = "version")
    String version;

    @JsonIgnore
    public DateTime getBuildTime() {
        return buildTime;
    }

    @JsonIgnore
    public void setBuildTime(DateTime buildTime) {
        this.buildTime = buildTime;
    }

    @JsonIgnore
    public DateTime getServerDate() {
        return serverDate;
    }

    @JsonIgnore
    public void setServerDate(DateTime serverDate) {
        this.serverDate = serverDate;
    }

    @JsonIgnore
    public String getCalendar() {
        return calendar;
    }

    @JsonIgnore
    public void setCalendar(String calendar) {
        this.calendar = calendar;
    }

    @JsonIgnore
    public String getDateFormat() {
        return dateFormat;
    }

    @JsonIgnore
    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    @JsonIgnore
    public String getIntervalSinceLastAnalyticsTableSuccess() {
        return intervalSinceLastAnalyticsTableSuccess;
    }

    @JsonIgnore
    public void setIntervalSinceLastAnalyticsTableSuccess(String date) {
        this.intervalSinceLastAnalyticsTableSuccess = date;
    }

    @JsonIgnore
    public String getLastAnalyticsTableSuccess() {
        return lastAnalyticsTableSuccess;
    }

    @JsonIgnore
    public void setLastAnalyticsTableSuccess(String lastAnalyticsTableSuccess) {
        this.lastAnalyticsTableSuccess = lastAnalyticsTableSuccess;
    }

    @JsonIgnore
    public String getRevision() {
        return revision;
    }

    @JsonIgnore
    public void setRevision(String revision) {
        this.revision = revision;
    }

    @JsonIgnore
    public String getVersion() {
        return version;
    }

    @JsonIgnore
    public void setVersion(String version) {
        this.version = version;
    }
}
