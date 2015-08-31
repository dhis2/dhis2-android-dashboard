package org.hisp.dhis.android.dashboard.api.models.interpretation;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.dashboard.api.models.flow.Interpretation$Flow;
import org.hisp.dhis.android.dashboard.api.models.flow.InterpretationElement$Flow;
import org.hisp.dhis.android.dashboard.api.models.flow.InterpretationElement$Flow$Table;

import java.util.List;

/**
 * Created by arazabishov on 8/27/15.
 */
public class InterpretationElementStore implements IInterpretationElementStore {

    @Override
    public void insert(InterpretationElement object) {
        InterpretationElement$Flow elementFlow =
                InterpretationElement$Flow.fromModel(object);
        elementFlow.insert();

        object.setId(elementFlow.getId());
    }

    @Override
    public void update(InterpretationElement object) {
        InterpretationElement$Flow.fromModel(object).update();
    }

    @Override
    public void save(InterpretationElement object) {
        InterpretationElement$Flow.fromModel(object).save();
    }

    @Override
    public void delete(InterpretationElement object) {
        InterpretationElement$Flow.fromModel(object).delete();
    }

    @Override
    public List<InterpretationElement> query() {
        List<InterpretationElement$Flow> elementFlows = new Select()
                .from(InterpretationElement$Flow.class)
                .queryList();
        return InterpretationElement$Flow.toModels(elementFlows);
    }

    @Override
    public InterpretationElement query(long id) {
        InterpretationElement$Flow elementFlow = new Select()
                .from(InterpretationElement$Flow.class)
                .where(Condition.column(InterpretationElement$Flow$Table.ID).is(id))
                .querySingle();
        return InterpretationElement$Flow.toModel(elementFlow);
    }

    @Override
    public InterpretationElement query(String uid) {
        InterpretationElement$Flow elementFlow = new Select()
                .from(InterpretationElement$Flow.class)
                .where(Condition.column(InterpretationElement$Flow$Table.UID).is(uid))
                .querySingle();
        return InterpretationElement$Flow.toModel(elementFlow);
    }

    @Override
    public List<InterpretationElement> query(Interpretation interpretation) {
        Interpretation$Flow interpretationFlow = Interpretation$Flow.fromModel(interpretation);
        List<InterpretationElement$Flow> elementFlow = new Select()
                .from(InterpretationElement$Flow.class)
                .where(Condition.column(InterpretationElement$Flow$Table
                        .INTERPRETATION_INTERPRETATION).is(interpretationFlow.getId()))
                .queryList();
        return InterpretationElement$Flow.toModels(elementFlow);
    }
}
