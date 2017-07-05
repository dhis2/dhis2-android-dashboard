package org.hisp.dhis.android.dashboard.api.models.meta.migrations;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import org.hisp.dhis.android.dashboard.api.models.DashboardItem;
import org.hisp.dhis.android.dashboard.api.models.meta.DbDhis;

@Migration(version = 4, databaseName = DbDhis.NAME)
public class MigrationAddOrderPosToDashboardItem extends AlterTableMigration<DashboardItem> {
    public MigrationAddOrderPosToDashboardItem(
            Class<DashboardItem> table) {
        super(table);
    }

    @Override
    public void onPreMigrate() {
        addColumn(Integer.class, "orderPosition");
    }
}
