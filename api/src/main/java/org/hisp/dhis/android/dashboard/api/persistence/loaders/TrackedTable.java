package org.hisp.dhis.android.dashboard.api.persistence.loaders;

import org.hisp.dhis.android.dashboard.api.models.common.IdentifiableObject;
import org.hisp.dhis.android.dashboard.api.models.common.meta.DbAction;

import java.util.Arrays;
import java.util.List;

public class TrackedTable {
    private final Class<? extends IdentifiableObject> mTrackedModel;
    private final List<DbAction> mActions;

    public TrackedTable(Class<? extends IdentifiableObject> trackedModel) {
        this(trackedModel, Arrays.asList(
                DbAction.INSERT,
                DbAction.UPDATE,
                DbAction.DELETE,
                DbAction.SAVE));
    }

    public TrackedTable(Class<? extends IdentifiableObject> trackedModel, DbAction action) {
        this(trackedModel, Arrays.asList(action));
    }

    public TrackedTable(Class<? extends IdentifiableObject> trackedModel, List<DbAction> actions) {
        mTrackedModel = trackedModel;
        mActions = actions;
    }

    public Class<? extends IdentifiableObject> getTrackedModel() {
        return mTrackedModel;
    }

    public List<DbAction> getActions() {
        return mActions;
    }
}