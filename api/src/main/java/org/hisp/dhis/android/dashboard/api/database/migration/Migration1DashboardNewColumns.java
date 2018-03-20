package org.hisp.dhis.android.dashboard.api.database.migration;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import org.hisp.dhis.android.dashboard.api.models.Dashboard;
import org.hisp.dhis.android.dashboard.api.models.meta.DbDhis;

@Migration(version = 6, databaseName = DbDhis.NAME)
public class Migration1DashboardNewColumns extends AlterTableMigration<Dashboard> {
    public Migration1DashboardNewColumns(Class<Dashboard> table) {
        super(Dashboard.class);
    }

    public Migration1DashboardNewColumns() {
        super(Dashboard.class);
    }

    @Override
    public void onPreMigrate() {
        addColumn(String.class, "publicAccess");
    }

}