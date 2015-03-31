package org.hisp.dhis.mobile.datacapture.io.handlers;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;

import org.hisp.dhis.mobile.datacapture.api.android.models.DbRow;
import org.hisp.dhis.mobile.datacapture.api.models.OrganisationUnit;
import org.hisp.dhis.mobile.datacapture.io.DBContract;
import org.hisp.dhis.mobile.datacapture.io.DBContract.OrganizationUnits;

import java.util.ArrayList;
import java.util.List;

import static org.hisp.dhis.mobile.datacapture.api.utils.Preconditions.isNull;

public class OrganizationUnitHandler {
    public static final String[] PROJECTION = new String[]{
            OrganizationUnits.DB_ID,
            OrganizationUnits.ID,
            OrganizationUnits.LABEL,
            OrganizationUnits.LEVEL,
            OrganizationUnits.PARENT
    };

    private static final int DB_ID = 0;
    private static final int ID = 1;
    private static final int LABEL = 2;
    private static final int LEVEL = 3;
    private static final int PARENT = 4;

    private Context mContext;

    public OrganizationUnitHandler(Context context) {
        mContext = isNull(context, "Context object must not be null");
    }

    private static ContentValues toContentValues(OrganisationUnit unit) {
        isNull(unit, "OrganizationUnit object must not be null");

        ContentValues values = new ContentValues();
        values.put(OrganizationUnits.ID, unit.getId());
        values.put(OrganizationUnits.LABEL, unit.getLabel());
        values.put(OrganizationUnits.LEVEL, unit.getLevel());
        values.put(OrganizationUnits.PARENT, unit.getParent());
        return values;
    }

    private static DbRow<OrganisationUnit> fromCursor(Cursor cursor) {
        isNull(cursor, "Cursor object must not be null");

        DbRow<OrganisationUnit> row = new DbRow<>();
        OrganisationUnit unit = new OrganisationUnit();
        unit.setId(cursor.getString(ID));
        unit.setLabel(cursor.getString(LABEL));
        unit.setLevel(cursor.getLong(LEVEL));
        unit.setParent(cursor.getString(PARENT));
        row.setId(cursor.getInt(DB_ID));
        row.setItem(unit);
        return row;
    }

    public List<DbRow<OrganisationUnit>> query(String selection) {
        Cursor cursor = mContext.getContentResolver().query(
                OrganizationUnits.CONTENT_URI, PROJECTION, selection, null, null
        );

        return query(cursor, true);
    }

    public static List<DbRow<OrganisationUnit>> query(Cursor cursor, boolean closeCursor) {
        List<DbRow<OrganisationUnit>> rows = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            // DataSetHandler dataSetHandler = new DataSetHandler(mContext);

            do {
                DbRow<OrganisationUnit> row = fromCursor(cursor);
                // List<DataSet> dataSets = dataSetHandler
                // .queryForOrganizationUnit(row.getId());
                // row.getItem().setDataSets(dataSets);
                rows.add(row);
            } while (cursor.moveToNext());

            if (closeCursor) {
                cursor.close();
            }
        }
        return rows;
    }

    public void bulkInsert(List<OrganisationUnit> units) {
        deleteAll();
        insert(units);
    }

    private void deleteAll() {
        mContext.getContentResolver().delete(
                OrganizationUnits.CONTENT_URI, null, null);
    }

    private void insert(List<OrganisationUnit> units) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        if (units != null && units.size() > 0) {
            for (OrganisationUnit unit : units) {
                insert(ops, unit);
            }
        }

        try {
            mContext.getContentResolver().applyBatch(DBContract.AUTHORITY, ops);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }

    private static void insert(List<ContentProviderOperation> ops,
                               OrganisationUnit unit) {
        isNull(ops, "List<ContentProviderOperation> must not be null");
        isNull(unit, "OrganizationUnit must not be null");

        ops.add(ContentProviderOperation
                .newInsert(OrganizationUnits.CONTENT_URI)
                .withValues(toContentValues(unit))
                .build());

        int index = ops.size() - 1;
        DataSetHandler.insertWithReference(ops, index, unit.getDataSets());
    }
}
