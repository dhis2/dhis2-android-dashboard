package org.hisp.dhis.android.dashboard.api.models.interpretation;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.dashboard.api.models.common.meta.State;
import org.hisp.dhis.android.dashboard.api.models.flow.InterpretationComment$Flow;
import org.hisp.dhis.android.dashboard.api.models.flow.InterpretationComment$Flow$Table;

import java.util.List;

/**
 * Created by arazabishov on 8/27/15.
 */
public class InterpretationCommentStore implements IInterpretationCommentStore {

    @Override
    public void insert(InterpretationComment object) {
        InterpretationComment$Flow commentFlow =
                InterpretationComment$Flow.fromModel(object);
        commentFlow.insert();

        object.setId(commentFlow.getId());
    }

    @Override
    public void update(InterpretationComment object) {
        InterpretationComment$Flow.fromModel(object).update();
    }

    @Override
    public void save(InterpretationComment object) {
        InterpretationComment$Flow.fromModel(object).save();
    }

    @Override
    public void delete(InterpretationComment object) {
        InterpretationComment$Flow.fromModel(object).delete();
    }

    @Override
    public List<InterpretationComment> query() {
        List<InterpretationComment$Flow> commentFlows = new Select()
                .from(InterpretationComment$Flow.class)
                .queryList();
        return InterpretationComment$Flow.toModels(commentFlows);
    }

    @Override
    public InterpretationComment query(long id) {
        InterpretationComment$Flow commentFlow = new Select()
                .from(InterpretationComment$Flow.class)
                .where(Condition.column(InterpretationComment$Flow$Table.ID).is(id))
                .querySingle();
        return InterpretationComment$Flow.toModel(commentFlow);
    }

    @Override
    public InterpretationComment query(String uid) {
        InterpretationComment$Flow commentFlow = new Select()
                .from(InterpretationComment$Flow.class)
                .where(Condition.column(InterpretationComment$Flow$Table.UID).is(uid))
                .querySingle();
        return InterpretationComment$Flow.toModel(commentFlow);
    }

    @Override
    public List<InterpretationComment> filter(State state) {
        List<InterpretationComment$Flow> commentFlows = new Select()
                .from(InterpretationComment$Flow.class)
                .where(Condition.column(InterpretationComment$Flow$Table
                        .STATE).isNot(state.toString()))
                .queryList();
        return InterpretationComment$Flow.toModels(commentFlows);
    }

    @Override
    public List<InterpretationComment> filter(Interpretation interpretation, State state) {
        List<InterpretationComment$Flow> commentFlows = new Select()
                .from(InterpretationComment$Flow.class)
                .where(Condition.column(InterpretationComment$Flow$Table
                        .INTERPRETATION_INTERPRETATION).is(interpretation.getId()).isNot(state.toString()))
                .and(Condition.column(InterpretationComment$Flow$Table
                        .STATE))
                .queryList();
        return InterpretationComment$Flow.toModels(commentFlows);
    }
}
