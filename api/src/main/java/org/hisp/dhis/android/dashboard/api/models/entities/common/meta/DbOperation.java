package org.hisp.dhis.android.dashboard.api.models.entities.common.meta;

import com.raizlabs.android.dbflow.structure.BaseModel;

import static org.hisp.dhis.android.dashboard.api.utils.Preconditions.isNull;

/**
 * This class is intended to implement partial
 * functionality of ContentProviderOperation for DbFlow.
 */
public final class DbOperation {
    private final BaseModel.Action mAction;
    private final BaseModel mModel;

    private DbOperation(BaseModel.Action action, BaseModel model) {
        mModel = isNull(model, "BaseModel object must nto be null,");
        mAction = isNull(action, "BaseModel.Action object must not be null");
    }

    public static <T extends BaseModel> DbOperation insert(T model) {
        return new DbOperation(BaseModel.Action.INSERT, model);
    }

    public static <T extends BaseModel> DbOperation update(T model) {
        return new DbOperation(BaseModel.Action.UPDATE, model);
    }

    public static <T extends BaseModel> DbOperation save(T model) {
        return new DbOperation(BaseModel.Action.SAVE, model);
    }

    public static <T extends BaseModel> DbOperation delete(T model) {
        return new DbOperation(BaseModel.Action.DELETE, model);
    }

    public BaseModel getModel() {
        return mModel;
    }

    public BaseModel.Action getAction() {
        return mAction;
    }
}
