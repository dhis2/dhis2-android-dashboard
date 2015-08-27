package org.hisp.dhis.android.dashboard.api.services.interpretations;

import org.hisp.dhis.android.dashboard.api.models.common.Access;
import org.hisp.dhis.android.dashboard.api.models.common.meta.State;
import org.hisp.dhis.android.dashboard.api.models.dashboard.DashboardItem;
import org.hisp.dhis.android.dashboard.api.models.interpretation.Interpretation;
import org.hisp.dhis.android.dashboard.api.models.interpretation.InterpretationComment;
import org.hisp.dhis.android.dashboard.api.models.interpretation.InterpretationElement;
import org.hisp.dhis.android.dashboard.api.models.user.User;
import org.hisp.dhis.android.dashboard.api.persistence.preferences.DateTimeManager;
import org.hisp.dhis.android.dashboard.api.persistence.preferences.ResourceType;
import org.joda.time.DateTime;

/**
 * Created by arazabishov on 8/27/15.
 */
public final class InterpretationService implements IInterpretationsService {

    public InterpretationService() {
        // empty constructor
    }

    /**
     * Creates comment for given interpretation. Comment is assigned to given user.
     *
     * @param interpretation Interpretation to associate comment with.
     * @param user           User who wants to create comment.
     * @param text           The actual content of comment.
     * @return Intrepretation comment.
     */
    @Override
    public InterpretationComment addComment(Interpretation interpretation, User user, String text) {
        DateTime lastUpdated = DateTimeManager.getInstance()
                .getLastUpdated(ResourceType.INTERPRETATIONS);

        InterpretationComment comment = new InterpretationComment();
        comment.setCreated(lastUpdated);
        comment.setLastUpdated(lastUpdated);
        comment.setAccess(Access.provideDefaultAccess());
        comment.setText(text);
        comment.setState(State.TO_POST);
        comment.setUser(user);
        comment.setInterpretation(interpretation);
        return comment;
    }

    /**
     * This method allows to create interpretation from: chart, map,
     * reportTable. Please note, it won't work for data sets.
     * <p>
     * Note, model won't be saved to database automatically. You have to call .save()
     * both on interpretation and interpretation elements of current object.
     *
     * @param item DashboardItem which will represent content of interpretation.
     * @param user User who associated with Interpretation.
     * @param text Interpretation text written by user.
     * @return new Interpretation.
     */
    @Override
    public Interpretation createInterpretation(DashboardItem item, User user, String text) {
        DateTime lastUpdated = DateTimeManager.getInstance()
                .getLastUpdated(ResourceType.INTERPRETATIONS);

        Interpretation interpretation = new Interpretation();
        interpretation.setCreated(lastUpdated);
        interpretation.setLastUpdated(lastUpdated);
        interpretation.setAccess(Access.provideDefaultAccess());
        interpretation.setText(text);
        interpretation.setState(State.TO_POST);
        interpretation.setUser(user);

        switch (item.getType()) {
            case Interpretation.TYPE_CHART: {
                InterpretationElement element = InterpretationElement
                        .fromDashboardElement(interpretation, item.getChart(), Interpretation.TYPE_CHART);
                interpretation.setType(Interpretation.TYPE_CHART);
                interpretation.setChart(element);
                break;
            }
            case Interpretation.TYPE_MAP: {
                InterpretationElement element = InterpretationElement
                        .fromDashboardElement(interpretation, item.getMap(), Interpretation.TYPE_MAP);
                interpretation.setType(Interpretation.TYPE_MAP);
                interpretation.setMap(element);
                break;
            }
            case Interpretation.TYPE_REPORT_TABLE: {
                InterpretationElement element = InterpretationElement
                        .fromDashboardElement(interpretation, item.getReportTable(), Interpretation.TYPE_REPORT_TABLE);
                interpretation.setType(Interpretation.TYPE_REPORT_TABLE);
                interpretation.setReportTable(element);
                break;
            }
            default: {
                throw new IllegalArgumentException("Unsupported DashboardItem type");
            }
        }

        return interpretation;
    }

    @Override
    public void updateInterpretationText(Interpretation interpretation, String text) {
        interpretation.setText(text);

        if (interpretation.getState() != State.TO_DELETE &&
                interpretation.getState() != State.TO_POST) {
            interpretation.setState(State.TO_UPDATE);
        }

        // super.save();
    }

    @Override
    public void deleteInterpretation(Interpretation interpretation) {
        if (State.TO_POST.equals(interpretation.getState())) {
            // super.delete();
        } else {
            interpretation.setState(State.TO_DELETE);
            // super.save();
        }
    }
}
