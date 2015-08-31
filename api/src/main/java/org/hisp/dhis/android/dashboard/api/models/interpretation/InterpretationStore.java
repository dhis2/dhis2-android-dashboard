package org.hisp.dhis.android.dashboard.api.models.interpretation;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.dashboard.api.models.common.meta.State;
import org.hisp.dhis.android.dashboard.api.models.flow.Interpretation$Flow;
import org.hisp.dhis.android.dashboard.api.models.flow.Interpretation$Flow$Table;

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
        List<Interpretation$Flow> interpretationFlows = new Select()
                .from(Interpretation$Flow.class)
                .queryList();
        return Interpretation$Flow.toModels(interpretationFlows);
    }

    @Override
    public Interpretation query(long id) {
        Interpretation$Flow interpretationFlow = new Select()
                .from(Interpretation$Flow.class)
                .where(Condition.column(Interpretation$Flow$Table.ID).is(id))
                .querySingle();
        return Interpretation$Flow.toModel(interpretationFlow);
    }

    @Override
    public Interpretation query(String uid) {
        Interpretation$Flow interpretationFlow = new Select()
                .from(Interpretation$Flow.class)
                .where(Condition.column(Interpretation$Flow$Table.UID).is(uid))
                .querySingle();
        return Interpretation$Flow.toModel(interpretationFlow);
    }

    @Override
    public List<Interpretation> filter(State state) {
        List<Interpretation$Flow> interpretationFlows = new Select()
                .from(Interpretation$Flow.class)
                .where(Condition.column(Interpretation$Flow$Table
                        .STATE).isNot(state.toString()))
                .queryList();
        return Interpretation$Flow.toModels(interpretationFlows);
    }
}
