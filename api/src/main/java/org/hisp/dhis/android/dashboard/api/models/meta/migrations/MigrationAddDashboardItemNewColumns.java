package org.hisp.dhis.android.dashboard.api.models.meta.migrations;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import org.hisp.dhis.android.dashboard.api.models.Dashboard;
import org.hisp.dhis.android.dashboard.api.models.DashboardItem;
import org.hisp.dhis.android.dashboard.api.models.meta.DbDhis;

@Migration(version = 6, databaseName = DbDhis.NAME)
public class MigrationAddDashboardItemNewColumns extends AlterTableMigration<DashboardItem> {
    public MigrationAddDashboardItemNewColumns(Class<DashboardItem> table) {
        super(DashboardItem.class);
    }

    public MigrationAddDashboardItemNewColumns() {
        super(DashboardItem.class);
    }

    @Override
    public void onPreMigrate() {
        addColumn(Integer.class, "x");
        addColumn(Integer.class, "y");
        addColumn(Integer.class, "w");
        addColumn(Integer.class, "h");
        addColumn(Integer.class, "originalHeight");
        addColumn(Integer.class, "width");
        addColumn(Integer.class, "height");
        addColumn(Boolean.class, "favorite");
    }

}