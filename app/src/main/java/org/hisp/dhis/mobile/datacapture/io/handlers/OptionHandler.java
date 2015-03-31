package org.hisp.dhis.mobile.datacapture.io.handlers;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import org.hisp.dhis.mobile.datacapture.api.android.models.DbRow;
import org.hisp.dhis.mobile.datacapture.api.models.Option;
import org.hisp.dhis.mobile.datacapture.io.DBContract.Options;

import java.util.ArrayList;
import java.util.List;

import static org.hisp.dhis.mobile.datacapture.api.utils.Preconditions.isNull;


public final class OptionHandler {
    public static final String[] PROJECTION = new String[]{
            Options.DB_ID,
            Options.ID,
            Options.CREATED,
            Options.LAST_UPDATED,
            Options.NAME
    };

    public static final String SELECTION = Options.OPTION_SET_DB_ID + " = " + " ? ";

    private static final int DB_ID = 0;
    private static final int ID = 1;
    private static final int CREATED = 2;
    private static final int LAST_UPDATED = 3;
    private static final int NAME = 4;

    private Context mContext;

    public OptionHandler(Context context) {
        mContext = isNull(context, "Context object must not be null");
    }

    public static ContentValues toContentValues(Option option) {
        isNull(option, "Option object must not be null");

        ContentValues values = new ContentValues();
        values.put(Options.ID, option.getId());
        values.put(Options.CREATED, option.getCreated());
        values.put(Options.LAST_UPDATED, option.getLastUpdated());
        values.put(Options.NAME, option.getName());
        return values;
    }

    public static DbRow<Option> fromCursor(Cursor cursor) {
        isNull(cursor, "Cursor object must not be null");

        DbRow<Option> row = new DbRow<>();
        Option option = new Option();
        option.setId(cursor.getString(ID));
        option.setCreated(cursor.getString(CREATED));
        option.setLastUpdated(cursor.getString(LAST_UPDATED));
        option.setName(cursor.getString(NAME));
        row.setId(cursor.getInt(DB_ID));
        row.setItem(option);
        return row;
    }

    public List<DbRow<Option>> query(int optionSetId) {
        Cursor cursor = mContext.getContentResolver().query(
                Options.CONTENT_URI, PROJECTION, SELECTION,
                new String[]{ optionSetId + "" }, null
        );

        return map(cursor, true);
    }

    public static List<DbRow<Option>> map(Cursor cursor, boolean closeCursor) {
        List<DbRow<Option>> options = new ArrayList<>();

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            do {
                options.add(fromCursor(cursor));
            } while(cursor.moveToNext());

            if (closeCursor) {
                cursor.close();
            }
        }

        return options;
    }

    public static void insertWithReference(List<ContentProviderOperation> ops,
                                           int index, Option option) {
        isNull(ops, "List<ContentProviderOperation> object must not be null");
        ops.add(ContentProviderOperation.newInsert(Options.CONTENT_URI)
                .withValueBackReference(Options.OPTION_SET_DB_ID, index)
                .withValues(toContentValues(option))
                .build());
    }
}