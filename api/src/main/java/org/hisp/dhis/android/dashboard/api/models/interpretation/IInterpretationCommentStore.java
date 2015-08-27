package org.hisp.dhis.android.dashboard.api.models.interpretation;

import org.hisp.dhis.android.dashboard.api.models.common.IStore;
import org.hisp.dhis.android.dashboard.api.models.common.meta.State;

import java.util.List;

/**
 * Created by arazabishov on 8/27/15.
 */
public interface IInterpretationCommentStore extends IStore<InterpretationComment> {
    List<InterpretationComment> filter(State state);

    List<InterpretationComment> filter(Interpretation interpretation, State state);
}
