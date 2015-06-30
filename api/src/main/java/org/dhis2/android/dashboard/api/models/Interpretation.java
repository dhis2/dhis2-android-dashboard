/*
 * Copyright (c) 2015, University of Oslo
 *
 * All rights reserved.
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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.Table;

import org.dhis2.android.dashboard.api.models.meta.State;
import org.dhis2.android.dashboard.api.persistence.DbDhis;
import org.dhis2.android.dashboard.api.persistence.preferences.DateTimeManager;
import org.joda.time.DateTime;

import java.util.List;

@Table(databaseName = DbDhis.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
public final class Interpretation extends BaseIdentifiableObject {

    public static final String TYPE_CHART = "chart";
    public static final String TYPE_MAP = "map";
    public static final String TYPE_REPORT_TABLE = "reportTable";
    public static final String TYPE_DATASET_REPORT = "dataSetReport";

    @JsonProperty("text")
    @Column(name = "text")
    String text;

    @JsonProperty("type")
    @Column(name = "type")
    String type;

    @JsonIgnore
    @Column(name = "state")
    @NotNull
    State state;

    @JsonProperty("user")
    @Column(name = "user")
    @ForeignKey(
            references = {
                    @ForeignKeyReference(columnName = "user", columnType = long.class, foreignColumnName = "id")
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE
    )
    User user;

    @JsonProperty("chart")
    InterpretationElement chart;

    @JsonProperty("map")
    InterpretationElement map;

    @JsonProperty("reportTable")
    InterpretationElement reportTable;

    /* dataSet, period and organisationUnit
    will contain only UUIDs of objects. Also, dashboard application
    won't allow to view dataSet interpretations until dataEntry is implemented here */
    @JsonProperty("dataSet")
    String dataSet;

    @JsonProperty("period")
    String period;

    @JsonProperty("organisationUnit")
    String organisationUnit;

    @JsonProperty("comments")
    List<InterpretationComment> comments;

    public Interpretation() {
        state = State.SYNCED;
    }

    public static InterpretationComment addComment(Interpretation interpretation, User user, String text) {
        DateTime lastUpdated = DateTimeManager.getInstance()
                .getLastUpdated(DateTimeManager.ResourceType.INTERPRETATION_COMMENTS);

        InterpretationComment comment = new InterpretationComment();
        comment.setCreated(lastUpdated);
        comment.setLastUpdated(lastUpdated);
        comment.setAccess(Access.provideDefaultAccess());
        comment.setName(text);
        comment.setDisplayName(text);
        comment.setText(text);
        comment.setState(State.TO_POST);
        comment.setUser(user);
        comment.setInterpretation(interpretation);

        return comment;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public InterpretationElement getChart() {
        return chart;
    }

    public void setChart(InterpretationElement chart) {
        this.chart = chart;
    }

    public InterpretationElement getMap() {
        return map;
    }

    public void setMap(InterpretationElement map) {
        this.map = map;
    }

    public InterpretationElement getReportTable() {
        return reportTable;
    }

    public void setReportTable(InterpretationElement reportTable) {
        this.reportTable = reportTable;
    }

    public String getDataSet() {
        return dataSet;
    }

    public void setDataSet(String dataSet) {
        this.dataSet = dataSet;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getOrganisationUnit() {
        return organisationUnit;
    }

    public void setOrganisationUnit(String organisationUnit) {
        this.organisationUnit = organisationUnit;
    }

    public List<InterpretationComment> getComments() {
        return comments;
    }

    public void setComments(List<InterpretationComment> comments) {
        this.comments = comments;
    }
}
