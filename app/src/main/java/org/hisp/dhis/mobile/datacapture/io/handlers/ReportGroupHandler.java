package org.hisp.dhis.mobile.datacapture.io.handlers;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import org.hisp.dhis.mobile.datacapture.api.android.models.DbRow;
import org.hisp.dhis.mobile.datacapture.api.models.Field;
import org.hisp.dhis.mobile.datacapture.api.models.Group;
import org.hisp.dhis.mobile.datacapture.io.DBContract.ReportGroups;
import org.hisp.dhis.mobile.datacapture.utils.DbUtils;

import java.util.ArrayList;
import java.util.List;

import static org.hisp.dhis.mobile.datacapture.api.utils.Preconditions.isNull;

public final class ReportGroupHandler {
    public static final String[] PROJECTION = new String[]{
            ReportGroups.TABLE_NAME + "." + ReportGroups.DB_ID,
            ReportGroups.TABLE_NAME + "." + ReportGroups.LABEL,
            ReportGroups.TABLE_NAME + "." + ReportGroups.DATA_ELEMENT_COUNT,
    };

    public static final String SELECTION_ID_REPORT =
            ReportGroups.REPORT_DB_ID + " = " + " ? ";

    private static final int DB_ID = 0;
    private static final int LABEL = 1;
    private static final int DATA_ELEMENT_COUNT = 2;

    private Context mContext;

    public ReportGroupHandler(Context context) {
        mContext = isNull(context, "Context object must not be null");
    }

    private static ContentValues toContentValues(Group group) {
        ContentValues values = new ContentValues();
        values.put(ReportGroups.LABEL, group.getLabel());
        values.put(ReportGroups.DATA_ELEMENT_COUNT, group.getDataElementCount());
        return values;
    }

    private static DbRow<Group> fromCursor(Cursor cursor) {
        Group group = new Group();
        group.setLabel(cursor.getString(LABEL));
        group.setDataElementCount(cursor.getInt(DATA_ELEMENT_COUNT));

        DbRow<Group> holder = new DbRow<>();
        holder.setId(cursor.getInt(DB_ID));
        holder.setItem(group);
        return holder;
    }

    public List<DbRow<Group>> query(int reportId) {
        ReportFieldHandler fieldHandler = new ReportFieldHandler(mContext);
        String[] selectionArgs = new String[]{reportId + ""};
        List<DbRow<Group>> groupRows = query(SELECTION_ID_REPORT, selectionArgs);
        for (DbRow<Group> row : groupRows) {
            List<Field> fields = DbUtils.stripRows(
                    fieldHandler.query(row.getId()));
            row.getItem().setFields(fields);
        }
        return groupRows;
    }

    public List<DbRow<Group>> query(String selection, String[] selectionArgs) {
        Cursor cursor = mContext.getContentResolver().query(
                ReportGroups.CONTENT_URI, PROJECTION, selection, selectionArgs, null
        );
        return map(cursor, true);
    }

    public static List<DbRow<Group>> map(Cursor cursor, boolean closeCursor) {
        List<DbRow<Group>> groups = new ArrayList<>();

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            do {
                groups.add(fromCursor(cursor));
            } while (cursor.moveToNext());

            if (closeCursor) {
                cursor.close();
            }
        }

        return groups;
    }

    public static void insertWithReference(List<ContentProviderOperation> ops,
                                           int reportIndex, List<Group> groups) {
        isNull(ops, "List<ContentProviderOperation> must not be null");

        if (groups != null && groups.size() > 0) {
            for (Group group : groups) {
                ops.add(ContentProviderOperation
                        .newInsert(ReportGroups.CONTENT_URI)
                        .withValueBackReference(ReportGroups.REPORT_DB_ID, reportIndex)
                        .withValues(toContentValues(group))
                        .build());
                int index = ops.size() - 1;
                ReportFieldHandler.insertWithReference(ops, index, group.getFields());
            }
        }
    }
}
