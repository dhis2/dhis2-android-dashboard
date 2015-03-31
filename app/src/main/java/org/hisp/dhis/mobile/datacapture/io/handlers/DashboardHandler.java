package org.hisp.dhis.mobile.datacapture.io.handlers;

import android.content.ContentProviderOperation;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.google.gson.Gson;

import org.hisp.dhis.mobile.datacapture.api.android.models.DbRow;
import org.hisp.dhis.mobile.datacapture.api.android.models.State;
import org.hisp.dhis.mobile.datacapture.api.models.Access;
import org.hisp.dhis.mobile.datacapture.api.models.Dashboard;
import org.hisp.dhis.mobile.datacapture.io.DBContract.Dashboards;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.text.TextUtils.isEmpty;

public final class DashboardHandler {

    public static final String[] PROJECTION = {
            Dashboards.DB_ID,
            Dashboards.ID,
            Dashboards.CREATED,
            Dashboards.LAST_UPDATED,
            Dashboards.ACCESS,
            Dashboards.NAME,
            Dashboards.ITEM_COUNT
    };

    private static final int DB_ID = 0;
    private static final int ID = 1;
    private static final int CREATED = 2;
    private static final int LAST_UPDATED = 3;
    private static final int ACCESS = 4;
    private static final int NAME = 5;
    private static final int ITEM_COUNT = 6;

    private DashboardHandler() {
    }

    public static ContentValues toContentValues(Dashboard dashboard) {
        if (dashboard == null) {
            throw new IllegalArgumentException("Dashboard object cannot be null");
        }

        Gson gson = new Gson();
        ContentValues values = new ContentValues();

        String created = dashboard.getCreated();
        String lastUpdated = dashboard.getLastUpdated();
        String access = gson.toJson(dashboard.getAccess());

        values.put(Dashboards.ID, dashboard.getId());
        values.put(Dashboards.CREATED, created);
        values.put(Dashboards.LAST_UPDATED, lastUpdated);
        values.put(Dashboards.ACCESS, access);
        values.put(Dashboards.NAME, dashboard.getName());
        values.put(Dashboards.ITEM_COUNT, dashboard.getItemCount());

        return values;
    }

    public static DbRow<Dashboard> fromCursor(Cursor cursor) {
        if (cursor == null) {
            throw new IllegalArgumentException("Cursor object cannot be null");
        }

        Gson gson = new Gson();
        Dashboard dashboard = new Dashboard();

        String created = cursor.getString(CREATED);
        String lastUpdated = cursor.getString(LAST_UPDATED);
        Access access = gson.fromJson(cursor.getString(ACCESS), Access.class);

        dashboard.setId(cursor.getString(ID));
        dashboard.setCreated(created);
        dashboard.setLastUpdated(lastUpdated);
        dashboard.setAccess(access);
        dashboard.setName(cursor.getString(NAME));
        dashboard.setItemCount(cursor.getInt(ITEM_COUNT));

        DbRow<Dashboard> holder = new DbRow<>();
        holder.setId(cursor.getInt(DB_ID));
        holder.setItem(dashboard);
        return holder;
    }

    private static boolean isCorrect(Dashboard dashboard) {
        return (dashboard != null &&
                dashboard.getAccess() != null &&
                !isEmpty(dashboard.getId()) &&
                !isEmpty(dashboard.getCreated()) &&
                !isEmpty(dashboard.getLastUpdated()));
    }

    public static ContentProviderOperation delete(DbRow<Dashboard> dashboard) {
        Uri uri = ContentUris.withAppendedId(
                Dashboards.CONTENT_URI, dashboard.getId()
        );
        return ContentProviderOperation.newDelete(uri).build();
    }

    public static ContentProviderOperation update(DbRow<Dashboard> oldDashboard,
                                                  Dashboard newDashboard) {
        return update(oldDashboard, newDashboard, State.GETTING);
    }

    public static ContentProviderOperation update(DbRow<Dashboard> oldDashboard,
                                                  Dashboard newDashboard, State state) {
        if (isCorrect(newDashboard)) {
            Uri uri = ContentUris.withAppendedId(Dashboards.CONTENT_URI,
                    oldDashboard.getId());
            return ContentProviderOperation.newUpdate(uri)
                    .withValues(DashboardHandler.toContentValues(newDashboard))
                    .withValue(Dashboards.STATE, state.toString()).build();
        } else {
            return null;
        }
    }

    public static ContentProviderOperation insert(Dashboard dashboard) {
        if (isCorrect(dashboard)) {
            return ContentProviderOperation.newInsert(Dashboards.CONTENT_URI)
                    .withValue(Dashboards.STATE, State.GETTING.toString())
                    .withValues(DashboardHandler.toContentValues(dashboard))
                    .build();
        } else {
            return null;
        }
    }

    public static Map<String, Dashboard> toMap(List<Dashboard> dashboardList) {
        Map<String, Dashboard> dashboardMap = new HashMap<>();
        for (Dashboard dashboard : dashboardList) {
            dashboardMap.put(dashboard.getId(), dashboard);
        }
        return dashboardMap;
    }
}
