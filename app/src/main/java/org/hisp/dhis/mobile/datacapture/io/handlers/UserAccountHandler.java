package org.hisp.dhis.mobile.datacapture.io.handlers;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;

import org.hisp.dhis.mobile.datacapture.api.android.models.DbRow;
import org.hisp.dhis.mobile.datacapture.api.models.Field;
import org.hisp.dhis.mobile.datacapture.api.models.UserAccount;
import org.hisp.dhis.mobile.datacapture.io.DBContract;
import org.hisp.dhis.mobile.datacapture.io.DBContract.UserAccountFields;
import org.hisp.dhis.mobile.datacapture.ui.adapters.rows.RowTypes;

import java.util.ArrayList;
import java.util.List;

import static org.hisp.dhis.mobile.datacapture.api.utils.Preconditions.isNull;

public final class UserAccountHandler {
    private static final String[] PROJECTION = new String[]{
            UserAccountFields.DB_ID,
            UserAccountFields.DATA_ELEMENT,
            UserAccountFields.TYPE,
            UserAccountFields.VALUE
    };

    private static final int DB_ID = 0;
    private static final int DATA_ELEMENT = 1;
    private static final int TYPE = 2;
    private static final int VALUE = 3;

    private static final String BIRTHDAY = "birthday";
    private static final String EDUCATION = "education";
    private static final String EMAIL = "email";
    private static final String EMPLOYER = "employer";
    private static final String FIRST_NAME = "firstName";
    private static final String GENDER = "gender";
    private static final String INTERESTS = "interests";
    private static final String INTRODUCTION = "introduction";
    private static final String JOB_TITLE = "jobTitle";
    private static final String LANGUAGES = "languages";
    private static final String PHONE_NUMBER = "phoneNumber";
    private static final String SURNAME = "surname";
    private static final String USERNAME = "username";

    private Context mContext;

    public UserAccountHandler(Context context) {
        mContext = isNull(context, "Context object must not be null");
    }

    private static ContentValues toContentValues(Field field) {
        isNull(field, "Field object must not be null");

        ContentValues values = new ContentValues();
        values.put(UserAccountFields.DATA_ELEMENT, field.getDataElement());
        values.put(UserAccountFields.TYPE, field.getType());
        values.put(UserAccountFields.VALUE, field.getValue());

        return values;
    }

    private static DbRow<Field> fromCursor(Cursor cursor) {
        isNull(cursor, "Cursor object must not be null");

        Field field = new Field();
        field.setDataElement(cursor.getString(DATA_ELEMENT));
        field.setType(cursor.getString(TYPE));
        field.setValue(cursor.getString(VALUE));

        DbRow<Field> dbRow = new DbRow<>();
        dbRow.setId(cursor.getInt(DB_ID));
        dbRow.setItem(field);

        return dbRow;
    }

    public void insert(UserAccount account) {
        isNull(account, "UserAccount object must not be null");

        List<Field> fields = toFields(account);
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        insert(ops, fields);

        try {
            mContext.getContentResolver().applyBatch(DBContract.AUTHORITY, ops);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }

    public void update(UserAccount account) {
        isNull(account, "UserAccount object must not be null");
        update(toFields(account));
    }

    public void update(List<Field> fields) {
        isNull(fields, "List<Field> must not be null");

        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        update(ops, fields);

        try {
            mContext.getContentResolver().applyBatch(DBContract.AUTHORITY, ops);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }

    public List<DbRow<Field>> query() {
        Cursor cursor = mContext.getContentResolver().query(
                UserAccountFields.CONTENT_URI, PROJECTION, null, null, null
        );

        return map(cursor, true);
    }

    public static List<DbRow<Field>> map(Cursor cursor, boolean close) {
        List<DbRow<Field>> rows = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            do {
                rows.add(fromCursor(cursor));
            } while (cursor.moveToNext());

            if (close) {
                cursor.close();
            }
        }

        return rows;
    }

    private static void insert(List<ContentProviderOperation> ops,
                               List<Field> fields) {
        for (Field field : fields) {
            ops.add(ContentProviderOperation
                    .newInsert(UserAccountFields.CONTENT_URI)
                    .withValues(toContentValues(field))
                    .build());
        }
    }

    private static void update(List<ContentProviderOperation> ops,
                               List<Field> fields) {
        for (Field field : fields) {
            ops.add(ContentProviderOperation
                    .newUpdate(UserAccountFields.CONTENT_URI)
                    .withValues(toContentValues(field))
                    .build());
        }
    }

    public static List<Field> toFields(UserAccount account) {
        List<Field> fields = new ArrayList<>();
        fields.add(buildField(BIRTHDAY, RowTypes.DATE.toString(), account.getBirthday()));
        fields.add(buildField(EDUCATION, RowTypes.TEXT.toString(), account.getEducation()));
        fields.add(buildField(EMAIL, RowTypes.TEXT.toString(), account.getEmail()));
        fields.add(buildField(EMPLOYER, RowTypes.TEXT.toString(), account.getEmployer()));
        fields.add(buildField(FIRST_NAME, RowTypes.TEXT.toString(), account.getFirstName()));
        fields.add(buildField(GENDER, RowTypes.BOOLEAN.toString(), account.getGender()));
        fields.add(buildField(INTERESTS, RowTypes.TEXT.toString(), account.getInterests()));
        fields.add(buildField(INTRODUCTION, RowTypes.TEXT.toString(), account.getIntroduction()));
        fields.add(buildField(JOB_TITLE, RowTypes.TEXT.toString(), account.getJobTitle()));
        fields.add(buildField(LANGUAGES, RowTypes.TEXT.toString(), account.getLanguages()));
        fields.add(buildField(PHONE_NUMBER, RowTypes.TEXT.toString(), account.getPhoneNumber()));
        fields.add(buildField(SURNAME, RowTypes.TEXT.toString(), account.getSurname()));
        fields.add(buildField(USERNAME, RowTypes.TEXT.toString(), account.getUsername()));
        return fields;
    }

    private static Field buildField(String dataElement,
                                    String type, String value) {
        Field field = new Field();
        field.setDataElement(dataElement);
        field.setType(type);
        field.setValue(value);
        return field;
    }
}
