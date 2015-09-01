package org.hisp.dhis.android.dashboard.api.models.common;

import com.raizlabs.android.dbflow.sql.language.Delete;

import org.hisp.dhis.android.dashboard.api.models.flow.Dashboard$Flow;
import org.hisp.dhis.android.dashboard.api.models.flow.DashboardElement$Flow;
import org.hisp.dhis.android.dashboard.api.models.flow.DashboardItem$Flow;
import org.hisp.dhis.android.dashboard.api.models.flow.DashboardItemContent$Flow;
import org.hisp.dhis.android.dashboard.api.models.flow.Interpretation$Flow;
import org.hisp.dhis.android.dashboard.api.models.flow.InterpretationComment$Flow;
import org.hisp.dhis.android.dashboard.api.models.flow.InterpretationElement$Flow;
import org.hisp.dhis.android.dashboard.api.models.flow.User$Flow;
import org.hisp.dhis.android.dashboard.api.models.flow.UserAccount$Flow;

/**
 * Created by arazabishov on 8/28/15.
 */
public class ModelsStore implements IModelsStore {

    public ModelsStore() {
        // empty constructor
    }

    @Override
    public void deleteAllTables() {
        Delete.tables(
                Dashboard$Flow.class,
                DashboardItem$Flow.class,
                DashboardElement$Flow.class,
                DashboardItemContent$Flow.class,
                Interpretation$Flow.class,
                InterpretationComment$Flow.class,
                InterpretationElement$Flow.class,
                UserAccount$Flow.class,
                User$Flow.class
        );
    }
}
