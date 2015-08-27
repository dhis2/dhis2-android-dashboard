package org.hisp.dhis.android.dashboard.api.models.dashboard;

import org.hisp.dhis.android.dashboard.api.models.common.IStore;
import org.hisp.dhis.android.dashboard.api.models.common.meta.State;

import java.util.List;

/**
 * Created by arazabishov on 8/19/15.
 */
public interface IDashboardItemStore extends IStore<DashboardItem> {
    List<DashboardItem> query(State... states);

    List<DashboardItem> query(List<State> states);

    List<DashboardItem> query(Dashboard dashboard, List<State> states);

    List<DashboardItem> filter(State state);

    List<DashboardItem> filter(Dashboard dashboard, State state);

    List<DashboardItem> filter(Dashboard dashboard, State state, String type);
}
