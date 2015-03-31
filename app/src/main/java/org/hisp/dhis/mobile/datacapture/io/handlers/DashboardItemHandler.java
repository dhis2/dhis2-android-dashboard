package org.hisp.dhis.mobile.datacapture.io.handlers;

import android.content.ContentProviderOperation;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.hisp.dhis.mobile.datacapture.api.android.models.DbRow;
import org.hisp.dhis.mobile.datacapture.api.android.models.State;
import org.hisp.dhis.mobile.datacapture.api.models.Access;
import org.hisp.dhis.mobile.datacapture.api.models.Dashboard;
import org.hisp.dhis.mobile.datacapture.api.models.DashboardItem;
import org.hisp.dhis.mobile.datacapture.api.models.DashboardItemElement;
import org.hisp.dhis.mobile.datacapture.api.models.User;
import org.hisp.dhis.mobile.datacapture.io.DBContract.DashboardItems;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.text.TextUtils.isEmpty;

public final class DashboardItemHandler {

    public static final String[] PROJECTION = {
            DashboardItems.DB_ID,
            DashboardItems.ID,
            DashboardItems.CREATED,
            DashboardItems.LAST_UPDATED,
            DashboardItems.ACCESS,
            DashboardItems.TYPE,
            DashboardItems.CONTENT_COUNT,
            DashboardItems.MESSAGES,
            DashboardItems.USERS,
            DashboardItems.REPORTS,
            DashboardItems.RESOURCES,
            DashboardItems.REPORT_TABLES,
            DashboardItems.CHART,
            DashboardItems.EVENT_CHART,
            DashboardItems.REPORT_TABLE,
            DashboardItems.MAP
    };

    private static final int DATABASE_ID = 0;
    private static final int ID = 1;
    private static final int CREATED = 2;
    private static final int LAST_UPDATED = 3;
    private static final int ACCESS = 4;
    private static final int TYPE = 5;
    private static final int CONTENT_COUNT = 6;
    private static final int MESSAGES = 7;
    private static final int USERS = 8;
    private static final int REPORTS = 9;
    private static final int RESOURCES = 10;
    private static final int REPORT_TABLES = 11;
    private static final int CHART = 12;
    private static final int EVENT_CHART = 13;
    private static final int REPORT_TABLE = 14;
    private static final int MAP = 15;

    private DashboardItemHandler() {
    }

    public static DbRow<DashboardItem> fromCursor(Cursor cursor) {
        if (cursor == null) {
            throw new IllegalArgumentException("Cursor object cannot be null");
        }

        Gson gson = new Gson();
        Access access = gson.fromJson(cursor.getString(ACCESS), Access.class);

        Type usersType = new TypeToken<List<User>>() { }.getType();
        Type dashboardType = new TypeToken<List<DashboardItemElement>>() { }.getType();

        List<User> users = gson.fromJson(cursor.getString(USERS), usersType);
        List<DashboardItemElement> reports = gson.fromJson(cursor.getString(REPORTS), dashboardType);
        List<DashboardItemElement> resources = gson.fromJson(cursor.getString(RESOURCES), dashboardType);
        List<DashboardItemElement> reportTables = gson.fromJson(cursor.getString(REPORT_TABLES), dashboardType);

        DashboardItemElement chart = gson.fromJson(cursor.getString(CHART), DashboardItemElement.class);
        DashboardItemElement eventChart = gson.fromJson(cursor.getString(EVENT_CHART), DashboardItemElement.class);
        DashboardItemElement reportTable = gson.fromJson(cursor.getString(REPORT_TABLE), DashboardItemElement.class);
        DashboardItemElement map = gson.fromJson(cursor.getString(MAP), DashboardItemElement.class);

        String created = cursor.getString(CREATED);
        String lastUpdated = cursor.getString(LAST_UPDATED);

        DashboardItem dashboardItem = new DashboardItem();

        dashboardItem.setId(cursor.getString(ID));
        dashboardItem.setCreated(created);
        dashboardItem.setLastUpdated(lastUpdated);
        dashboardItem.setAccess(access);
        dashboardItem.setType(cursor.getString(TYPE));
        dashboardItem.setContentCount(cursor.getInt(CONTENT_COUNT));
        dashboardItem.setMessages(cursor.getInt(MESSAGES) == 1);

        dashboardItem.setUsers(users);
        dashboardItem.setReports(reports);
        dashboardItem.setResources(resources);
        dashboardItem.setReportTables(reportTables);
        dashboardItem.setChart(chart);
        dashboardItem.setEventChart(eventChart);
        dashboardItem.setReportTable(reportTable);
        dashboardItem.setMap(map);

        DbRow<DashboardItem> holder = new DbRow<>();
        holder.setId(cursor.getInt(DATABASE_ID));
        holder.setItem(dashboardItem);
        return holder;
    }

