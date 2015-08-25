package org.hisp.dhis.android.dashboard.api.models.entities.dashboard;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.builder.Condition.CombinedCondition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.dashboard.api.models.entities.common.meta.State;
import org.hisp.dhis.android.dashboard.api.models.entities.flow.DashboardFlow;
import org.hisp.dhis.android.dashboard.api.models.entities.flow.DashboardFlow$Table;

import java.util.Arrays;
import java.util.List;

/**
 * Created by arazabishov on 8/18/15.
 */
public final class DashboardStore implements IDashboardStore {

    @Override
    public void insert(Dashboard object) {
        DashboardFlow dashboardFlow = DashboardFlow
                .fromModel(object);
        dashboardFlow.insert();

        object.setId(dashboardFlow.getId());
    }

    @Override
    public void update(Dashboard object) {
        DashboardFlow dashboardFlow = DashboardFlow
                .fromModel(object);
        dashboardFlow.update();
    }

    @Override
    public void delete(Dashboard object) {
        DashboardFlow dashboardFlow = new Select().from(DashboardFlow.class)
                .where(Condition.column(DashboardFlow$Table
                        .ID).is(object.getId()))
                .querySingle();

        if (dashboardFlow != null) {
            dashboardFlow.delete();
        }
    }

    @Override
    public List<Dashboard> query() {
        return null;
    }

    @Override
    public List<Dashboard> query(State... states) {
        return query(Arrays.asList(states));
    }

    @Override
    public List<Dashboard> query(List<State> states) {
        if (states == null || states.isEmpty()) {
            throw new IllegalArgumentException("Please, provide at least one State");
        }

        CombinedCondition combinedCondition = null;
        for (State state : states) {
            if (combinedCondition == null) {
                combinedCondition = CombinedCondition.begin(isState(state));
            } else {
                combinedCondition = combinedCondition.or(isState(state));
            }
        }

        List<DashboardFlow> dashboardFlows = new Select()
                .from(DashboardFlow.class)
                .where(combinedCondition)
                .queryList();

        // converting flow models to Dashboard
        return DashboardFlow.toModels(dashboardFlows);
    }

    @Override
    public List<Dashboard> filter(State state) {
        if (state == null) {
            throw new IllegalArgumentException("Please, provide State");
        }

        List<DashboardFlow> dashboardFlows = new Select()
                .from(DashboardFlow.class)
                .where(Condition.column(DashboardFlow$Table
                        .STATE).isNot(state.toString()))
                .queryList();

        // converting flow models to Dashboard
        return DashboardFlow.toModels(dashboardFlows);
    }

    private static Condition isState(State state) {
        return Condition.column(DashboardFlow$Table
                .STATE).is(state.toString());
    }
}
