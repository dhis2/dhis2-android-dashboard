package org.hisp.dhis.android.dashboard.api.models.dashboard;

import com.raizlabs.android.dbflow.sql.builder.Condition.CombinedCondition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.dashboard.api.models.flow.DashboardItemContent$Flow;
import org.hisp.dhis.android.dashboard.api.models.flow.DashboardItemContent$Flow$Table;

import java.util.List;

import static com.raizlabs.android.dbflow.sql.builder.Condition.column;

/**
 * Created by arazabishov on 8/26/15.
 */
public final class DashboardItemContentStore implements IDashboardItemContentStore {

    public DashboardItemContentStore() {
        // empty constructor
    }

    @Override
    public void insert(DashboardItemContent object) {
        DashboardItemContent$Flow flowModel
                = DashboardItemContent$Flow.fromModel(object);
        flowModel.insert();

        object.setId(flowModel.getId());
    }

    @Override
    public void update(DashboardItemContent object) {
        DashboardItemContent$Flow.fromModel(object).update();
    }

    @Override
    public void save(DashboardItemContent object) {
        DashboardItemContent$Flow flowModel
                = DashboardItemContent$Flow.fromModel(object);
        flowModel.save();

        object.setId(flowModel.getId());
    }

    @Override
    public void delete(DashboardItemContent object) {
        DashboardItemContent$Flow.fromModel(object).delete();
    }

    @Override
    public List<DashboardItemContent> query() {
        List<DashboardItemContent$Flow> flows = new Select()
                .from(DashboardItemContent$Flow.class)
                .queryList();
        return DashboardItemContent$Flow.toModels(flows);
    }

    @Override
    public DashboardItemContent query(long id) {
        DashboardItemContent$Flow dashboardItemContentFlow = new Select()
                .from(DashboardItemContent$Flow.class)
                .where(column(DashboardItemContent$Flow$Table
                        .ID).is(id))
                .querySingle();
        return DashboardItemContent$Flow.toModel(dashboardItemContentFlow);
    }

    @Override
    public DashboardItemContent query(String uid) {
        DashboardItemContent$Flow dashboardItemContentFlow = new Select()
                .from(DashboardItemContent$Flow.class)
                .where(column(DashboardItemContent$Flow$Table
                        .UID).is(uid))
                .querySingle();
        return DashboardItemContent$Flow.toModel(dashboardItemContentFlow);
    }

    @Override
    public List<DashboardItemContent> query(List<String> types) {
        CombinedCondition generalCondition = CombinedCondition.begin(
                column(DashboardItemContent$Flow$Table.TYPE).isNotNull());
        CombinedCondition columnConditions = null;
        for (String type : types) {
            if (columnConditions == null) {
                columnConditions = CombinedCondition
                        .begin(column(DashboardItemContent$Flow$Table.TYPE).is(type));
            } else {
                columnConditions = columnConditions
                        .or(column(DashboardItemContent$Flow$Table.TYPE).is(type));
            }
        }
        generalCondition.and(columnConditions);

        List<DashboardItemContent$Flow> resources = new Select()
                .from(DashboardItemContent$Flow.class)
                .where(generalCondition)
                .queryList();
        return DashboardItemContent$Flow.toModels(resources);
    }
}
