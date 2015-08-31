package org.hisp.dhis.android.dashboard.api.models.dashboard;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.builder.Condition.CombinedCondition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.dashboard.api.models.common.meta.State;
import org.hisp.dhis.android.dashboard.api.models.flow.Dashboard$Flow;
import org.hisp.dhis.android.dashboard.api.models.flow.Dashboard$Flow$Table;

import java.util.Arrays;
import java.util.List;

/**
 * Created by arazabishov on 8/18/15.
 */
public final class DashboardStore implements IDashboardStore {

    @Override
    public void insert(Dashboard object) {
        Dashboard$Flow dashboardFlow
                = Dashboard$Flow.fromModel(object);
        dashboardFlow.insert();

        object.setId(dashboardFlow.getId());
    }

    @Override
    public void update(Dashboard object) {
        Dashboard$Flow dashboardFlow
                = Dashboard$Flow.fromModel(object);
        dashboardFlow.update();
    }

    @Override
    public void save(Dashboard object) {
        Dashboard$Flow dashboardFlow
                = Dashboard$Flow.fromModel(object);
        dashboardFlow.save();

        object.setId(dashboardFlow.getId());
    }

    @Override
    public void delete(Dashboard object) {
        Dashboard$Flow dashboardFlow = new Select().from(Dashboard$Flow.class)
                .where(Condition.column(Dashboard$Flow$Table
                        .ID).is(object.getId()))
                .querySingle();

        if (dashboardFlow != null) {
            dashboardFlow.delete();
        }
    }

    @Override
    public List<Dashboard> query() {
        List<Dashboard$Flow> dashboardFlows =
                new Select().from(Dashboard$Flow.class).queryList();
        return Dashboard$Flow.toModels(dashboardFlows);
    }

    @Override
    public Dashboard query(long id) {
        Dashboard$Flow dashboardFlow = new Select()
                .from(Dashboard$Flow.class)
                .where(Condition.column(Dashboard$Flow$Table
                        .ID).is(id))
                .querySingle();
        return Dashboard$Flow.toModel(dashboardFlow);
    }

    @Override
    public Dashboard query(String uid) {
        Dashboard$Flow dashboardFlow = new Select()
                .from(Dashboard$Flow.class)
                .where(Condition.column(Dashboard$Flow$Table
                        .UID).is(uid))
                .querySingle();
        return Dashboard$Flow.toModel(dashboardFlow);
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

        List<Dashboard$Flow> dashboardFlows = new Select()
                .from(Dashboard$Flow.class)
                .where(combinedCondition)
                .queryList();

        // converting flow models to Dashboard
        return Dashboard$Flow.toModels(dashboardFlows);
    }

    @Override
    public List<Dashboard> filter(State state) {
        if (state == null) {
            throw new IllegalArgumentException("Please, provide State");
        }

        List<Dashboard$Flow> dashboardFlows = new Select()
                .from(Dashboard$Flow.class)
                .where(Condition.column(Dashboard$Flow$Table
                        .STATE).isNot(state.toString()))
                .queryList();

        // converting flow models to Dashboard
        return Dashboard$Flow.toModels(dashboardFlows);
    }

    private static Condition isState(State state) {
        return Condition.column(Dashboard$Flow$Table
                .STATE).is(state.toString());
    }
}
