package org.hisp.dhis.android.dashboard.api.models.interpretation;

import org.hisp.dhis.android.dashboard.api.models.interpretation.InterpretationComment;
import org.hisp.dhis.android.dashboard.api.models.common.IService;

/**
 * Created by arazabishov on 8/27/15.
 */
public interface IInterpretationCommentService extends IService {
    void deleteComment(InterpretationComment comment);

    void updateCommentText(InterpretationComment comment, String text);
}
