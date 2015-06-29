package org.dhis2.android.dashboard.api.utils;

import android.util.Log;

import com.raizlabs.android.dbflow.runtime.TransactionManager;

import org.dhis2.android.dashboard.api.models.BaseIdentifiableObject;
import org.dhis2.android.dashboard.api.models.meta.DbOperation;
import org.dhis2.android.dashboard.api.persistence.DbDhis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.dhis2.android.dashboard.api.utils.CollectionUtils.toMap;
import static org.dhis2.android.dashboard.api.utils.Preconditions.isNull;

/**
 * This class is intended to process list of DbOperations
 * during single database transaction
 */
public final class DbUtils {
    private static final String TAG = DbUtils.class.getSimpleName();

    private DbUtils() {
        // no instances
    }

    /**
     * Performs each DbOperation during one database transaction
     *
     * @param operations List of DbOperations to be performed.
     */
    public static void applyBatch(final Collection<DbOperation> operations) {
        isNull(operations, "List<DbOperation> object must not be null");

        if (operations.isEmpty()) {
            return;
        }

        TransactionManager.transact(DbDhis.NAME, new Runnable() {
            @Override
            public void run() {
                for (DbOperation operation : operations) {
                    switch (operation.getOperationType()) {
                        case INSERT: {
                            operation.getModel().insert();
                            break;
                        }
                        case UPDATE: {
                            operation.getModel().update();
                            break;
                        }
                        case SAVE:
                            operation.getModel().save();
                            break;
                        case DELETE: {
                            operation.getModel().delete();
                            break;
                        }
                    }
                }
            }
        });
    }

    /**
     * This utility method allows to determine which type of operation to apply to
     * each BaseIdentifiableObject depending on TimeStamp.
     *
     * @param oldModels List of models from local storage.
     * @param newModels List of models of distance instance of DHIS.
     */
    public static <T extends BaseIdentifiableObject> List<DbOperation> createOperations(List<T> oldModels,
                                                                                        List<T> newModels) {
        List<DbOperation> ops = new ArrayList<>();

        Map<String, T> newModelsMap = toMap(newModels);
        Map<String, T> oldModelsMap = toMap(oldModels);

        for (String oldModelKey : oldModelsMap.keySet()) {
            T newModel = newModelsMap.get(oldModelKey);
            T oldModel = oldModelsMap.get(oldModelKey);

            if (newModel == null) {
                ops.add(DbOperation.delete(oldModel));
                continue;
            }

            if (newModel.getLastUpdated().isAfter(oldModel.getLastUpdated())) {
                ops.add(DbOperation.update(newModel));
            }

            newModelsMap.remove(oldModelKey);
        }

        for (String newModelKey : newModelsMap.keySet()) {
            T item = newModelsMap.get(newModelKey);
            ops.add(DbOperation.insert(item));
        }

        return ops;
    }

    public static <T extends BaseIdentifiableObject> List<DbOperation> createOperations(Collection<T> oldModels,
                                                                                        Collection<T> newModels) {
        List<DbOperation> ops = new ArrayList<>();

        Map<String, T> newModelsMap = toMap(newModels);
        Map<String, T> oldModelsMap = toMap(oldModels);

        for (String oldModelKey : oldModelsMap.keySet()) {
            T oldModel = oldModelsMap.get(oldModelKey);

            if (!newModelsMap.containsKey(oldModelKey)) {
                Log.d(TAG, "Deleting: " + oldModel.getId());
                ops.add(DbOperation.delete(oldModel));
                continue;
            }

            T newModel = newModelsMap.get(oldModelKey);
            if (newModel != null && newModel.getLastUpdated()
                    .isAfter(oldModel.getLastUpdated())) {
                Log.d(TAG, "Updating: " + oldModel.getId());
                ops.add(DbOperation.update(newModel));
            }

            newModelsMap.remove(oldModelKey);
        }

        for (String newModelKey : newModelsMap.keySet()) {
            T item = newModelsMap.get(newModelKey);
            if (item != null) {
                Log.d(TAG, "Inserting: " + newModelKey);
                ops.add(DbOperation.insert(item));
            } else {
                throw new IllegalArgumentException("Something went wrong");
            }
        }

        return ops;
    }
}