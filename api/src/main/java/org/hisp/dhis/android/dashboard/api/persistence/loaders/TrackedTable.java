package org.hisp.dhis.android.dashboard.api.persistence.loaders;

import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.Model;

import java.util.Arrays;
import java.util.List;

public class TrackedTable {
        private final Class<? extends Model> mTrackedModel;
        private final List<BaseModel.Action> mActions;

        public TrackedTable(Class<? extends Model> trackedModel) {
            this(trackedModel, Arrays.asList(
                    BaseModel.Action.INSERT,
                    BaseModel.Action.UPDATE,
                    BaseModel.Action.DELETE,
                    BaseModel.Action.SAVE));
        }

        public TrackedTable(Class<? extends Model> trackedModel,
                            BaseModel.Action action) {
            this(trackedModel, Arrays.asList(action));
        }

        public TrackedTable(Class<? extends Model> trackedModel,
                            List<BaseModel.Action> actions) {
            mTrackedModel = trackedModel;
            mActions = actions;
        }

        public Class<? extends Model> getTrackedModel() {
            return mTrackedModel;
        }

        public List<BaseModel.Action> getActions() {
            return mActions;
        }
    }