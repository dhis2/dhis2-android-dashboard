package org.hisp.dhis.android.dashboard.api.api;

import org.hisp.dhis.android.dashboard.api.controllers.common.IDataController;
import org.hisp.dhis.android.dashboard.api.models.dashboard.DashboardElement;
import org.hisp.dhis.android.dashboard.api.models.dashboard.DashboardItem;
import org.hisp.dhis.android.dashboard.api.models.interpretation.IInterpretationCommentService;
import org.hisp.dhis.android.dashboard.api.models.interpretation.IInterpretationElementService;
import org.hisp.dhis.android.dashboard.api.models.interpretation.IInterpretationService;
import org.hisp.dhis.android.dashboard.api.models.interpretation.Interpretation;
import org.hisp.dhis.android.dashboard.api.models.interpretation.InterpretationComment;
import org.hisp.dhis.android.dashboard.api.models.interpretation.InterpretationElement;
import org.hisp.dhis.android.dashboard.api.models.user.User;
import org.hisp.dhis.android.dashboard.api.network.APIException;

import java.util.List;

/**
 * Created by arazabishov on 8/28/15.
 */
public final class InterpretationScope implements IDataController<Interpretation>,
        IInterpretationService, IInterpretationElementService, IInterpretationCommentService {
    private final IDataController<Interpretation> interpretationController;
    private final IInterpretationService interpretationService;
    private final IInterpretationElementService interpretationElementService;
    private final IInterpretationCommentService interpretationCommentService;

    public InterpretationScope(IDataController<Interpretation> interpretationController,
                               IInterpretationService interpretationService,
                               IInterpretationElementService interpretationElementService,
                               IInterpretationCommentService interpretationCommentService) {
        this.interpretationController = interpretationController;
        this.interpretationService = interpretationService;
        this.interpretationElementService = interpretationElementService;
        this.interpretationCommentService = interpretationCommentService;
    }

    @Override
    public void sync() throws APIException {
        interpretationController.sync();
    }

    @Override
    public void deleteComment(InterpretationComment comment) {
        interpretationCommentService.deleteComment(comment);
    }

    @Override
    public void updateCommentText(InterpretationComment comment, String text) {
        interpretationCommentService.updateCommentText(comment, text);
    }

    @Override
    public InterpretationElement createInterpretationElement(Interpretation interpretation,
                                                             DashboardElement dashboardElement, String mimeType) {
        return interpretationElementService.createInterpretationElement(interpretation, dashboardElement, mimeType);
    }

    @Override
    public InterpretationComment addComment(Interpretation interpretation, User user, String text) {
        return interpretationService.addComment(interpretation, user, text);
    }

    @Override
    public Interpretation createInterpretation(DashboardItem item, User user, String text) {
        return interpretationService.createInterpretation(item, user, text);
    }

    @Override
    public void updateInterpretationText(Interpretation interpretation, String text) {
        interpretationService.updateInterpretationText(interpretation, text);
    }

    @Override
    public void deleteInterpretation(Interpretation interpretation) {
        interpretationService.deleteInterpretation(interpretation);
    }

    @Override
    public void setInterpretationElements(Interpretation interpretation, List<InterpretationElement> elements) {
        interpretationService.setInterpretationElements(interpretation, elements);
    }

    @Override
    public List<InterpretationElement> getInterpretationElements(Interpretation interpretation) {
        return interpretationService.getInterpretationElements(interpretation);
    }
}
