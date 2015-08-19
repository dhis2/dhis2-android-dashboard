package org.hisp.dhis.android.dashboard.api.models.entities.dashboard;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.dashboard.api.models.entities.common.IStore;
import org.hisp.dhis.android.dashboard.api.models.entities.flow.DashboardFlow;
import org.hisp.dhis.android.dashboard.api.models.entities.flow.DashboardFlow$Table;

import java.util.List;

/**
 * Created by arazabishov on 8/18/15.
 */
public final class DashboardStore implements IStore<Dashboard> {

    @Override
    public void insert(Dashboard object) {
        DashboardFlow dashboardFlow = DashboardFlow
                .fromModel(object);
        dashboardFlow.insert();

        object.setId(dashboardFlow.getId());
    }

    @Override
    public void update(Dashboard object) {
        DashboardFlow dashboardFlow = DashboardFlow
                .fromModel(object);
        dashboardFlow.update();
    }

    @Override
    public void delete(Dashboard object) {
        DashboardFlow dashboardFlow = new Select().from(DashboardFlow.class)
                .where(Condition.column(DashboardFlow$Table
                        .ID).is(object.getId()))
                .querySingle();

        if (dashboardFlow != null) {
            dashboardFlow.delete();
        }
    }

    @Override
    public List<Dashboard> query() {
        return null;
    }
}
