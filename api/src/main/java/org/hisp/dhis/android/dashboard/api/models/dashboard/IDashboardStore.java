package org.hisp.dhis.android.dashboard.api.models.dashboard;

import org.hisp.dhis.android.dashboard.api.models.common.IStore;
import org.hisp.dhis.android.dashboard.api.models.common.meta.State;

import java.util.List;

/**
 * Created by arazabishov on 8/19/15.
 */
public interface IDashboardStore extends IStore<Dashboard> {
    List<Dashboard> query(State... states);

    List<Dashboard> query(List<State> states);

    List<Dashboard> filter(State state);
}
