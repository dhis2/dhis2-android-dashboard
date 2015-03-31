package org.hisp.dhis.mobile.datacapture.io;

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

public interface DBSchema {

    /**
     * TODO Handle collisions with 'replace on conflict' operator
     */
    public static final String CREATE_DASHBOARD_TABLE = "CREATE TABLE " + Dashboards.TABLE_NAME + "(" +
            Dashboards.DB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            Dashboards.ID + " TEXT NOT NULL UNIQUE," +
            Dashboards.CREATED + " TEXT NOT NULL," +
            Dashboards.LAST_UPDATED + " TEXT NOT NULL," +
            Dashboards.STATE + " TEXT NOT NULL," +
            Dashboards.ACCESS + " TEXT NOT NULL," +
            Dashboards.NAME + " TEXT," +
            Dashboards.ITEM_COUNT + " INTEGER" + ")";

    public static final String DROP_DASHBOARD_TABLE = "DROP TABLE IF EXISTS " + Dashboards.TABLE_NAME;

    public static final String CREATE_DASHBOARD_ITEMS_TABLE = "CREATE TABLE " + DashboardItems.TABLE_NAME + "(" +
            DashboardItems.DB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            DashboardItems.DASHBOARD_DB_ID + " INTEGER NOT NULL," +
            DashboardItems.ID + " TEXT NOT NULL," +
            DashboardItems.CREATED + " TEXT NOT NULL," +
            DashboardItems.LAST_UPDATED + " TEXT NOT NULL," +
            DashboardItems.STATE + " TEXT NOT NULL," +
            DashboardItems.ACCESS + " TEXT NOT NULL," +
            DashboardItems.TYPE + " TEXT NOT NULL," +
            DashboardItems.CONTENT_COUNT + " INTEGER," +
            DashboardItems.MESSAGES + " INTEGER," +
            DashboardItems.USERS + " TEXT," +
            DashboardItems.REPORTS + " TEXT," +
            DashboardItems.RESOURCES + " TEXT," +
            DashboardItems.REPORT_TABLES + " TEXT," +
            DashboardItems.CHART + " TEXT," +
            DashboardItems.EVENT_CHART + " TEXT," +
            DashboardItems.REPORT_TABLE + " TEXT," +
            DashboardItems.MAP + " TEXT," +
            " FOREIGN KEY" + "(" + DashboardItems.DASHBOARD_DB_ID + ")" +
            " REFERENCES " + Dashboards.TABLE_NAME + "(" + Dashboards.DB_ID + ")" +
            " ON DELETE CASCADE" + ")";

    public static final String DROP_DASHBOARD_ITEMS_TABLE = "DROP TABLE IF EXISTS " + DashboardItems.TABLE_NAME;

    public static final String CREATE_INTERPRETATIONS_TABLE = "CREATE TABLE " + Interpretations.TABLE_NAME + "(" +
            Interpretations.DB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            Interpretations.ID + " TEXT NOT NULL UNIQUE," +
            Interpretations.CREATED + " TEXT NOT NULL," +
            Interpretations.LAST_UPDATED + " TEXT NOT NULL," +
            Interpretations.STATE + " TEXT NOT NULL," +
            Interpretations.ACCESS + " TEXT NOT NULL," +
            Interpretations.TYPE + " TEXT NOT NULL," +
            Interpretations.NAME + " TEXT," +
            Interpretations.DISPLAY_NAME + " TEXT," +
            Interpretations.TEXT + " TEXT," +
            Interpretations.EXTERNAL_ACCESS + " INTEGER," +
            Interpretations.MAP + " TEXT," +
            Interpretations.CHART + " TEXT," +
            Interpretations.REPORT_TABLE + " TEXT," +
            Interpretations.DATASET + " TEXT," +
            Interpretations.ORGANIZATION_UNIT + " TEXT," +
            Interpretations.PERIOD + " TEXT," +
            Interpretations.USER + " TEXT," +
            Interpretations.COMMENTS + " TEXT" + ")";

    public static final String DROP_INTERPRETATIONS_TABLE = "DROP TABLE IF EXISTS " + Interpretations.TABLE_NAME;

