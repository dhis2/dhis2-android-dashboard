package org.hisp.dhis.mobile.datacapture.io;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class DBContract {
    public static final String AUTHORITY = "org.hisp.dhis.mobile.datacapture.io.DBContentProvider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    static interface DashboardColumns {
        public static final String TABLE_NAME = "dashboardTable";
        public static final String DB_ID = BaseColumns._ID;
        public static final String ID = "id";
        public static final String CREATED = "created";
        public static final String LAST_UPDATED = "lastUpdated";
        public static final String ACCESS = "access";
        public static final String NAME = "name";
        public static final String ITEM_COUNT = "itemCount";
        public static final String STATE = "state";
    }

    static interface DashboardItemColumns {
        public static final String TABLE_NAME = "dashboardItemColumns";
        public static final String DB_ID = BaseColumns._ID;
        public static final String ID = "id";
        public static final String CREATED = "created";
        public static final String LAST_UPDATED = "lastUpdated";
        public static final String ACCESS = "access";
        public static final String TYPE = "type";
        public static final String CONTENT_COUNT = "contentCount";
        public static final String MESSAGES = "messages";
        public static final String USERS = "users";
        public static final String REPORTS = "reports";
        public static final String RESOURCES = "resources";
        public static final String REPORT_TABLES = "reportTables";
        public static final String CHART = "chart";
        public static final String EVENT_CHART = "eventChart";
        public static final String REPORT_TABLE = "reportTable";
        public static final String MAP = "map";
        public static final String DASHBOARD_DB_ID = "dashboardDBId";
        public static final String STATE = "state";
    }

    static interface InterpretationColumns {
        public static final String TABLE_NAME = "interpretationsTable";
        public static final String DB_ID = BaseColumns._ID;
        public static final String ID = "id";
        public static final String CREATED = "created";
        public static final String LAST_UPDATED = "lastUpdated";
        public static final String ACCESS = "access";
        public static final String TYPE = "type";
        public static final String NAME = "name";
        public static final String DISPLAY_NAME = "displayName";
        public static final String TEXT = "text";
        public static final String EXTERNAL_ACCESS = "externalAccess";
        public static final String MAP = "map";
        public static final String CHART = "chart";
        public static final String REPORT_TABLE = "reportTable";
        public static final String DATASET = "dataSet";
        public static final String ORGANIZATION_UNIT = "organisationUnit";
        public static final String PERIOD = "period";
        public static final String USER = "user";
        public static final String COMMENTS = "comments";
        public static final String STATE = "state";
    }

    static interface ReportColumns {
        public static final String TABLE_NAME = "reportTable";
        public static final String DB_ID = BaseColumns._ID;
        public static final String ORG_UNIT_ID = "orgUnitId";
        public static final String ORG_UNIT_LABEL = "orgUnitLabel";
        public static final String DATASET_ID = "dataSetId";
        public static final String DATASET_LABEL = "dataSetLabel";
        public static final String PERIOD = "period";
        public static final String PERIOD_LABEL = "periodLabel";
        public static final String COMPLETE_DATE = "completeDate";
        public static final String STATE = "state";
    }

    static interface ReportGroupColumns {
        public static final String TABLE_NAME = "reportsGroupTable";
        public static final String DB_ID = BaseColumns._ID;
        public static final String LABEL = "groupLabel";
        public static final String DATA_ELEMENT_COUNT = "dataElementCount";
        // ForeignKey to ReportColumns(_id)
        public static final String REPORT_DB_ID = "reportDBId";
    }

    static interface ReportFieldColumns {
        public static final String TABLE_NAME = "reportFieldsTable";
        public static final String DB_ID = BaseColumns._ID;
        public static final String LABEL = "label";
        public static final String TYPE = "type";
        public static final String DATA_ELEMENT = "dataElement";
        public static final String CATEGORY_OPTION_COMBO = "categoryOptionCombo";
        public static final String VALUE = "value";
        // ForeignKey to ReportGroupColumns(_id)
        public static final String GROUP_DB_ID = "reportGroupId";
        // ForeignKey to OptionSetColumns(_id)
        public static final String OPTION_SET = "optionSet";
    }

    static interface OrganizationUnitColumns {
        public static final String TABLE_NAME = "organizationUnitTable";
        public static final String DB_ID = BaseColumns._ID;
        public static final String ID = "id";
        public static final String LABEL = "label";
        public static final String LEVEL = "level";
        public static final String PARENT = "parent";
    }

    static interface DataSetColumns {
        public static final String TABLE_NAME = "dataSetTable";
        public static final String DB_ID = BaseColumns._ID;
        public static final String ID = "id";
        public static final String LABEL = "label";
        public static final String SUBTITLE = "subtitle";
        public static final String ALLOW_FUTURE_PERIODS = "allowFuturePeriods";
        public static final String EXPIRY_DAYS = "expiryDays";
        public static final String PERIOD_TYPE = "periodType";
        // ForeignKey to OrganizationUnitColumns(_id)
        public static final String ORGANIZATION_UNIT_DB_ID = "organizationUnitDBId";
    }

    static interface GroupColumns {
        public static final String TABLE_NAME = "dataSetGroupTable";
        public static final String DB_ID = BaseColumns._ID;
        public static final String LABEL = "label";
        public static final String DATA_ELEMENT_COUNT = "dataElementCount";
        // ForeignKey to DataSetColumns(_id)
        public static final String DATA_SET_DB_ID = "dataSetDBId";
    }

    static interface FieldColumns {
        public static final String TABLE_NAME = "fieldTable";
        public static final String DB_ID = BaseColumns._ID;
        public static final String LABEL = "label";
        public static final String TYPE = "type";
        public static final String DATA_ELEMENT = "dataElement";
        public static final String CATEGORY_OPTION_COMBO = "categoryOptionCombo";
        public static final String VALUE = "value";
        // ForeignKey to GroupColumns(_id)
        public static final String GROUP_DB_ID = "groupId";
        // ForeignKey to OptionSetColumns(_id)
        public static final String OPTION_SET = "optionSet";
    }

    static interface OptionSetColumns {
        public static final String TABLE_NAME = "optionSetTable";
        public static final String DB_ID = BaseColumns._ID;
        public static final String ID = "id";
        public static final String CREATED = "created";
        public static final String LAST_UPDATED = "lastUpdated";
        public static final String NAME = "name";
        public static final String DISPLAY_NAME = "displayName";
    }

    static interface UserAccountFieldColumns {
        public static final String TABLE_NAME = "userAccountFields";

        public static final String DB_ID = BaseColumns._ID;
        public static final String DATA_ELEMENT = "dataElement";
        public static final String TYPE = "type";
        public static final String VALUE = "value";
    }

    static interface OptionColumns {
        public static final String TABLE_NAME = "optionTable";
        public static final String DB_ID = BaseColumns._ID;
        public static final String ID = "id";
        public static final String CREATED = "created";
        public static final String LAST_UPDATED = "lastUpdated";
        public static final String NAME = "name";
        // ForeignKey to OptionSetColumns(_id)
        public static final String OPTION_SET_DB_ID = "optionSetDBId";
    }

    public static class Dashboards implements DashboardColumns {
        public static final String PATH = DashboardColumns.TABLE_NAME;
        public static final String DASHBOARDS = PATH;
        public static final String DASHBOARD_ID = PATH + "/#";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH);

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/org.hisp.dhis.mobile.datacapture.api.models.Dashboard";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/org.hisp.dhis.mobile.datacapture.api.models.Dashboard";
    }

    public static class DashboardItems implements DashboardItemColumns {
        public static final String PATH = DashboardItemColumns.TABLE_NAME;
        public static final String DASHBOARD_ITEMS = PATH;
        public static final String DASHBOARD_ITEM_ID = PATH + "/#";

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/org.hisp.dhis.mobile.datacapture.api.models.DashboardItem";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/org.hisp.dhis.mobile.datacapture.api.models.DashboardItem";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH);
    }

    public static class Interpretations implements InterpretationColumns {
        public static final String PATH = InterpretationColumns.TABLE_NAME;
        public static final String INTERPRETATIONS = PATH;
        public static final String INTERPRETATION_ID = PATH + "/#";

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/org.hisp.dhis.mobile.datacapture.api.models.Interpretation";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/org.hisp.dhis.mobile.datacapture.api.models.Interpretation";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH);
    }

    public static class Reports implements ReportColumns {
        public static final String PATH = ReportColumns.TABLE_NAME;
        public static final String REPORTS = PATH;
        public static final String REPORT_ID = PATH + "/#";
        public static final String REPORT_WITH_GROUPS = PATH + "/" + ReportGroups.PATH;
        // public static final String REPORT_ID_GROUPS = PATH + "/*/" + ReportGroups.PATH;

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/org.hisp.dhis.mobile.datacapture.api.models.Report";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/org.hisp.dhis.mobile.datacapture.api.models.Report";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH);

        private static final int REPORT_ID_POSITION = 1;

        public static Uri buildUriWithGroups() {
            return CONTENT_URI.buildUpon().appendPath(ReportGroups.PATH).build();
        }

        public static Uri buildReportUriWithGroups(long id) {
            String stringId = String.valueOf(id);
            return CONTENT_URI.buildUpon()
                    .appendPath(stringId)
                    .appendPath(ReportGroups.PATH)
                    .build();
        }

        public static long getReportIdFromUri(Uri uri) {
            String stringId = uri.getPathSegments().get(REPORT_ID_POSITION);
            return Long.parseLong(stringId);
        }
    }

    public static class ReportGroups implements ReportGroupColumns {
        public static final String PATH = ReportGroupColumns.TABLE_NAME;
        public static final String REPORT_GROUPS = PATH;
        public static final String REPORT_GROUP_ID = PATH + "/#";

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/org.hisp.dhis.mobile.datacapture.api.models.Group";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/org.hisp.dhis.mobile.datacapture.api.models.Group";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH);
    }

    public static class ReportFields implements ReportFieldColumns {
        public static final String PATH = ReportFieldColumns.TABLE_NAME;
        public static final String REPORT_FIELDS = PATH;
        public static final String REPORT_FIELD_ID = PATH + "/#";

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/org.hisp.dhis.mobile.datacapture.api.models.Field";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/org.hisp.dhis.mobile.datacapture.api.models.Field";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH);
    }

    public static class OrganizationUnits implements OrganizationUnitColumns {
        public static final String PATH = OrganizationUnitColumns.TABLE_NAME;
        public static final String ORGANIZATION_UNITS = PATH;
        public static final String ORGANIZATION_UNIT_ID = PATH + "/#";
        public static final String ORGANIZATION_UNITS_WITH_DATASETS = PATH + "/" + DataSets.PATH;
        // public static final String ORGANIZATION_UNIT_ID_DATASETS = PATH + "/*/" + DataSets.PATH;

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/org.hisp.dhis.mobile.datacapture.api.models.OrganisationUnit";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/org.hisp.dhis.mobile.datacapture.api.models.OrganisationUnit";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH);

        private static final int ORGANIZATION_UNIT_ID_POSITION = 1;

        public static Uri buildUriWithDataSets() {
            return CONTENT_URI.buildUpon().appendPath(DataSets.PATH).build();
        }

        public static Uri buildUriWithDataSets(long orgUnitID) {
            String stringId = String.valueOf(orgUnitID);
            return CONTENT_URI.buildUpon()
                    .appendPath(stringId)
                    .appendPath(DataSets.PATH)
                    .build();
        }

        public static long getIdFromUri(Uri uri) {
            String stringId = uri.getPathSegments().get(ORGANIZATION_UNIT_ID_POSITION);
            return Long.parseLong(stringId);
        }
    }

    public static class DataSets implements DataSetColumns {
        public static final String PATH = TABLE_NAME;
        public static final String DATASETS = PATH;
        public static final String DATASET_ID = PATH + "/#";

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/org.hisp.dhis.mobile.datacapture.api.models.DataSet";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/org.hisp.dhis.mobile.datacapture.api.models.DataSet";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH);
    }

    public static class Groups implements GroupColumns {
        public static final String PATH = TABLE_NAME;
        public static final String GROUPS = PATH;
        public static final String GROUP_ID = PATH + "/#";

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/org.hisp.dhis.mobile.datacapture.api.models.Group";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/org.hisp.dhis.mobile.datacapture.api.models.Group";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH);
    }

    public static class Fields implements FieldColumns {
        public static final String PATH = TABLE_NAME;
        public static final String FIELDS = PATH;
        public static final String FIELD_ID = PATH + "/#";

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/org.hisp.dhis.mobile.datacapture.api.models.Field";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/org.hisp.dhis.mobile.datacapture.api.models.Field";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH);
    }

    public static class OptionSets implements OptionSetColumns {
        public static final String PATH = TABLE_NAME;
        public static final String OPTION_SETS = PATH;
        public static final String OPTION_SET_ID = PATH + "/#";

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/org.hisp.dhis.mobile.datacapture.api.models.OptionSet";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/org.hisp.dhis.mobile.datacapture.api.models.OptionSet";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH);
    }

    public static class Options implements OptionColumns {
        public static final String PATH = TABLE_NAME;
        public static final String OPTIONS = PATH;
        public static final String OPTION_ID = PATH + "/#";

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/org.hisp.dhis.mobile.datacapture.api.models.Option";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/org.hisp.dhis.mobile.datacapture.api.models.Option";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH);
    }

    public static class UserAccountFields implements UserAccountFieldColumns {
        public static final String PATH = TABLE_NAME;
        public static final String FIELDS = PATH;
        public static final String FIELD_ID = PATH + "/#";

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/org.hisp.dhis.mobile.datacapture.api.models.Field";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/org.hisp.dhis.mobile.datacapture.api.models.Field";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH);
    }
}