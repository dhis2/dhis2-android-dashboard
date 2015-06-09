package org.dhis2.android.dashboard.api.persistence;

import android.util.Log;

import com.raizlabs.android.dbflow.runtime.TransactionManager;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.dhis2.mobile.sdk.persistence.models.BaseIdentifiableObject;
import org.dhis2.mobile.sdk.persistence.models.DbOperation;
import org.dhis2.mobile.sdk.persistence.models.RelationModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import static org.dhis2.mobile.sdk.utils.DbUtils.toMap;
import static org.dhis2.mobile.sdk.utils.Preconditions.isNull;

/**
 * This class is intended to process list of DbOperations
 * during single database transaction
 */
public final class DbHelper {
    private static final String TAG = DbHelper.class.getSimpleName();

    private DbHelper() {
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
            @Override public void run() {
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
                System.out.println("Deleting: " + oldModel.getId());
                ops.add(DbOperation.delete(oldModel));
                continue;
            }

            if (newModel.getLastUpdated().isAfter(oldModel.getLastUpdated())) {
                System.out.println("Updating: " + oldModel.getId());
                ops.add(DbOperation.update(newModel));
            }

            newModelsMap.remove(oldModelKey);
        }

        for (String newModelKey : newModelsMap.keySet()) {
            System.out.println("Inserting: " + newModelKey);
            T item = newModelsMap.get(newModelKey);
            ops.add(DbOperation.insert(item));
        }

        return ops;
    }

    // TODO The problem here is that each relation model does not contain orgUnit and dataSet keys (in case if it was not saved before)
    // TODO you need to find a new way to store keys in Relation Models
    public static <T extends BaseModel & RelationModel> List<DbOperation> syncRelationModels(List<T> oldRelationsList,
                                                                                             List<T> newRelationsList) {
        System.out.println("OldRelations: " + oldRelationsList.size());
        System.out.println("NewRelations: " + newRelationsList.size());

        List<DbOperation> ops = new ArrayList<>();
        Map<String, T> oldRelations = relationModelListToMap(oldRelationsList);
        Map<String, T> newRelations = relationModelListToMap(newRelationsList);

        for (String oldRelationKey : oldRelations.keySet()) {
            T oldRelation = oldRelations.get(oldRelationKey);
            T newRelation = newRelations.get(oldRelationKey);

            if (newRelation == null) {
                ops.add(DbOperation.delete(oldRelation));
                System.out.println("DELETING_OLD_RELATION: " + oldRelationKey);
                continue;
            }

            newRelations.remove(oldRelationKey);
        }

        for (String newRelationKey : newRelations.keySet()) {
            ops.add(DbOperation.insert(newRelations.get(newRelationKey)));
            System.out.println("INSERTING_NEW_RELATION: " + newRelationKey);
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

    public static <T extends BaseModel> List<DbOperation> save(List<T> models) {
        List<DbOperation> operations = new ArrayList<>();
        if (models != null && !models.isEmpty()) {
            for (T model : models) {
                operations.add(DbOperation.save(model));
            }
        }
        return operations;
    }

    private static <T extends RelationModel> Map<String, T> relationModelListToMap(List<T> relations) {
        Map<String, T> relationMap = new HashMap<>();
        if (relations != null && !relations.isEmpty()) {
            for (T relation : relations) {
                relationMap.put(relation.getFirstKey() + relation.getSecondKey(), relation);
            }
        }
        return relationMap;
    }
}