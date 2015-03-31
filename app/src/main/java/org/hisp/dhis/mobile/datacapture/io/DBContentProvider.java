package org.hisp.dhis.mobile.datacapture.io;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import org.hisp.dhis.mobile.datacapture.io.DBContract.DashboardItems;
import org.hisp.dhis.mobile.datacapture.io.DBContract.Dashboards;
import org.hisp.dhis.mobile.datacapture.io.DBContract.DataSets;
import org.hisp.dhis.mobile.datacapture.io.DBContract.Fields;
import org.hisp.dhis.mobile.datacapture.io.DBContract.Groups;
import org.hisp.dhis.mobile.datacapture.io.DBContract.Interpretations;
import org.hisp.dhis.mobile.datacapture.io.DBContract.OptionSets;
import org.hisp.dhis.mobile.datacapture.io.DBContract.Options;
import org.hisp.dhis.mobile.datacapture.io.DBContract.OrganizationUnits;
import org.hisp.dhis.mobile.datacapture.io.DBContract.ReportFields;
import org.hisp.dhis.mobile.datacapture.io.DBContract.ReportGroups;
import org.hisp.dhis.mobile.datacapture.io.DBContract.Reports;
import org.hisp.dhis.mobile.datacapture.io.DBContract.UserAccountFields;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static android.content.ContentUris.parseId;
import static android.content.ContentUris.withAppendedId;
import static android.text.TextUtils.isEmpty;


public class DBContentProvider extends ContentProvider {
    private static final int DASHBOARDS = 600;
    private static final int DASHBOARD_ID = 601;

    private static final int DASHBOARD_ITEMS = 701;
    private static final int DASHBOARD_ITEM_ID = 702;

    private static final int INTERPRETATIONS = 800;
    private static final int INTERPRETATIONS_ID = 801;

    private static final int REPORTS = 1100;
    private static final int REPORT_ID = 1101;
    private static final int REPORT_WITH_GROUPS = 1102;

    private static final int REPORT_GROUPS = 1200;
    private static final int REPORT_GROUP_ID = 1201;

    private static final int REPORT_FIELDS = 1300;
    private static final int REPORT_FIELD_ID = 1301;

    private static final int OPTION_SETS = 1400;
    private static final int OPTION_SET_ID = 1401;

    private static final int OPTIONS = 1500;
    private static final int OPTION_ID = 1501;

    private static final int FIELDS = 1600;
    //private static final int FIELDS_WITH_OPTION_SETS = 1602;
    private static final int FIELD_ID = 1601;

    private static final int GROUPS = 1700;
    private static final int GROUP_ID = 1701;

    private static final int ORGANIZATION_UNITS = 1800;
    private static final int ORGANIZATION_UNIT_ID = 1801;
    private static final int ORGANIZATION_UNITS_WITH_DATASETS = 1802;

    private static final int DATASETS = 1900;
    private static final int DATASET_ID = 1901;
    //private static final int DATASET_ID_WITH_GROUPS = 1902;

    private static final int USER_ACCOUNT_FIELDS = 2000;
    private static final int USER_ACCOUNT_FIELD_ID = 2001;

    private static final UriMatcher URI_MATCHER = buildMatcher();

    private DBOpenHelper mDBHelper;
    private ThreadLocal<Boolean> mIsInBatchMode;