    public static ContentValues toContentValues(DashboardItem dashboardItem) {
        if (dashboardItem == null) {
            throw new IllegalArgumentException("DashboardItem object cannot be null");
        }

        Gson gson = new Gson();
        ContentValues values = new ContentValues();

        String created = dashboardItem.getCreated();
        String lastUpdated = dashboardItem.getLastUpdated();

        String access = gson.toJson(dashboardItem.getAccess());
        String users = gson.toJson(dashboardItem.getUsers());
        String reports = gson.toJson(dashboardItem.getReports());
        String resources = gson.toJson(dashboardItem.getResources());
        String reportTables = gson.toJson(dashboardItem.getReportTables());
        String chart = gson.toJson(dashboardItem.getChart());
        String eventChart = gson.toJson(dashboardItem.getEventChart());
        String reportTable = gson.toJson(dashboardItem.getReportTable());
        String map = gson.toJson(dashboardItem.getMap());

        values.put(DashboardItems.ID, dashboardItem.getId());
        values.put(DashboardItems.CREATED, created);
        values.put(DashboardItems.LAST_UPDATED, lastUpdated);
        values.put(DashboardItems.ACCESS, access);
        values.put(DashboardItems.TYPE, dashboardItem.getType());
        values.put(DashboardItems.CONTENT_COUNT, dashboardItem.getContentCount());
        values.put(DashboardItems.MESSAGES, dashboardItem.isMessages() ? 1 : 0);
        values.put(DashboardItems.USERS, users);
        values.put(DashboardItems.REPORTS, reports);
        values.put(DashboardItems.RESOURCES, resources);
        values.put(DashboardItems.REPORT_TABLES, reportTables);
        values.put(DashboardItems.CHART, chart);
        values.put(DashboardItems.EVENT_CHART, eventChart);
        values.put(DashboardItems.REPORT_TABLE, reportTable);
        values.put(DashboardItems.MAP, map);

        return values;
    }

    public static ContentProviderOperation delete(DbRow<DashboardItem> dashboardItem) {
        Uri uri = ContentUris.withAppendedId(
                DashboardItems.CONTENT_URI, dashboardItem.getId()
        );
        return ContentProviderOperation.newDelete(uri).build();
    }

    public static ContentProviderOperation update(DbRow<DashboardItem> oldItem,
                                                  DashboardItem newItem) {
        if (isCorrect(newItem)) {
            Uri uri = ContentUris.withAppendedId(
                    DashboardItems.CONTENT_URI, oldItem.getId()
            );
            return ContentProviderOperation.newUpdate(uri)
                    .withValues(toContentValues(newItem)).build();
        } else {
            return null;
        }
    }

    public static ContentProviderOperation insert(DbRow<Dashboard> dashboard,
                                                  DashboardItem dashboardItem) {
        if (isCorrect(dashboardItem)) {
            return ContentProviderOperation.newInsert(DashboardItems.CONTENT_URI)
                    .withValue(DashboardItems.DASHBOARD_DB_ID, dashboard.getId())
                    .withValue(DashboardItems.STATE, State.GETTING.toString())
                    .withValues(toContentValues(dashboardItem))
                    .build();
        } else {
            return null;
        }
    }

    // This method is intended to work with an array of Dashboard items,
    // where dashboardIndex is the index of Dashboard to which we want to attach Dashboard Item
    public static ContentProviderOperation insertWithBackReference(int dashboardIndex,
                                                                   DashboardItem dashboardItem) {
        if (isCorrect(dashboardItem)) {
            return ContentProviderOperation.newInsert(DashboardItems.CONTENT_URI)
                    .withValueBackReference(DashboardItems.DASHBOARD_DB_ID, dashboardIndex)
                    .withValues(toContentValues(dashboardItem))
                    .withValue(DashboardItems.STATE, State.GETTING.toString())
                    .build();
        } else {
            return null;
        }
    }

    private static boolean isCorrect(DashboardItem dashboardItem) {
        return (dashboardItem != null && dashboardItem.getAccess() != null &&
                !isEmpty(dashboardItem.getId()) && !isEmpty(dashboardItem.getCreated()) &&
                !isEmpty(dashboardItem.getLastUpdated()) && !isEmpty(dashboardItem.getType()));
    }

    public static Map<String, DashboardItem> toMap(List<DashboardItem> dashboardItems) {
        Map<String, DashboardItem> dashboardItemMap = new HashMap<>();
        if (dashboardItems != null && dashboardItems.size() > 0) {
            for (DashboardItem dashboard : dashboardItems) {
                dashboardItemMap.put(dashboard.getId(), dashboard);
            }
        }
        return dashboardItemMap;
    }
}
