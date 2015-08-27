package org.hisp.dhis.android.dashboard.api.services.interpretations;

import org.hisp.dhis.android.dashboard.api.models.dashboard.DashboardItem;
import org.hisp.dhis.android.dashboard.api.models.interpretation.Interpretation;
import org.hisp.dhis.android.dashboard.api.models.interpretation.InterpretationComment;
import org.hisp.dhis.android.dashboard.api.models.user.User;
import org.hisp.dhis.android.dashboard.api.services.common.IService;

/**
 * Created by arazabishov on 8/27/15.
 */
public interface IInterpretationsService extends IService {
    InterpretationComment addComment(Interpretation interpretation, User user, String text);

    Interpretation createInterpretation(DashboardItem item, User user, String text);

    void updateInterpretationText(Interpretation interpretation, String text);

    void deleteInterpretation(Interpretation interpretation);
}