    private static UriMatcher buildMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(DBContract.AUTHORITY, Dashboards.DASHBOARDS, DASHBOARDS);
        matcher.addURI(DBContract.AUTHORITY, Dashboards.DASHBOARD_ID, DASHBOARD_ID);
        matcher.addURI(DBContract.AUTHORITY, DashboardItems.DASHBOARD_ITEMS, DASHBOARD_ITEMS);
        matcher.addURI(DBContract.AUTHORITY, DashboardItems.DASHBOARD_ITEM_ID, DASHBOARD_ITEM_ID);
        matcher.addURI(DBContract.AUTHORITY, Interpretations.INTERPRETATIONS, INTERPRETATIONS);
        matcher.addURI(DBContract.AUTHORITY, Interpretations.INTERPRETATION_ID, INTERPRETATIONS_ID);
        matcher.addURI(DBContract.AUTHORITY, Reports.REPORTS, REPORTS);
        matcher.addURI(DBContract.AUTHORITY, Reports.REPORT_ID, REPORT_ID);
        matcher.addURI(DBContract.AUTHORITY, Reports.REPORT_WITH_GROUPS, REPORT_WITH_GROUPS);
        matcher.addURI(DBContract.AUTHORITY, ReportGroups.REPORT_GROUPS, REPORT_GROUPS);
        matcher.addURI(DBContract.AUTHORITY, ReportGroups.REPORT_GROUP_ID, REPORT_GROUP_ID);
        matcher.addURI(DBContract.AUTHORITY, ReportFields.REPORT_FIELDS, REPORT_FIELDS);
        matcher.addURI(DBContract.AUTHORITY, ReportFields.REPORT_FIELD_ID, REPORT_FIELD_ID);
        matcher.addURI(DBContract.AUTHORITY, OptionSets.OPTION_SETS, OPTION_SETS);
        matcher.addURI(DBContract.AUTHORITY, OptionSets.OPTION_SET_ID, OPTION_SET_ID);
        matcher.addURI(DBContract.AUTHORITY, Options.OPTIONS, OPTIONS);
        matcher.addURI(DBContract.AUTHORITY, Options.OPTION_ID, OPTION_ID);
        matcher.addURI(DBContract.AUTHORITY, Fields.FIELDS, FIELDS);
        matcher.addURI(DBContract.AUTHORITY, Fields.FIELD_ID, FIELD_ID);
        matcher.addURI(DBContract.AUTHORITY, Groups.GROUPS, GROUPS);
        matcher.addURI(DBContract.AUTHORITY, Groups.GROUP_ID, GROUP_ID);
        matcher.addURI(DBContract.AUTHORITY, OrganizationUnits.ORGANIZATION_UNITS, ORGANIZATION_UNITS);
        matcher.addURI(DBContract.AUTHORITY, OrganizationUnits.ORGANIZATION_UNIT_ID, ORGANIZATION_UNIT_ID);
        matcher.addURI(DBContract.AUTHORITY, OrganizationUnits.ORGANIZATION_UNITS_WITH_DATASETS, ORGANIZATION_UNITS_WITH_DATASETS);
        matcher.addURI(DBContract.AUTHORITY, DataSets.DATASETS, DATASETS);
        matcher.addURI(DBContract.AUTHORITY, DataSets.DATASET_ID, DATASET_ID);
        matcher.addURI(DBContract.AUTHORITY, UserAccountFields.FIELDS, USER_ACCOUNT_FIELDS);
        matcher.addURI(DBContract.AUTHORITY, UserAccountFields.FIELD_ID, USER_ACCOUNT_FIELD_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mDBHelper = new DBOpenHelper(getContext());
        mIsInBatchMode = new ThreadLocal<>();
        return true;
    }

