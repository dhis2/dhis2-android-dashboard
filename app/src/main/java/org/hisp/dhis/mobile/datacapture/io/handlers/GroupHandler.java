package org.hisp.dhis.mobile.datacapture.io.handlers;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import org.hisp.dhis.mobile.datacapture.api.android.models.DbRow;
import org.hisp.dhis.mobile.datacapture.api.models.Group;
import org.hisp.dhis.mobile.datacapture.io.DBContract.Groups;

import java.util.ArrayList;
import java.util.List;

import static org.hisp.dhis.mobile.datacapture.api.utils.Preconditions.isNull;

public final class GroupHandler {
    public static final String[] PROJECTION = new String[]{
            Groups.DB_ID,
            Groups.LABEL,
            Groups.DATA_ELEMENT_COUNT
    };

    public static final String GROUP_SELECTION = Groups.DATA_SET_DB_ID + " = " + " ? ";

    private static final int DB_ID = 0;
    private static final int LABEL = 1;
    private static final int DATA_ELEMENT_COUNT = 2;

    private Context mContext;

    public GroupHandler(Context context) {
        mContext = isNull(context, "Context object must not be null");
    }

    public List<DbRow<Group>> query(int dataSetId) {
        Cursor cursor = mContext.getContentResolver().query(
                Groups.CONTENT_URI, PROJECTION, GROUP_SELECTION,
                new String[]{dataSetId + ""}, null
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

    public static ContentValues toContentValues(Group group) {
        isNull(group, "Group must not be null");

        ContentValues values = new ContentValues();
        values.put(Groups.LABEL, group.getLabel());
        values.put(Groups.DATA_ELEMENT_COUNT, group.getDataElementCount());

        return values;
    }

    public static DbRow<Group> fromCursor(Cursor cursor) {
        isNull(cursor, "Cursor object must not be null");

        DbRow<Group> row = new DbRow<>();
        Group group = new Group();
        group.setLabel(cursor.getString(LABEL));
        group.setDataElementCount(cursor.getInt(DATA_ELEMENT_COUNT));
        row.setId(cursor.getInt(DB_ID));
        row.setItem(group);
        return row;
    }

    public static void insertWithReference(List<ContentProviderOperation> ops,
                                           int dataSetIndex, List<Group> groups) {
        isNull(ops, "List<ContentProviderOperation> must not be null");

        if (groups != null && groups.size() > 0) {
            for (Group group : groups) {
                ops.add(ContentProviderOperation
                        .newInsert(Groups.CONTENT_URI)
                        .withValueBackReference(Groups.DATA_SET_DB_ID, dataSetIndex)
                        .withValues(toContentValues(group))
                        .build());
                int index = ops.size() - 1;
                FieldHandler.insertWithReference(ops, index, group.getFields());
            }
        }
    }
}
