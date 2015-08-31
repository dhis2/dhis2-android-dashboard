package org.hisp.dhis.android.dashboard.api.models.interpretation;

import org.hisp.dhis.android.dashboard.api.models.common.meta.State;
import org.hisp.dhis.android.dashboard.api.models.flow.Interpretation$Flow;

import java.util.List;

/**
 * Created by arazabishov on 8/27/15.
 */
public final class InterpretationStore implements IInterpretationStore {

    public InterpretationStore() {
        // empty constructor
    }

    @Override
    public void insert(Interpretation object) {
        Interpretation$Flow interpretationFlow
                = Interpretation$Flow.fromModel(object);
        interpretationFlow.insert();

        object.setId(interpretationFlow.getId());
    }

    @Override
    public void update(Interpretation object) {
        Interpretation$Flow.fromModel(object).update();
    }

    @Override
    public void save(Interpretation object) {
        Interpretation$Flow.fromModel(object).save();
    }

    @Override
    public void delete(Interpretation object) {
        Interpretation$Flow.fromModel(object).delete();
    }

    @Override
    public List<Interpretation> query() {
        return null;
    }

    @Override
    public Interpretation query(long id) {
        return null;
    }

    @Override
    public Interpretation query(String uid) {
        return null;
    }

    /*
    private static List<Interpretation> queryInterpretations() {
        return new Select()
                .from(Interpretation.class)
                .where(Condition.column(Interpretation$Table
                        .STATE).isNot(State.TO_POST.toString()))
                .queryList();
    }*/

    @Override
    public List<Interpretation> filter(State state) {
        return null;
    }
}