    @Override
    public String getType(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case DASHBOARDS:
                return Dashboards.CONTENT_TYPE;
            case DASHBOARD_ID:
                return Dashboards.CONTENT_ITEM_TYPE;
            case DASHBOARD_ITEMS:
                return DashboardItems.CONTENT_TYPE;
            case DASHBOARD_ITEM_ID:
                return DashboardItems.CONTENT_ITEM_TYPE;
            case INTERPRETATIONS:
                return Interpretations.CONTENT_TYPE;
            case INTERPRETATIONS_ID:
                return Interpretations.CONTENT_ITEM_TYPE;
            case REPORTS:
                return Reports.CONTENT_TYPE;
            case REPORT_ID:
                return Reports.CONTENT_ITEM_TYPE;
            case REPORT_WITH_GROUPS:
                return ReportGroups.CONTENT_TYPE;
            case REPORT_GROUPS:
                return ReportGroups.CONTENT_TYPE;
            case REPORT_GROUP_ID:
                return ReportGroups.CONTENT_ITEM_TYPE;
            case REPORT_FIELDS:
                return ReportFields.CONTENT_TYPE;
            case REPORT_FIELD_ID:
                return ReportFields.CONTENT_ITEM_TYPE;
            case OPTION_SETS:
                return OptionSets.CONTENT_TYPE;
            case OPTION_SET_ID:
                return OptionSets.CONTENT_ITEM_TYPE;
            case OPTIONS:
                return Options.CONTENT_TYPE;
            case OPTION_ID:
                return Options.CONTENT_ITEM_TYPE;
            case FIELDS:
                return Fields.CONTENT_TYPE;
            case FIELD_ID:
                return Fields.CONTENT_ITEM_TYPE;
            case GROUPS:
                return Groups.CONTENT_TYPE;
            case GROUP_ID:
                return Groups.CONTENT_ITEM_TYPE;
            case ORGANIZATION_UNITS:
                return OrganizationUnits.CONTENT_TYPE;
            case ORGANIZATION_UNIT_ID:
                return OrganizationUnits.CONTENT_ITEM_TYPE;
            case ORGANIZATION_UNITS_WITH_DATASETS:
                return DataSets.CONTENT_TYPE;
            case DATASETS:
                return DataSets.CONTENT_TYPE;
            case DATASET_ID:
                return DataSets.CONTENT_ITEM_TYPE;
            case USER_ACCOUNT_FIELDS:
                return UserAccountFields.CONTENT_TYPE;
            case USER_ACCOUNT_FIELD_ID:
                return UserAccountFields.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("No corresponding Uri type was found");
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        switch (URI_MATCHER.match(uri)) {
            case DASHBOARDS: {
                return query(uri, Dashboards.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
            }
            case DASHBOARD_ID: {
                String id = String.valueOf(parseId(uri));
                return queryId(uri, Dashboards.TABLE_NAME, Dashboards.DB_ID,
                        projection, selection, selectionArgs, sortOrder, id);
            }
            case DASHBOARD_ITEMS: {
                return query(uri, DashboardItems.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
            }
            case DASHBOARD_ITEM_ID: {
                String id = String.valueOf(parseId(uri));
                return queryId(uri, DashboardItems.TABLE_NAME, DashboardItems.DB_ID,
                        projection, selection, selectionArgs, sortOrder, id);
            }
            case INTERPRETATIONS: {
                return query(uri, Interpretations.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
            }
            case INTERPRETATIONS_ID: {
                String id = String.valueOf(parseId(uri));
                return queryId(uri, Interpretations.TABLE_NAME, Interpretations.DB_ID,
                        projection, selection, selectionArgs, sortOrder, id);
            }
            case REPORTS: {
                return query(uri, Reports.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
            }
            case REPORT_ID: {
                String id = String.valueOf(parseId(uri));
                return queryId(uri, Reports.TABLE_NAME, Reports.DB_ID,
                        projection, selection, selectionArgs, sortOrder, id);
            }
            case REPORT_WITH_GROUPS: {
                String table = Reports.TABLE_NAME +
                        " LEFT OUTER JOIN " + ReportGroups.TABLE_NAME +
                        " ON " + Reports.TABLE_NAME + "." + Reports.DB_ID +
                        " = " + ReportGroups.TABLE_NAME + "." + ReportGroups.REPORT_DB_ID;
                return query(uri, table, projection, selection, selectionArgs, sortOrder);
            }
            case REPORT_GROUPS: {
                return query(uri, ReportGroups.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
            }
            case REPORT_GROUP_ID: {
                String id = String.valueOf(parseId(uri));
                return queryId(uri, ReportGroups.TABLE_NAME, ReportGroups.DB_ID,
                        projection, selection, selectionArgs, sortOrder, id);
            }
            case REPORT_FIELDS: {
                return query(uri, ReportFields.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
            }
            case REPORT_FIELD_ID: {
                String id = String.valueOf(parseId(uri));
                return queryId(uri, ReportFields.TABLE_NAME, ReportFields.DB_ID,
                        projection, selection, selectionArgs, sortOrder, id);
            }
            case OPTION_SETS: {
                return query(uri, OptionSets.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
            }
            case OPTION_SET_ID: {
                String id = String.valueOf(parseId(uri));
                return queryId(uri, OptionSets.TABLE_NAME, OptionSets.DB_ID,
                        projection, selection, selectionArgs, sortOrder, id);
            }
            case OPTIONS: {
                return query(uri, Options.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
            }
            case OPTION_ID: {
                String id = String.valueOf(parseId(uri));
                return queryId(uri, Options.TABLE_NAME, Options.DB_ID,
                        projection, selection, selectionArgs, sortOrder, id);
            }
            case FIELDS: {
                return query(uri, Fields.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
            }
            /* case FIELDS_WITH_OPTION_SETS: {
                String table = FieldColumns.TABLE_NAME +
                        " FULL OUTER JOIN " + OptionSetColumns.TABLE_NAME +
                        " ON " + FieldColumns.TABLE_NAME + "." + FieldColumns.OPTION_SET +
                        " = " + OptionSetColumns.TABLE_NAME + "." + OptionSetColumns.ID +
                        " FULL OUTER JOIN " + OptionColumns.TABLE_NAME +
                        " ON " + OptionSetColumns.TABLE_NAME + "." + OptionSetColumns.DB_ID +
                        " = " + OptionColumns.TABLE_NAME + "." + OptionColumns.OPTION_SET_DB_ID;
                return query(uri, table, projection,
                        selection, selectionArgs, sortOrder);
            } */
            case FIELD_ID: {
                String id = String.valueOf(parseId(uri));
                return queryId(uri, Fields.TABLE_NAME, Fields.DB_ID,
                        projection, selection, selectionArgs, sortOrder, id);
            }
            case GROUPS: {
                return query(uri, Groups.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
            }
            case GROUP_ID: {
                String id = String.valueOf(parseId(uri));
                return queryId(uri, Groups.TABLE_NAME, Groups.DB_ID,
                        projection, selection, selectionArgs, sortOrder, id);
            }
            case ORGANIZATION_UNITS: {
                return query(uri, OrganizationUnits.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
            }
            case ORGANIZATION_UNITS_WITH_DATASETS: {
                String table = OrganizationUnits.TABLE_NAME +
                        " FULL OUTER JOIN " + DataSets.TABLE_NAME +
                        " ON " + OrganizationUnits.TABLE_NAME + "." + OrganizationUnits.DB_ID +
                        " = " + DataSets.TABLE_NAME + "." + DataSets.ORGANIZATION_UNIT_DB_ID;
                return query(uri, table, projection, selection, selectionArgs, sortOrder);
            }
            case ORGANIZATION_UNIT_ID: {
                String id = String.valueOf(parseId(uri));
                return queryId(uri, OrganizationUnits.TABLE_NAME,
                        OrganizationUnits.DB_ID, projection, selection,
                        selectionArgs, sortOrder, id);
            }
            case DATASETS: {
                return query(uri, DataSets.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
            }
            case DATASET_ID: {
                String id = String.valueOf(parseId(uri));
                return queryId(uri, DataSets.TABLE_NAME,
                        DataSets.DB_ID, projection, selection,
                        selectionArgs, sortOrder, id);
            }

            case USER_ACCOUNT_FIELDS: {
                return query(uri, UserAccountFields.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
            }
            case USER_ACCOUNT_FIELD_ID: {
                String id = String.valueOf(parseId(uri));
                return queryId(uri, UserAccountFields.TABLE_NAME,
                        UserAccountFields.DB_ID, projection, selection,
                        selectionArgs, sortOrder, id);
            }
            /* case DATASET_ID_WITH_GROUPS: {
                String table = DataSetColumns.TABLE_NAME +
                        " FULL OUTER JOIN " + GroupColumns.TABLE_NAME +
                        " ON " + DataSetColumns.TABLE_NAME + "." + DataSetColumns.DB_ID +
                        " = " + GroupColumns.TABLE_NAME + "." + GroupColumns.DATA_SET_DB_ID +
                        " FULL OUTER JOIN " + FieldColumns.TABLE_NAME +
                        " ON " + GroupColumns.TABLE_NAME + "." + GroupColumns.DB_ID +
                        " = " + FieldColumns.TABLE_NAME + "." + FieldColumns.GROUP_DB_ID +
                        " FULL OUTER JOIN " + OptionSetColumns.TABLE_NAME +
                        " ON " + FieldColumns.TABLE_NAME + "." + FieldColumns.OPTION_SET +
                        " = " + OptionSetColumns.TABLE_NAME + "." + OptionSetColumns.ID +
                        " FULL OUTER JOIN " + OptionColumns.TABLE_NAME +
                        " ON " + OptionSetColumns.TABLE_NAME + "." + OptionSetColumns.DB_ID +
                        " = " + OptionColumns.TABLE_NAME + "." + OptionColumns.OPTION_SET_DB_ID;
                return queryId(uri, table,DataSetColumns.DB_ID,
                        projection, selection,selectionArgs, sortOrder);
            } */

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch (URI_MATCHER.match(uri)) {
            case DASHBOARDS:
                return insert(Dashboards.TABLE_NAME, values, uri);
            case DASHBOARD_ITEMS:
                return insert(DashboardItems.TABLE_NAME, values, uri);
            case INTERPRETATIONS:
                return insert(Interpretations.TABLE_NAME, values, uri);
            case REPORTS:
                return insert(Reports.TABLE_NAME, values, uri);
            case REPORT_GROUPS:
                return insert(ReportGroups.TABLE_NAME, values, uri);
            case REPORT_FIELDS:
                return insert(ReportFields.TABLE_NAME, values, uri);
            case OPTION_SETS:
                return insert(OptionSets.TABLE_NAME, values, uri);
            case OPTIONS:
                return insert(Options.TABLE_NAME, values, uri);
            case FIELDS:
                return insert(Fields.TABLE_NAME, values, uri);
            case GROUPS:
                return insert(Groups.TABLE_NAME, values, uri);
            case ORGANIZATION_UNITS:
                return insert(OrganizationUnits.TABLE_NAME, values, uri);
            case DATASETS:
                return insert(DataSets.TABLE_NAME, values, uri);
            case USER_ACCOUNT_FIELDS: {
                return insert(UserAccountFields.TABLE_NAME, values, uri);
            }
            default:
                throw new IllegalArgumentException("Unsupported URI for insertion: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        switch (URI_MATCHER.match(uri)) {
            case DASHBOARDS: {
                return delete(uri, Dashboards.TABLE_NAME, selection, selectionArgs);
            }
            case DASHBOARD_ID: {
                return deleteId(uri, Dashboards.TABLE_NAME,
                        Dashboards.DB_ID, selection, selectionArgs);
            }
            case DASHBOARD_ITEMS: {
                return delete(uri, DashboardItems.TABLE_NAME, selection, selectionArgs);
            }
            case DASHBOARD_ITEM_ID: {
                return deleteId(uri, DashboardItems.TABLE_NAME,
                        DashboardItems.DB_ID, selection, selectionArgs);
            }
            case INTERPRETATIONS: {
                return delete(uri, Interpretations.TABLE_NAME, selection, selectionArgs);
            }
            case INTERPRETATIONS_ID: {
                return deleteId(uri, Interpretations.TABLE_NAME,
                        Interpretations.DB_ID, selection, selectionArgs);
            }
            case REPORTS: {
                return delete(uri, Reports.TABLE_NAME, selection, selectionArgs);
            }
            case REPORT_ID: {
                return deleteId(uri, Reports.TABLE_NAME,
                        Reports.DB_ID, selection, selectionArgs);
            }
            case REPORT_GROUPS: {
                return delete(uri, ReportGroups.TABLE_NAME, selection, selectionArgs);
            }
            case REPORT_GROUP_ID: {
                return deleteId(uri, ReportGroups.TABLE_NAME,
                        ReportGroups.DB_ID, selection, selectionArgs);
            }
            case REPORT_FIELDS: {
                return delete(uri, ReportFields.TABLE_NAME, selection, selectionArgs);
            }
            case REPORT_FIELD_ID: {
                return deleteId(uri, ReportFields.TABLE_NAME,
                        ReportFields.DB_ID, selection, selectionArgs);
            }
            case OPTION_SETS: {
                return delete(uri, OptionSets.TABLE_NAME, selection, selectionArgs);
            }
            case OPTION_SET_ID: {
                return deleteId(uri, OptionSets.TABLE_NAME,
                        OptionSets.DB_ID, selection, selectionArgs);
            }
            case OPTIONS: {
                return delete(uri, Options.TABLE_NAME, selection, selectionArgs);
            }
            case OPTION_ID: {
                return deleteId(uri, Options.TABLE_NAME,
                        Options.DB_ID, selection, selectionArgs);
            }
            case FIELDS: {
                return delete(uri, Fields.TABLE_NAME, selection, selectionArgs);
            }
            case FIELD_ID: {
                return deleteId(uri, Fields.TABLE_NAME,
                        Fields.DB_ID, selection, selectionArgs);
            }
            case GROUPS: {
                return delete(uri, Groups.TABLE_NAME, selection, selectionArgs);
            }
            case GROUP_ID: {
                return deleteId(uri, Groups.TABLE_NAME,
                        Groups.DB_ID, selection, selectionArgs);
            }
            case ORGANIZATION_UNITS: {
                return delete(uri, OrganizationUnits.TABLE_NAME, selection, selectionArgs);
            }
            case ORGANIZATION_UNIT_ID: {
                return deleteId(uri, OrganizationUnits.TABLE_NAME,
                        OrganizationUnits.DB_ID, selection, selectionArgs);
            }
            case DATASETS: {
                return delete(uri, DataSets.TABLE_NAME, selection, selectionArgs);
            }
            case DATASET_ID: {
                return deleteId(uri, DataSets.TABLE_NAME,
                        DataSets.DB_ID, selection, selectionArgs);
            }
            case USER_ACCOUNT_FIELDS: {
                return delete(uri, UserAccountFields.TABLE_NAME, selection, selectionArgs);
            }
            case USER_ACCOUNT_FIELD_ID: {
                return deleteId(uri, UserAccountFields.TABLE_NAME,
                        UserAccountFields.DB_ID, selection, selectionArgs);
            }
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values,
                      String selection, String[] selectionArgs) {

        switch (URI_MATCHER.match(uri)) {
            case DASHBOARDS: {
                return update(uri, Dashboards.TABLE_NAME,
                        selection, selectionArgs, values);
            }
            case DASHBOARD_ID: {
                return updateId(uri, Dashboards.TABLE_NAME,
                        Dashboards.DB_ID, selection, selectionArgs, values);
            }
            case DASHBOARD_ITEMS: {
                return update(uri, DashboardItems.TABLE_NAME,
                        selection, selectionArgs, values);
            }
            case DASHBOARD_ITEM_ID: {
                return updateId(uri, DashboardItems.TABLE_NAME,
                        DashboardItems.DB_ID, selection, selectionArgs, values);
            }
            case INTERPRETATIONS: {
                return update(uri, Interpretations.TABLE_NAME,
                        selection, selectionArgs, values);
            }
            case INTERPRETATIONS_ID: {
                return updateId(uri, Interpretations.TABLE_NAME,
                        Interpretations.DB_ID, selection, selectionArgs, values);
            }
            case REPORTS: {
                return update(uri, Reports.TABLE_NAME,
                        selection, selectionArgs, values);
            }
            case REPORT_ID: {
                return updateId(uri, Reports.TABLE_NAME,
                        Reports.DB_ID, selection, selectionArgs, values);
            }
            case REPORT_GROUPS: {
                return update(uri, ReportGroups.TABLE_NAME,
                        selection, selectionArgs, values);
            }
            case REPORT_GROUP_ID: {
                return updateId(uri, ReportGroups.TABLE_NAME,
                        ReportGroups.DB_ID, selection, selectionArgs, values);
            }
            case REPORT_FIELDS: {
                return update(uri, ReportFields.TABLE_NAME,
                        selection, selectionArgs, values);
            }
            case REPORT_FIELD_ID: {
                return updateId(uri, ReportFields.TABLE_NAME,
                        ReportFields.DB_ID, selection, selectionArgs, values);
            }
            case OPTION_SETS: {
                return update(uri, OptionSets.TABLE_NAME,
                        selection, selectionArgs, values);
            }
            case OPTION_SET_ID: {
                return updateId(uri, OptionSets.TABLE_NAME,
                        OptionSets.DB_ID, selection, selectionArgs, values);
            }
            case OPTIONS: {
                return update(uri, Options.TABLE_NAME,
                        selection, selectionArgs, values);
            }
            case OPTION_ID: {
                return updateId(uri, Options.TABLE_NAME,
                        Options.DB_ID, selection, selectionArgs, values);
            }
            case FIELDS: {
                return update(uri, Fields.TABLE_NAME,
                        selection, selectionArgs, values);
            }
            case FIELD_ID: {
                return updateId(uri, Fields.TABLE_NAME,
                        Fields.DB_ID, selection, selectionArgs, values);
            }
            case GROUPS: {
                return update(uri, Groups.TABLE_NAME,
                        selection, selectionArgs, values);
            }
            case GROUP_ID: {
                return updateId(uri, Groups.TABLE_NAME,
                        Groups.DB_ID, selection, selectionArgs, values);
            }
            case ORGANIZATION_UNITS: {
                return update(uri, OrganizationUnits.TABLE_NAME,
                        selection, selectionArgs, values);
            }
            case ORGANIZATION_UNIT_ID: {
                return updateId(uri, OrganizationUnits.TABLE_NAME,
                        OrganizationUnits.DB_ID, selection, selectionArgs, values);
            }
            case DATASETS: {
                return update(uri, DataSets.TABLE_NAME,
                        selection, selectionArgs, values);
            }
            case DATASET_ID: {
                return updateId(uri, DataSets.TABLE_NAME,
                        DataSets.DB_ID, selection, selectionArgs, values);
            }
            case USER_ACCOUNT_FIELDS: {
                return update(uri, UserAccountFields.TABLE_NAME,
                        selection, selectionArgs, values);
            }
            case USER_ACCOUNT_FIELD_ID: {
                return updateId(uri, UserAccountFields.TABLE_NAME,
                        UserAccountFields.DB_ID, selection, selectionArgs, values);
            }
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    private Cursor query(Uri uri, String tableName, String[] projection,
                         String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();
        qBuilder.setTables(tableName);

        Cursor cursor = qBuilder.query(
                db, projection, selection, selectionArgs, null, null, sortOrder
        );

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    private Cursor queryId(Uri uri, String tableName, String colId, String[] projection,
                           String selection, String[] selectionArgs, String sortOrder, String id) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();

        qBuilder.setTables(tableName);
        qBuilder.appendWhere(colId + " = " + id);

        Cursor cursor = qBuilder.query(
                db, projection, selection, selectionArgs, null, null, sortOrder
        );

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    private Uri insert(String tableName, ContentValues values, Uri uri) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        long id = db.insertOrThrow(tableName, null, values);
        if (!isInBatchMode()) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return withAppendedId(uri, id);
    }

    private int delete(Uri uri, String tableName,
                       String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int count = db.delete(tableName, selection, selectionArgs);
        if (count > 0 && !isInBatchMode()) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    private int deleteId(Uri uri, String tableName, String colId,
                         String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        long id = parseId(uri);
        String where = colId + " = " + id;
        if (!isEmpty(selection)) {
            where += " AND " + selection;
        }

        int count = db.delete(tableName, where, selectionArgs);
        if (count > 0 && !isInBatchMode()) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    private int update(Uri uri, String tableName, String selection,
                       String[] selectionArgs, ContentValues values) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int count = db.update(tableName, values, selection, selectionArgs);
        if (count > 0 && !isInBatchMode()) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    private int updateId(Uri uri, String tableName, String colId,
                         String selection, String[] selectionArgs,
                         ContentValues values) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        long id = parseId(uri);
        String where = colId + " = " + id;
        if (!isEmpty(selection)) {
            where += " AND " + selection;
        }

        int count = db.update(tableName, values, where, selectionArgs);
        if (count > 0 && !isInBatchMode()) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        final Set<Uri> contentUris = new HashSet<>();
        final SQLiteDatabase db = mDBHelper.getWritableDatabase();

        mIsInBatchMode.set(true);
        db.beginTransaction();
        try {
            final int numOperations = operations.size();
            final ContentProviderResult[] results = new ContentProviderResult[numOperations];

            for (int i = 0; i < numOperations; i++) {
                contentUris.add(operations.get(i).getUri());
                results[i] = operations.get(i).apply(this, results, i);
            }
            db.setTransactionSuccessful();
            return results;
        } finally {
            db.endTransaction();
            mIsInBatchMode.remove();
            ContentResolver resolver = getContext().getContentResolver();
            for (Uri uri : contentUris) {
                resolver.notifyChange(uri, null);
            }
        }
    }

    private boolean isInBatchMode() {
        return mIsInBatchMode.get() != null && mIsInBatchMode.get();
    }
}