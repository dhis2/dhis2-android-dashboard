package org.hisp.dhis.android.dashboard.api.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.hisp.dhis.android.dashboard.api.models.meta.DbDhis;

@Table(databaseName = DbDhis.NAME)
public class DataElementDimension extends BaseModel {
    @JsonIgnore
    @Column
    @PrimaryKey(autoincrement = true)
    long id;

    UIDObject dataElement;

    public DataElementDimension() {
    }

    public DataElementDimension(UIDObject dataElement) {
        this.dataElement = dataElement;
    }

    public UIDObject getDataElement() {
        return dataElement;
    }

    public void setDataElement(UIDObject dataElement) {
        this.dataElement = dataElement;
    }
}
