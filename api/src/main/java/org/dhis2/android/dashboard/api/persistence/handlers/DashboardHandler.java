/*
 * Copyright (c) 2015, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.dhis2.android.dashboard.api.persistence.handlers;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import org.dhis2.android.dashboard.api.models.Access;
import org.dhis2.android.dashboard.api.models.Dashboard;
import org.dhis2.android.dashboard.api.models.DashboardItem;
import org.dhis2.android.dashboard.api.persistence.DbManager;
import org.dhis2.android.dashboard.api.persistence.database.DbContract.DashboardItems;
import org.dhis2.android.dashboard.api.persistence.database.DbContract.Dashboards;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.dhis2.android.dashboard.api.persistence.database.DbContract.Dashboards.buildUriWithItems;
import static org.dhis2.android.dashboard.api.utils.DbUtils.toMap;
import static org.dhis2.android.dashboard.api.utils.Preconditions.isNull;

public final class DashboardHandler implements IModelHandler<Dashboard> {
    private static final String TAG = DashboardHandler.class.getSimpleName();

    public static final String[] PROJECTION = {
            Dashboards.TABLE_NAME + "." + Dashboards.ID,
            Dashboards.TABLE_NAME + "." + Dashboards.CREATED,
            Dashboards.TABLE_NAME + "." + Dashboards.LAST_UPDATED,
            Dashboards.TABLE_NAME + "." + Dashboards.NAME,
            Dashboards.TABLE_NAME + "." + Dashboards.DISPLAY_NAME,
            Dashboards.TABLE_NAME + "." + Dashboards.ITEM_COUNT,
            Dashboards.TABLE_NAME + "." + Dashboards.DELETE,
            Dashboards.TABLE_NAME + "." + Dashboards.EXTERNALIZE,
            Dashboards.TABLE_NAME + "." + Dashboards.MANAGE,
            Dashboards.TABLE_NAME + "." + Dashboards.READ,
            Dashboards.TABLE_NAME + "." + Dashboards.UPDATE,
            Dashboards.TABLE_NAME + "." + Dashboards.WRITE
    };

    /* When two tables are joined sometimes we can get empty rows.
    For example dashboard does not contain any dashboard items.
    In order to avoid strange bugs during table JOINs,
    we explicitly state that we want only not null values  */
    private static final String NON_NULL_DASHBOARD_ITEMS = DashboardItems.TABLE_NAME + "." +
            DashboardItems.ID + " IS NOT NULL";

    private static final int ID = 0;
    private static final int CREATED = 1;
    private static final int LAST_UPDATED = 2;
    private static final int NAME = 3;
    private static final int DISPLAY_NAME = 4;
    private static final int ITEM_COUNT = 5;
    private static final int DELETE = 6;
    private static final int EXTERNALIZE = 7;
    private static final int MANAGE = 8;
    private static final int READ = 9;
    private static final int UPDATE = 10;
    private static final int WRITE = 11;

    private Context mContext;

    public DashboardHandler(Context context) {
        mContext = isNull(context, "Context object must not be null");
    }

    private static ContentValues toContentValues(Dashboard dashboard) {
        isNull(dashboard, "Dashboard object must not be null");

        String created = dashboard.getCreated().toString();
        String lastUpdated = dashboard.getLastUpdated().toString();
        Access access = dashboard.getAccess();

        ContentValues values = new ContentValues();
        values.put(Dashboards.ID, dashboard.getId());
        values.put(Dashboards.CREATED, created);
        values.put(Dashboards.LAST_UPDATED, lastUpdated);
        values.put(Dashboards.NAME, dashboard.getName());
        values.put(Dashboards.DISPLAY_NAME, dashboard.getDisplayName());
        values.put(Dashboards.ITEM_COUNT, dashboard.getItemCount());

        values.put(Dashboards.DELETE, access.isDelete() ? 1 : 0);
        values.put(Dashboards.EXTERNALIZE, access.isExternalize() ? 1 : 0);
        values.put(Dashboards.MANAGE, access.isManage() ? 1 : 0);
        values.put(Dashboards.READ, access.isRead() ? 1 : 0);
        values.put(Dashboards.UPDATE, access.isUpdate() ? 1 : 0);
        values.put(Dashboards.WRITE, access.isWrite() ? 1 : 0);
        return values;
    }

    private static Dashboard fromCursor(Cursor cursor) {
        isNull(cursor, "Cursor object must not be null");

        DateTime created = DateTime.parse(cursor.getString(CREATED));
        DateTime lastUpdated = DateTime.parse(cursor.getString(LAST_UPDATED));

        Access access = new Access();
        access.setDelete(cursor.getInt(DELETE) == 1);
        access.setExternalize(cursor.getInt(EXTERNALIZE) == 1);
        access.setManage(cursor.getInt(MANAGE) == 1);
        access.setRead(cursor.getInt(READ) == 1);
        access.setUpdate(cursor.getInt(UPDATE) == 1);
        access.setWrite(cursor.getInt(WRITE) == 1);

        Dashboard dashboard = new Dashboard();
        dashboard.setId(cursor.getString(ID));
        dashboard.setCreated(created);
        dashboard.setLastUpdated(lastUpdated);
        dashboard.setName(cursor.getString(NAME));
        dashboard.setDisplayName(cursor.getString(DISPLAY_NAME));
        dashboard.setItemCount(cursor.getInt(ITEM_COUNT));
        dashboard.setAccess(access);

        return dashboard;
    }

    @Override public List<Dashboard> map(Cursor cursor, boolean closeCursor) {
        List<Dashboard> units = new ArrayList<>();
        try {
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    units.add(fromCursor(cursor));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null && closeCursor) {
                cursor.close();
            }
        }
        return units;
    }

    @Override public String[] getProjection() {
        return PROJECTION;
    }

    @Override public ContentProviderOperation insert(Dashboard dashboard) {
        isNull(dashboard, "Dashboard must not be null");

        Log.v(TAG, "Inserting " + dashboard.getName());
        return ContentProviderOperation
                .newInsert(Dashboards.CONTENT_URI)
                .withValues(toContentValues(dashboard))
                .build();
    }

    @Override public ContentProviderOperation update(Dashboard dashboard) {
        isNull(dashboard, "Dashboard must not be null");

        Log.v(TAG, "Updating " + dashboard.getName());
        Uri uri = Dashboards.CONTENT_URI.buildUpon()
                .appendPath(dashboard.getId()).build();
        return ContentProviderOperation
                .newUpdate(uri)
                .withValues(toContentValues(dashboard))
                .build();
    }

    @Override public ContentProviderOperation delete(Dashboard dashboard) {
        isNull(dashboard, "Dashboard must not be null");

        Log.v(TAG, "Deleting " + dashboard.getName());
        Uri uri = Dashboards.CONTENT_URI.buildUpon()
                .appendPath(dashboard.getId()).build();
        return ContentProviderOperation
                .newDelete(uri)
                .build();
    }

    @Override public <T> List<T> queryRelatedModels(Class<T> clazz, Object id) {
        isNull(clazz, "Class object must not be null");
        isNull(id, "id must not be null");

        if (clazz == DashboardItem.class) {
            Cursor cursor = mContext.getContentResolver().query(
                    buildUriWithItems((String) id), DbManager.with(DashboardItem.class)
                            .getProjection(), NON_NULL_DASHBOARD_ITEMS, null, null
            );

            return (List<T>) DbManager.with(DashboardItem.class).map(cursor, true);
        } else {
            throw new IllegalArgumentException("Unsupported type");
        }
    }

    @Override public List<Dashboard> query(String selection, String[] args) {
        Cursor cursor = mContext.getContentResolver().query(
                Dashboards.CONTENT_URI, PROJECTION, selection, args, null
        );
        return map(cursor, true);
    }

    @Override public List<Dashboard> query() {
        return query(null, null);
    }

    @Override public List<ContentProviderOperation> sync(List<Dashboard> units) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        Map<String, Dashboard> newDashboards = toMap(units);
        Map<String, Dashboard> oldDashboards = toMap(query());

        for (String oldDashboardKey : oldDashboards.keySet()) {
            Dashboard newDashboard = newDashboards.get(oldDashboardKey);
            Dashboard oldDashboard = oldDashboards.get(oldDashboardKey);

            if (newDashboard == null) {
                ops.add(delete(oldDashboard));
                continue;
            }

            if (newDashboard.getLastUpdated().isAfter(oldDashboard.getLastUpdated())) {
                ops.add(update(newDashboard));
            }

            newDashboards.remove(oldDashboardKey);
        }

        for (String newDashboardKey : newDashboards.keySet()) {
            ops.add(insert(newDashboards.get(newDashboardKey)));
        }

        return ops;
    }
}