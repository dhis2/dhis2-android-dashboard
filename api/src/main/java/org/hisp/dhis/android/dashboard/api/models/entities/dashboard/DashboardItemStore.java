package org.hisp.dhis.android.dashboard.api.models.entities.dashboard;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.dashboard.api.models.entities.common.meta.State;
import org.hisp.dhis.android.dashboard.api.models.entities.flow.DashboardItemFlow;
import org.hisp.dhis.android.dashboard.api.models.entities.flow.DashboardItemFlow$Table;

import java.util.Arrays;
import java.util.List;

/**
 * Created by arazabishov on 8/18/15.
 */
public class DashboardItemStore implements IDashboardItemStore {

    @Override
    public void insert(DashboardItem object) {
        DashboardItemFlow dashboardItemFlow
                = DashboardItemFlow.fromModel(object);
        dashboardItemFlow.insert();

        object.setId(dashboardItemFlow.getId());
    }

    @Override
    public void update(DashboardItem object) {
        DashboardItemFlow.fromModel(object).update();
    }

    @Override
    public void delete(DashboardItem object) {
        DashboardItemFlow.fromModel(object).delete();
    }

    @Override
    public List<DashboardItem> query() {
        List<DashboardItemFlow> dashboardItemFlows = new Select()
                .from(DashboardItemFlow.class)
                .queryList();
        return DashboardItemFlow.toModels(dashboardItemFlows);
    }

    @Override
    public List<DashboardItem> query(State... states) {
        return query(Arrays.asList(states));
    }

    @Override
    public List<DashboardItem> query(List<State> states) {
        if (states == null || states.isEmpty()) {
            throw new IllegalArgumentException("Please, provide at least one State");
        }

        Condition.CombinedCondition combinedCondition = buildCombinedCondition(states);
        List<DashboardItemFlow> dashboardItemFlows = new Select()
                .from(DashboardItemFlow.class)
                .where(combinedCondition)
                .queryList();

        return DashboardItemFlow.toModels(dashboardItemFlows);
    }

    @Override
    public List<DashboardItem> query(Dashboard dashboard, List<State> states) {
        if (states == null || states.isEmpty()) {
            throw new IllegalArgumentException("Please, provide at least one State");
        }

        Condition.CombinedCondition combinedCondition = buildCombinedCondition(states);
        combinedCondition = combinedCondition.and(Condition.column(DashboardItemFlow$Table
                .DASHBOARD_DASHBOARD).is(dashboard.getId()));
        List<DashboardItemFlow> dashboardItemFlows = new Select()
                .from(DashboardItemFlow.class)
                .where(combinedCondition)
                .queryList();

        return DashboardItemFlow.toModels(dashboardItemFlows);
    }

    @Override
    public List<DashboardItem> filter(Dashboard dashboard, State state) {
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
        return Condition.column(DashboardItemFlow$Table
                .STATE).is(state.toString());
    }
}
