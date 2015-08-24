package org.hisp.dhis.android.dashboard.api.models.entities.common.meta;

import org.hisp.dhis.android.dashboard.api.models.entities.common.IStore;
import org.hisp.dhis.android.dashboard.api.models.entities.common.IdentifiableObject;

import static org.hisp.dhis.android.dashboard.api.utils.Preconditions.isNull;

/**
 * This class is intended to implement partial
 * functionality of ContentProviderOperation for DbFlow.
 */
public final class DbOperation<T extends IdentifiableObject> {
    private final Action mAction;
    private final T mModel;
    private final IStore<T> mModelStore;

    private DbOperation(Action action, T model, IStore<T> store) {
        mModel = isNull(model, "IdentifiableObject object must nto be null,");
        mAction = isNull(action, "BaseModel.Action object must not be null");
        mModelStore = isNull(store, "IStore object must not be null");
    }

    public static <T extends IdentifiableObject> DbOperationBuilder<T> with(IStore<T> store) {
        return new DbOperationBuilder<>(store);
    }

    public T getModel() {
        return mModel;
    }

    public Action getAction() {
        return mAction;
    }

    public IStore<T> getStore() {
        return mModelStore;
    }

    public void execute() {
        switch (mAction) {
            case INSERT: {
                mModelStore.insert(mModel);
                break;
            }
            case UPDATE: {
                mModelStore.update(mModel);
                break;
            }
            case SAVE: {
                break;
            }
            case DELETE: {
                mModelStore.delete(mModel);
                break;
            }
        }
    }

    public static class DbOperationBuilder<T extends IdentifiableObject> {
        private final IStore<T> mStore;

        DbOperationBuilder(IStore<T> store) {
            mStore = store;
        }

        public DbOperation insert(T model) {
            return new DbOperation<>(Action.INSERT, model, mStore);
        }

        public DbOperation update(T model) {
            return new DbOperation<>(Action.UPDATE, model, mStore);
        }

        public DbOperation save(T model) {
            return new DbOperation<>(Action.SAVE, model, mStore);
        }

        public DbOperation delete(T model) {
            return new DbOperation<>(Action.DELETE, model, mStore);
        }
    }

    enum Action {
        INSERT, UPDATE, SAVE, DELETE
    }
}
