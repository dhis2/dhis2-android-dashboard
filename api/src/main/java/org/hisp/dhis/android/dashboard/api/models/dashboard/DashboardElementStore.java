package org.hisp.dhis.android.dashboard.api.models.dashboard;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.dashboard.api.models.common.meta.State;
import org.hisp.dhis.android.dashboard.api.models.flow.DashboardElement$Flow;
import org.hisp.dhis.android.dashboard.api.models.flow.DashboardElement$Flow$Table;

import java.util.Arrays;
import java.util.List;

/**
 * Created by arazabishov on 8/18/15.
 */
public class DashboardElementStore implements IDashboardElementStore {

    @Override
    public void insert(DashboardElement object) {
        DashboardElement$Flow elementFlow = DashboardElement$Flow
                .fromModel(object);
        elementFlow.insert();

        object.setId(elementFlow.getId());
    }

    @Override
    public void update(DashboardElement object) {
        DashboardElement$Flow.fromModel(object).update();
    }

    @Override
    public void save(DashboardElement object) {
        DashboardElement$Flow elementFlow
                = DashboardElement$Flow.fromModel(object);
        elementFlow.save();

        object.setId(elementFlow.getId());
    }

    @Override
    public void delete(DashboardElement object) {
        DashboardElement$Flow.fromModel(object).delete();
    }

    @Override
    public List<DashboardElement> query() {
        List<DashboardElement$Flow> elementFlows = new Select()
                .from(DashboardElement$Flow.class)
                .queryList();
        return DashboardElement$Flow.toModels(elementFlows);
    }

    @Override
    public DashboardElement query(long id) {
        DashboardElement$Flow dashboardElementFlow = new Select()
                .from(DashboardElement$Flow.class)
                .where(Condition.column(DashboardElement$Flow$Table.ID).is(id))
                .querySingle();
        return DashboardElement$Flow.toModel(dashboardElementFlow);
    }

    @Override
    public DashboardElement query(String uid) {
        DashboardElement$Flow dashboardElementFlow = new Select()
                .from(DashboardElement$Flow.class)
                .where(Condition.column(DashboardElement$Flow$Table.UID).is(uid))
                .querySingle();
        return DashboardElement$Flow.toModel(dashboardElementFlow);
    }

    @Override
    public List<DashboardElement> query(DashboardItem dashboardItem, State... states) {
        return query(dashboardItem, Arrays.asList(states));
    }

    @Override
    public List<DashboardElement> query(DashboardItem dashboardItem, List<State> states) {
        if (states != null && states.isEmpty()) {
            throw new IllegalArgumentException("Please, provide at least one State");
        }

        Condition.CombinedCondition combinedCondition = buildCombinedCondition(states);
        combinedCondition = combinedCondition.and(Condition
                .column(DashboardElement$Flow$Table
                        .DASHBOARDITEM_DASHBOARDITEM).is(dashboardItem.getId()));

        List<DashboardElement$Flow> elementFlows = new Select()
                .from(DashboardElement$Flow.class)
                .where(combinedCondition)
                .queryList();

        // converting flow models to Dashboard
        return DashboardElement$Flow.toModels(elementFlows);
    }

    @Override
    public List<DashboardElement> filter(DashboardItem dashboardItem, State state) {
        if (state == null) {
            throw new IllegalArgumentException("Please, provide State");
        }

        List<DashboardElement$Flow> elementFlows = new Select()
                .from(DashboardElement$Flow.class)
                .where(Condition.CombinedCondition
                        .begin(Condition.column(DashboardElement$Flow$Table
                                .STATE).isNot(state.toString()))
                        .and(Condition.column(DashboardElement$Flow$Table
                                .DASHBOARDITEM_DASHBOARDITEM).is(dashboardItem.getId())))
                .queryList();

        // converting flow models to Dashboard
        return DashboardElement$Flow.toModels(elementFlows);
    }

    @Override
    public List<DashboardElement> filter(State state) {
        if (state == null) {
            throw new IllegalArgumentException("Please, provide State");
        }

        List<DashboardElement$Flow> elementFlows = new Select()
                .from(DashboardElement$Flow.class)
                .where(Condition.column(DashboardElement$Flow$Table
                        .STATE).isNot(state.toString()))
                .queryList();

        // converting flow models to Dashboard
        return DashboardElement$Flow.toModels(elementFlows);
    }

    private static Condition.CombinedCondition buildCombinedCondition(List<State> states) {
        Condition.CombinedCondition combinedCondition = null;
        for (State state : states) {
            if (combinedCondition == null) {
                combinedCondition = Condition.CombinedCondition.begin(isState(state));
            } else {
                combinedCondition = combinedCondition.or(isState(state));
            }
        }
        return combinedCondition;
    }

    private static Condition isState(State state) {
        return Condition.column(DashboardElement$Flow$Table
                .STATE).is(state.toString());
    }
}
