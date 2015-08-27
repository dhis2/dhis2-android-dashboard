package org.hisp.dhis.android.dashboard.api.models.dashboard;

import org.hisp.dhis.android.dashboard.api.models.common.IStore;
import org.hisp.dhis.android.dashboard.api.models.common.meta.State;

import java.util.List;

/**
 * Created by arazabishov on 8/19/15.
 */
public interface IDashboardElementStore extends IStore<DashboardElement> {
    List<DashboardElement> query(DashboardItem dashboardItem, State... states);

    List<DashboardElement> query(DashboardItem dashboardItem, List<State> states);

    List<DashboardElement> filter(DashboardItem dashboardItem, State state);

    List<DashboardElement> filter(State state);
}
