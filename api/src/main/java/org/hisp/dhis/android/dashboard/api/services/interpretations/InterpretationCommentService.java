package org.hisp.dhis.android.dashboard.api.services.interpretations;

import org.hisp.dhis.android.dashboard.api.models.Models;
import org.hisp.dhis.android.dashboard.api.models.common.meta.State;
import org.hisp.dhis.android.dashboard.api.models.interpretation.InterpretationComment;

/**
 * Created by arazabishov on 8/27/15.
 */
public final class InterpretationCommentService implements IInterpretationCommentService {

    public InterpretationCommentService() {
        // empty constructor
    }

    /**
     * Performs soft delete of model. If State of object was SYNCED, it will be set to TO_DELETE.
     * If the model is persisted only in the local database, it will be removed immediately.
     */
    @Override
    public void deleteComment(InterpretationComment comment) {
        if (State.TO_POST.equals(comment.getState())) {
            Models.interpretationComments().delete(comment);
        } else {
            comment.setState(State.TO_DELETE);
            Models.interpretationComments().save(comment);
        }
    }

    /**
     * Method modifies the original comment text and sets TO_UPDATE as state,
     * if the object was received from server. If the model was persisted only locally,
     * the State will be the TO_POST.
     *
     * @param text Edited text of comment.
     */
    @Override
    public void updateCommentText(InterpretationComment comment, String text) {
        comment.setText(text);

        if (comment.getState() != State.TO_DELETE &&
                comment.getState() != State.TO_POST) {
            comment.setState(State.TO_UPDATE);
        }

        Models.interpretationComments().save(comment);
    }
}
