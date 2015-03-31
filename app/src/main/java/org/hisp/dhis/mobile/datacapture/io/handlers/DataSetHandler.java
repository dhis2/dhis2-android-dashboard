package org.hisp.dhis.mobile.datacapture.io.handlers;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import org.hisp.dhis.mobile.datacapture.api.android.models.DbRow;
import org.hisp.dhis.mobile.datacapture.api.models.DataSet;
import org.hisp.dhis.mobile.datacapture.api.models.DataSetOptions;
import org.hisp.dhis.mobile.datacapture.api.models.Field;
import org.hisp.dhis.mobile.datacapture.api.models.Group;
import org.hisp.dhis.mobile.datacapture.io.DBContract.DataSets;
import org.hisp.dhis.mobile.datacapture.utils.DbUtils;

import java.util.ArrayList;
import java.util.List;

import static org.hisp.dhis.mobile.datacapture.api.utils.Preconditions.isNull;

public class DataSetHandler {
    public static final String[] PROJECTION = new String[]{
            DataSets.DB_ID,
            DataSets.ID,
            DataSets.LABEL,
            DataSets.SUBTITLE,
            DataSets.ALLOW_FUTURE_PERIODS,
            DataSets.EXPIRY_DAYS,
            DataSets.PERIOD_TYPE,
    };

    public static final String SELECTION_BY_ORG_UNIT = DataSets.ORGANIZATION_UNIT_DB_ID +
            " = " + " ? ";
    public static final String DATASET_SELECTION = DataSets.ID + " = " + " ? ";

    private static final int DB_ID = 0;
    private static final int ID = 1;
    private static final int LABEL = 2;
    private static final int SUBTITLE = 3;
    private static final int ALLOW_FUTURE_PERIODS = 4;
    private static final int EXPIRY_DAYS = 5;
    private static final int PERIOD_TYPE = 6;

    private Context mContext;

    public DataSetHandler(Context context) {
        mContext = isNull(context, "Context object must not be null");
    }

    private static ContentValues toContentValues(DataSet dataSet) {
        isNull(dataSet, "DataSet object must not be null");

        ContentValues values = new ContentValues();
        values.put(DataSets.ID, dataSet.getId());
        values.put(DataSets.LABEL, dataSet.getLabel());
        values.put(DataSets.SUBTITLE, dataSet.getSubtitle());

        if (dataSet.getOptions() != null) {
            DataSetOptions options = dataSet.getOptions();
            values.put(DataSets.ALLOW_FUTURE_PERIODS,
                    options.isAllowFuturePeriods() ? 1 : 0);
            values.put(DataSets.EXPIRY_DAYS, options.getExpiryDays());
            values.put(DataSets.PERIOD_TYPE, options.getPeriodType());
        }

        return values;
    }

    private static DbRow<DataSet> fromCursor(Cursor cursor) {
        isNull(cursor, "Cursor object must not be null");

        DbRow<DataSet> row = new DbRow<>();
        DataSet dataSet = new DataSet();
        dataSet.setId(cursor.getString(ID));
        dataSet.setLabel(cursor.getString(LABEL));
        dataSet.setSubtitle(cursor.getString(SUBTITLE));

        DataSetOptions options = new DataSetOptions();
        options.setAllowFuturePeriods(cursor.getInt(ALLOW_FUTURE_PERIODS) == 1);
        options.setExpiryDays(cursor.getInt(EXPIRY_DAYS));
        options.setPeriodType(cursor.getString(PERIOD_TYPE));

        dataSet.setOptions(options);

        row.setId(cursor.getInt(DB_ID));
        row.setItem(dataSet);
        return row;
    }

    public List<DataSet> queryForOrganizationUnit(int orgUnitId) {
        String selection = DataSets.ORGANIZATION_UNIT_DB_ID + " = " + orgUnitId;
        List<DbRow<DataSet>> dataSetRows = query(selection);
        return DbUtils.stripRows(dataSetRows);
    }

    public List<DbRow<DataSet>> query(String selection) {
        Cursor cursor = mContext.getContentResolver().query(
                DataSets.CONTENT_URI, PROJECTION, selection, null, null
        );

        return query(cursor, true);
    }

    public DbRow<DataSet> queryById(String dataSetId, boolean readRelatedData) {
        Cursor cursor = mContext.getContentResolver().query(
                DataSets.CONTENT_URI, PROJECTION, DATASET_SELECTION,
                new String[]{dataSetId}, null
        );

        DbRow<DataSet> dataSet = querySingleItem(cursor, true);
        if (readRelatedData) {
            GroupHandler groupHandler = new GroupHandler(mContext);
            FieldHandler fieldHandler = new FieldHandler(mContext);

            List<DbRow<Group>> groupRows = groupHandler.query(dataSet.getId());
            for (DbRow<Group> group : groupRows) {
                List<DbRow<Field>> fieldRows = fieldHandler.query(group.getId());
                group.getItem().setFields(DbUtils.stripRows(fieldRows));
            }
            List<Group> groups = DbUtils.stripRows(groupRows);
            dataSet.getItem().setGroups(groups);
        }

        return dataSet;
    }

    public static List<DbRow<DataSet>> query(Cursor cursor, boolean closeCursor) {
        List<DbRow<DataSet>> rows = new ArrayList<>();

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            do {
                rows.add(fromCursor(cursor));
            } while (cursor.moveToNext());

            if (closeCursor) {
                cursor.close();
            }
        }

        return rows;
    }

    public static DbRow<DataSet> querySingleItem(Cursor cursor, boolean closeCursor) {
        DbRow<DataSet> row = null;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            row = fromCursor(cursor);

            if (closeCursor) {
                cursor.close();
            }
        }
        return row;
    }

    public static void insertWithReference(List<ContentProviderOperation> ops,
                                           int orgUnitIndex, List<DataSet> dataSets) {
        isNull(ops, "List<ContentProviderOperation> must not be null");

        if (dataSets != null && dataSets.size() > 0) {
            for (DataSet dataSet : dataSets) {
                ops.add(ContentProviderOperation
                        .newInsert(DataSets.CONTENT_URI)
                        .withValueBackReference(
                                DataSets.ORGANIZATION_UNIT_DB_ID, orgUnitIndex)
                        .withValues(toContentValues(dataSet))
                        .build());

                int index = ops.size() - 1;
                GroupHandler.insertWithReference(ops, index, dataSet.getGroups());
            }
        }
    }
}