    public static final String CREATE_REPORTS_TABLE = "CREATE TABLE " + Reports.TABLE_NAME + "(" +
            Reports.DB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            Reports.ORG_UNIT_ID + " TEXT NOT NULL," +
            Reports.ORG_UNIT_LABEL + " TEXT," +
            Reports.DATASET_ID + " TEXT NOT NULL," +
            Reports.DATASET_LABEL + " TEXT," +
            Reports.PERIOD + " TEXT NOT NULL," +
            Reports.PERIOD_LABEL + " TEXT," +
            Reports.COMPLETE_DATE + " TEXT," +
            Reports.STATE + " TEXT NOT NULL," +
            " UNIQUE " + "(" +
            Reports.ORG_UNIT_ID + "," +
            Reports.DATASET_ID + "," +
            Reports.PERIOD +
            ")" +
            ")";

    public static final String DROP_REPORTS_TABLE = "DROP TABLE IF EXISTS " + Reports.TABLE_NAME;

    public static final String CREATE_REPORT_GROUP_TABLE = "CREATE TABLE " + ReportGroups.TABLE_NAME + "(" +
            ReportGroups.DB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            ReportGroups.LABEL + " TEXT NOT NULL," +
            ReportGroups.DATA_ELEMENT_COUNT + " INTEGER," +
            ReportGroups.REPORT_DB_ID + " INTEGER NOT NULL," +
            " FOREIGN KEY " + "(" + ReportGroups.REPORT_DB_ID + ")" +
            " REFERENCES " + Reports.TABLE_NAME + "(" + Reports.DB_ID + ")" +
            " ON DELETE CASCADE " + ")";

    public static final String DROP_REPORT_GROUP_TABLE = "DROP TABLE IF EXISTS " + ReportGroups.TABLE_NAME;

    public static final String CREATE_REPORT_FIELDS_TABLE = "CREATE TABLE " + ReportFields.TABLE_NAME + "(" +
            ReportFields.DB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            ReportFields.LABEL + " TEXT," +
            ReportFields.TYPE + " TEXT NOT NULL," +
            ReportFields.DATA_ELEMENT + " TEXT NOT NULL," +
            ReportFields.CATEGORY_OPTION_COMBO + " TEXT NOT NULL," +
            ReportFields.OPTION_SET + " TEXT," +
            ReportFields.VALUE + " TEXT," +
            ReportFields.GROUP_DB_ID + " INTEGER NOT NULL," +
            " FOREIGN KEY " + "(" + ReportFields.GROUP_DB_ID + ")" +
            " REFERENCES " + ReportGroups.TABLE_NAME + "(" + ReportGroups.DB_ID + ")" +
            " ON DELETE CASCADE " +
            " FOREIGN KEY " + "(" + ReportFields.OPTION_SET + ")" +
            " REFERENCES " + OptionSets.TABLE_NAME + "(" + OptionSets.ID + ")" +
            " ON DELETE CASCADE " + ")";

    public static final String DROP_REPORT_FIELDS_TABLE = "DROP TABLE IF EXISTS " + ReportFields.TABLE_NAME;


    public static final String CREATE_ORGANIZATION_UNIT_TABLE = "CREATE TABLE " + OrganizationUnits.TABLE_NAME + "(" +
            OrganizationUnits.DB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            OrganizationUnits.ID + " TEXT NOT NULL," +
            OrganizationUnits.LABEL + " TEXT," +
            OrganizationUnits.LEVEL + " INTEGER," +
            OrganizationUnits.PARENT + " TEXT" + ")";

    public static final String DROP_ORGANIZATION_UNIT_TABLE = "DROP TABLE IF EXISTS " + OrganizationUnits.TABLE_NAME;


    public static final String CREATE_DATASET_TABLE = "CREATE TABLE " + DataSets.TABLE_NAME + "(" +
            DataSets.DB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            DataSets.ID + " TEXT NOT NULL," +
            DataSets.LABEL + " TEXT," +
            DataSets.SUBTITLE + " TEXT," +
            DataSets.ALLOW_FUTURE_PERIODS + " INTEGER," +
            DataSets.EXPIRY_DAYS + " INTEGER," +
            DataSets.PERIOD_TYPE + " TEXT," +
            DataSets.ORGANIZATION_UNIT_DB_ID + " INTEGER NOT NULL," +
            " FOREIGN KEY " + "(" + DataSets.ORGANIZATION_UNIT_DB_ID + ")" +
            " REFERENCES " + OrganizationUnits.TABLE_NAME + "(" + OrganizationUnits.DB_ID + ")" +
            " ON DELETE CASCADE " + ")";

