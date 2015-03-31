package org.hisp.dhis.mobile.datacapture.io.handlers;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import org.hisp.dhis.mobile.datacapture.api.android.models.DbRow;
import org.hisp.dhis.mobile.datacapture.api.models.Field;
import org.hisp.dhis.mobile.datacapture.io.DBContract.ReportFields;

import java.util.ArrayList;
import java.util.List;

import static org.hisp.dhis.mobile.datacapture.api.utils.Preconditions.isNull;

public final class ReportFieldHandler {
    public static final String[] PROJECTION = new String[]{
            ReportFields.DB_ID,
            ReportFields.LABEL,
            ReportFields.TYPE,
            ReportFields.DATA_ELEMENT,
            ReportFields.CATEGORY_OPTION_COMBO,
            ReportFields.OPTION_SET,
            ReportFields.VALUE
    };

    public static final String SELECTION =
            ReportFields.GROUP_DB_ID + " = " + " ? ";

    private static final int DB_ID = 0;
    private static final int LABEL = 1;
    private static final int TYPE = 2;
    private static final int DATA_ELEMENT = 3;
    private static final int CATEGORY_OPTION_COMBO = 4;
    private static final int OPTION_SET = 5;
    private static final int VALUE = 6;

    private Context mContext;

    public ReportFieldHandler(Context context) {
        mContext = isNull(context, "Context object must not be null");
    }

    private static ContentValues toContentValues(Field field) {
        ContentValues values = new ContentValues();
        values.put(ReportFields.LABEL, field.getLabel());
        values.put(ReportFields.TYPE, field.getType());
        values.put(ReportFields.DATA_ELEMENT, field.getDataElement());
        values.put(ReportFields.CATEGORY_OPTION_COMBO, field.getCategoryOptionCombo());
        values.put(ReportFields.OPTION_SET, field.getOptionSet());
        values.put(ReportFields.VALUE, field.getValue());
        return values;
    }

    private static DbRow<Field> fromCursor(Cursor cursor) {
        Field field = new Field();
        field.setLabel(cursor.getString(LABEL));
        field.setType(cursor.getString(TYPE));
        field.setDataElement(cursor.getString(DATA_ELEMENT));
        field.setCategoryOptionCombo(cursor.getString(CATEGORY_OPTION_COMBO));
        field.setOptionSet(cursor.getString(OPTION_SET));
        field.setValue(cursor.getString(VALUE));

        DbRow<Field> holder = new DbRow<>();
        holder.setId(cursor.getInt(DB_ID));
        holder.setItem(field);
        return holder;
    }

    public static List<DbRow<Field>> map(Cursor cursor, boolean closeCursor) {
        List<DbRow<Field>> fields = new ArrayList<>();

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            do {
                fields.add(fromCursor(cursor));
            } while (cursor.moveToNext());

            if (closeCursor) {
                cursor.close();
            }
        }

        return fields;
    }

    public static void insertWithReference(List<ContentProviderOperation> ops,
                                           int groupIndex, List<Field> fields) {
        isNull(ops, "List<ContentProviderOperation> must not be null");

        if (fields != null && fields.size() > 0) {
            for (Field field : fields) {
                ops.add(ContentProviderOperation
                        .newInsert(ReportFields.CONTENT_URI)
                        .withValueBackReference(ReportFields.GROUP_DB_ID, groupIndex)
                        .withValues(toContentValues(field))
                        .build());
            }
        }
    }

    public List<DbRow<Field>> query(int groupId) {
        String[] selectionArgs = new String[] { groupId + "" };
        Cursor cursor = mContext.getContentResolver().query(
                ReportFields.CONTENT_URI, PROJECTION, SELECTION, selectionArgs, null
        );
        return map(cursor, true);
    }
}
