package org.hisp.dhis.android.dashboard.api.models.entities.dashboard;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.dashboard.api.models.entities.common.meta.State;
import org.hisp.dhis.android.dashboard.api.models.entities.flow.DashboardElementFlow;
import org.hisp.dhis.android.dashboard.api.models.entities.flow.DashboardElementFlow$Table;

import java.util.Arrays;
import java.util.List;

/**
 * Created by arazabishov on 8/18/15.
 */
public class DashboardElementStore implements IDashboardElementStore {

    @Override
    public void insert(DashboardElement object) {
        DashboardElementFlow elementFlow = DashboardElementFlow
                .fromModel(object);
        elementFlow.insert();

        object.setId(elementFlow.getId());
    }

    @Override
    public void update(DashboardElement object) {
        DashboardElementFlow.fromModel(object).update();
    }

    @Override
    public void delete(DashboardElement object) {
        DashboardElementFlow.fromModel(object).delete();
    }

    @Override
    public List<DashboardElement> query() {
        List<DashboardElementFlow> elementFlows = new Select()
                .from(DashboardElementFlow.class)
                .queryList();
        return DashboardElementFlow.toModels(elementFlows);
    }

    @Override
    public List<DashboardElement> query(DashboardItem dashboardItem, List<State> states) {
        if (states != null && states.isEmpty()) {
            throw new IllegalArgumentException("Please, provide at least one State");
        }

        Condition.CombinedCondition combinedCondition = buildCombinedCondition(states);
        combinedCondition = combinedCondition.and(Condition
                .column(DashboardElementFlow$Table
                        .DASHBOARDITEM_DASHBOARDITEM).is(dashboardItem.getId()));

        List<DashboardElementFlow> elementFlows = new Select()
                .from(DashboardElementFlow.class)
                .where(combinedCondition)
                .queryList();

        // converting flow models to Dashboard
        return DashboardElementFlow.toModels(elementFlows);
    }

    @Override
    public List<DashboardElement> filter(DashboardItem dashboardItem, State state) {
        return null;
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
        return Condition.column(DashboardElementFlow$Table
                .STATE).is(state.toString());
    }
}
