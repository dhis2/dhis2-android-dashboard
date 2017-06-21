package org.hisp.dhis.android.dashboard.api.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import org.hisp.dhis.android.dashboard.api.models.meta.DbDhis;

import java.util.List;

@Table(databaseName = DbDhis.NAME)
public final class EventReport extends BaseIdentifiableObject {
    @JsonIgnore
    @Column(name = "id")
    @PrimaryKey(autoincrement = true)
    long id;

    @JsonProperty("id")
    @Column(name = "uId")
    String uId;

    UIDObject programStage;
    List<UIDObject> organisationUnits;
    RelativePeriod relativePeriods;

}
