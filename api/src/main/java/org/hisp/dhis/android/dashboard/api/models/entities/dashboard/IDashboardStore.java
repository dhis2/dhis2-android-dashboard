package org.hisp.dhis.android.dashboard.api.models.entities.dashboard;

import org.hisp.dhis.android.dashboard.api.models.entities.common.IStore;
import org.hisp.dhis.android.dashboard.api.models.entities.common.meta.State;

import java.util.List;

/**
 * Created by arazabishov on 8/19/15.
 */
public interface IDashboardStore extends IStore<Dashboard> {
    List<Dashboard> query(State... states);

    List<Dashboard> query(List<State> states);
}
