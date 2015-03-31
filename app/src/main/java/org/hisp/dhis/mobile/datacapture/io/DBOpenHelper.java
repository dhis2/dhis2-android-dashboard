package org.hisp.dhis.mobile.datacapture.io;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "dhis2.db";
    private static final String ENABLE_FOREIGN_KEYS = "PRAGMA foreign_keys = ON;";
    private static final int DATABASE_VERSION = 8;

    public DBOpenHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBSchema.CREATE_DASHBOARD_TABLE);
        db.execSQL(DBSchema.CREATE_DASHBOARD_ITEMS_TABLE);
        db.execSQL(DBSchema.CREATE_INTERPRETATIONS_TABLE);

        db.execSQL(DBSchema.CREATE_REPORTS_TABLE);
        db.execSQL(DBSchema.CREATE_REPORT_GROUP_TABLE);
        db.execSQL(DBSchema.CREATE_REPORT_FIELDS_TABLE);

        db.execSQL(DBSchema.CREATE_ORGANIZATION_UNIT_TABLE);
        db.execSQL(DBSchema.CREATE_DATASET_TABLE);
        db.execSQL(DBSchema.CREATE_GROUP_TABLE);
        db.execSQL(DBSchema.CREATE_FIELD_TABLE);

        db.execSQL(DBSchema.CREATE_OPTION_SET_TABLE);
        db.execSQL(DBSchema.CREATE_OPTION_TABLE);
        db.execSQL(DBSchema.CREATE_USER_ACCOUNT_FIELDS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DBSchema.DROP_DASHBOARD_TABLE);
        db.execSQL(DBSchema.DROP_DASHBOARD_ITEMS_TABLE);
        db.execSQL(DBSchema.DROP_INTERPRETATIONS_TABLE);

        db.execSQL(DBSchema.DROP_REPORTS_TABLE);
        db.execSQL(DBSchema.DROP_REPORT_GROUP_TABLE);
        db.execSQL(DBSchema.DROP_REPORT_FIELDS_TABLE);

        db.execSQL(DBSchema.DROP_ORGANIZATION_UNIT_TABLE);
        db.execSQL(DBSchema.DROP_DATASET_TABLE);
        db.execSQL(DBSchema.DROP_GROUP_TABLE);
        db.execSQL(DBSchema.DROP_FIELD_TABLE);

        db.execSQL(DBSchema.DROP_OPTION_SET_TABLE);
        db.execSQL(DBSchema.DROP_OPTION_TABLE);
        db.execSQL(DBSchema.DROP_USER_ACCOUNT_FIELDS_TABLE);

        onCreate(db);
    }

    /**
     * Enabling support of ForeignKeys in SQLite database
     * each time it is being used. Works on android from 2.2
     */
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.execSQL(ENABLE_FOREIGN_KEYS);
        }
    }
}
