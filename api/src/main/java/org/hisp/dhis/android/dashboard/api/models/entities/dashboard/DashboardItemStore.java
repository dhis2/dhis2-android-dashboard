package org.hisp.dhis.android.dashboard.api.models.entities.dashboard;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.dashboard.api.models.entities.common.meta.State;
import org.hisp.dhis.android.dashboard.api.models.entities.flow.DashboardItem$Flow;
import org.hisp.dhis.android.dashboard.api.models.entities.flow.DashboardItem$Flow$Table;

import java.util.Arrays;
import java.util.List;

/**
 * Created by arazabishov on 8/18/15.
 */
public class DashboardItemStore implements IDashboardItemStore {

    @Override
    public void insert(DashboardItem object) {
        DashboardItem$Flow dashboardItemFlow
                = DashboardItem$Flow.fromModel(object);
        dashboardItemFlow.insert();

        object.setId(dashboardItemFlow.getId());
    }

    @Override
    public void update(DashboardItem object) {
        DashboardItem$Flow.fromModel(object).update();
    }

    @Override
    public void delete(DashboardItem object) {
        DashboardItem$Flow.fromModel(object).delete();
    }

    @Override
    public List<DashboardItem> query() {
        List<DashboardItem$Flow> dashboardItemFlows = new Select()
                .from(DashboardItem$Flow.class)
                .queryList();
        return DashboardItem$Flow.toModels(dashboardItemFlows);
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
        List<DashboardItem$Flow> dashboardItemFlows = new Select()
                .from(DashboardItem$Flow.class)
                .where(combinedCondition)
                .queryList();

        return DashboardItem$Flow.toModels(dashboardItemFlows);
    }

    @Override
    public List<DashboardItem> query(Dashboard dashboard, List<State> states) {
        if (states == null || states.isEmpty()) {
            throw new IllegalArgumentException("Please, provide at least one State");
        }

        Condition.CombinedCondition combinedCondition = buildCombinedCondition(states);
        combinedCondition = combinedCondition.and(Condition.column(DashboardItem$Flow$Table
                .DASHBOARD_DASHBOARD).is(dashboard.getId()));
        List<DashboardItem$Flow> dashboardItemFlows = new Select()
                .from(DashboardItem$Flow.class)
                .where(combinedCondition)
                .queryList();

        return DashboardItem$Flow.toModels(dashboardItemFlows);
    }

    @Override
    public List<DashboardItem> filter(State state) {
        if (state == null) {
            throw new IllegalArgumentException("Please, provide State");
        }

        List<DashboardItem$Flow> dashboardItemFlows = new Select()
                .from(DashboardItem$Flow.class)
                .where(Condition.column(DashboardItem$Flow$Table
                        .STATE).isNot(state.toString()))
                .queryList();

        return DashboardItem$Flow.toModels(dashboardItemFlows);
    }

    @Override
    public List<DashboardItem> filter(Dashboard dashboard, State state) {
        if (state == null) {
            throw new IllegalArgumentException("Please, provide State");
        }

        List<DashboardItem$Flow> dashboardItemFlows = new Select()
                .from(DashboardItem$Flow.class)
                .where(Condition.CombinedCondition
                        .begin(Condition.column(DashboardItem$Flow$Table
                                .STATE).isNot(state.toString()))
                        .and(Condition.column(DashboardItem$Flow$Table
                                .DASHBOARD_DASHBOARD).is(dashboard.getId())))
                .queryList();

        return DashboardItem$Flow.toModels(dashboardItemFlows);
    }

    @Override
    public List<DashboardItem> filter(Dashboard dashboard, State state, String type) {
        if (state == null) {
            throw new IllegalArgumentException("Please, provide State");
        }

        List<DashboardItem$Flow> dashboardItemFlows = new Select()
                .from(DashboardItem$Flow.class)
                .where(Condition.CombinedCondition
                        .begin(Condition.column(DashboardItem$Flow$Table
                                .STATE).isNot(state.toString()))
                        .and(Condition.column(DashboardItem$Flow$Table
                                .DASHBOARD_DASHBOARD).is(dashboard.getId()))
                        .and(Condition.column(DashboardItem$Flow$Table.TYPE).isNot(type)))
                .queryList();

        return DashboardItem$Flow.toModels(dashboardItemFlows);
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
        return Condition.column(DashboardItem$Flow$Table
                .STATE).is(state.toString());
    }
}
