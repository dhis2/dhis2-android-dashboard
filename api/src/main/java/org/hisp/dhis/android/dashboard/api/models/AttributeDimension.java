package org.hisp.dhis.android.dashboard.api.models;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.hisp.dhis.android.dashboard.api.models.meta.DbDhis;

@Table(databaseName = DbDhis.NAME)
public class AttributeDimension extends BaseModel {

    @Column(name = "id")
    @PrimaryKey(autoincrement = true)
    long id;

    UIDObject attribute;

    public UIDObject getAttribute() {
        return attribute;
    }

    public void setAttribute(UIDObject attribute) {
        this.attribute = attribute;
    }
}
