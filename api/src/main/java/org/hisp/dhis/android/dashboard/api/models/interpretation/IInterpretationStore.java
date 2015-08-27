package org.hisp.dhis.android.dashboard.api.models.interpretation;

import org.hisp.dhis.android.dashboard.api.models.common.IStore;
import org.hisp.dhis.android.dashboard.api.models.common.meta.State;

import java.util.List;

/**
 * Created by arazabishov on 8/27/15.
 */
public interface IInterpretationStore extends IStore<Interpretation> {
    List<Interpretation> filter(State state);
}