    public static final String DROP_DATASET_TABLE = "DROP TABLE IF EXISTS " + DataSets.TABLE_NAME;


    public static final String CREATE_GROUP_TABLE = "CREATE TABLE " + Groups.TABLE_NAME + "(" +
            Groups.DB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            Groups.LABEL + " TEXT," +
            Groups.DATA_ELEMENT_COUNT + " INTEGER," +
            Groups.DATA_SET_DB_ID + " INTEGER NOT NULL," +
            " FOREIGN KEY " + "(" + Groups.DATA_SET_DB_ID + ")" +
            " REFERENCES " + DataSets.TABLE_NAME + "(" + DataSets.DB_ID + ")" +
            " ON DELETE CASCADE " + ")";

    public static final String DROP_GROUP_TABLE = "DROP TABLE IF EXISTS " + Groups.TABLE_NAME;


    public static final String CREATE_FIELD_TABLE = "CREATE TABLE " + Fields.TABLE_NAME + "(" +
            Fields.DB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            Fields.LABEL + " TEXT," +
            Fields.TYPE + " TEXT NOT NULL," +
            Fields.DATA_ELEMENT + " TEXT NOT NULL," +
            Fields.CATEGORY_OPTION_COMBO + " TEXT NOT NULL," +
            Fields.VALUE + " TEXT," +
            Fields.GROUP_DB_ID + " INTEGER NOT NULL," +
            Fields.OPTION_SET + " TEXT," +
            " FOREIGN KEY " + "(" + Fields.GROUP_DB_ID + ")" +
            " REFERENCES " + Groups.TABLE_NAME + "(" + Groups.DB_ID + ")" +
            " ON DELETE CASCADE " +
            " FOREIGN KEY " + "(" + Fields.OPTION_SET + ")" +
            " REFERENCES " + OptionSets.TABLE_NAME + "(" + OptionSets.ID + ")" +
            " ON DELETE CASCADE " + ")";

    public static final String DROP_FIELD_TABLE = "DROP TABLE IF EXISTS " + Fields.TABLE_NAME;


    public static final String CREATE_OPTION_SET_TABLE = "CREATE TABLE " + OptionSets.TABLE_NAME + "(" +
            OptionSets.DB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            OptionSets.ID + " TEXT NOT NULL," +
            OptionSets.CREATED + " TEXT NOT NULL," +
            OptionSets.LAST_UPDATED + " TEXT NOT NULL," +
            OptionSets.NAME + " TEXT," +
            OptionSets.DISPLAY_NAME + " TEXT," +
            " UNIQUE " + "(" + OptionSets.ID + ")" + " ON CONFLICT REPLACE " + ")";

    public static final String DROP_OPTION_SET_TABLE = "DROP TABLE IF EXISTS " + OptionSets.TABLE_NAME;

    public static final String CREATE_OPTION_TABLE = "CREATE TABLE " + Options.TABLE_NAME + "(" +
            Options.DB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            Options.ID + " TEXT NOT NULL," +
            Options.CREATED + " TEXT NOT NULL," +
            Options.LAST_UPDATED + " TEXT NOT NULL," +
            Options.NAME + " TEXT," +
            Options.OPTION_SET_DB_ID + " INTEGER NOT NULL," +
            " FOREIGN KEY " + "(" + Options.OPTION_SET_DB_ID + ")" +
            " REFERENCES " + OptionSets.TABLE_NAME + "(" + OptionSets.DB_ID + ")" +
            " ON DELETE CASCADE " + ")";

    public static final String DROP_OPTION_TABLE = "DROP TABLE IF EXISTS " + Options.TABLE_NAME;

    public static final String CREATE_USER_ACCOUNT_FIELDS_TABLE = "CREATE TABLE " + UserAccountFields.TABLE_NAME + "(" +
            UserAccountFields.DB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            UserAccountFields.DATA_ELEMENT + " TEXT NOT NULL," +
            UserAccountFields.TYPE + " TEXT NOT NULL," +
            UserAccountFields.VALUE + " TEXT," +
            " UNIQUE " + "(" + UserAccountFields.DATA_ELEMENT + ")" + " ON CONFLICT REPLACE" + ")";

    public static final String DROP_USER_ACCOUNT_FIELDS_TABLE = "DROP TABLE IF EXISTS " + UserAccountFields.TABLE_NAME;
}
