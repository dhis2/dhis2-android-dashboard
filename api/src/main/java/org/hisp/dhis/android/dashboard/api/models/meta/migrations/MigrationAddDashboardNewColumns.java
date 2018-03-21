package org.hisp.dhis.android.dashboard.api.models.meta.migrations;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import org.hisp.dhis.android.dashboard.api.models.Dashboard;
import org.hisp.dhis.android.dashboard.api.models.meta.DbDhis;

@Migration(version = 6, databaseName = DbDhis.NAME)
public class MigrationAddDashboardNewColumns extends AlterTableMigration<Dashboard> {
    public MigrationAddDashboardNewColumns(Class<Dashboard> table) {
        super(Dashboard.class);
    }

    public MigrationAddDashboardNewColumns() {
        super(Dashboard.class);
    }

    @Override
    public void onPreMigrate() {
        addColumn(String.class, "publicAccess");
    }

}