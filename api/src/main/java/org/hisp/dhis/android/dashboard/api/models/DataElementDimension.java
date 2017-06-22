package org.hisp.dhis.android.dashboard.api.models;

import com.raizlabs.android.dbflow.annotation.Table;

import org.hisp.dhis.android.dashboard.api.models.meta.DbDhis;

@Table(databaseName = DbDhis.NAME)
public class DataElementDimension {

    UIDObject dataElement;

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
