package org.hisp.dhis.android.dashboard.api.models.entities.dashboard;

import org.hisp.dhis.android.dashboard.api.models.entities.common.IStore;
import org.hisp.dhis.android.dashboard.api.models.entities.common.meta.State;

import java.util.List;

/**
 * Created by arazabishov on 8/18/15.
 */
public class DashboardElementStore implements IDashboardElementStore {

    @Override
    public void insert(DashboardElement object) {

    }

    @Override
    public void update(DashboardElement object) {

    }

    @Override
    public void delete(DashboardElement object) {

    }

    @Override
    public List<DashboardElement> query() {
        return null;
    }

    @Override
    public List<DashboardElement> query(DashboardItem dashboardItem, State... states) {
        return null;
    }
}
