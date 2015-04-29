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
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import org.dhis2.android.dashboard.api.persistence.database.DbContract.DashboardsToItems;
import org.dhis2.android.dashboard.api.persistence.models.Dashboard;
import org.dhis2.android.dashboard.api.persistence.models.DashboardItem;
import org.dhis2.android.dashboard.api.persistence.models.DbRow;
import org.dhis2.android.dashboard.api.persistence.models.Entry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.dhis2.android.dashboard.api.persistence.database.DbContract.Dashboards.buildUriWithItems;
import static org.dhis2.android.dashboard.api.utils.Preconditions.isNull;

public final class DashboardsToItemsHandler {
    private static final String TAG = DashboardsToItemsHandler.class.getSimpleName();

    private static final String[] PROJECTION = new String[]{
            DashboardsToItems.ID,
            DashboardsToItems.DASHBOARD_ID,
            DashboardsToItems.DASHBOARD_ITEM_ID
    };

    private static final int ID = 0;
    private static final int DASHBOARD_ID = 1;
    private static final int DASHBOARD_ITEM_ID = 2;

    private final Context mContext;

    public DashboardsToItemsHandler(Context context) {
        mContext = context;
    }

    private static ContentValues toContentValues(String dashboardId, String itemId) {
        isNull(dashboardId, "Dashboard ID object must not be null");
        isNull(dashboardId, "DashboardItem ID object must not be null");

        ContentValues values = new ContentValues();
        values.put(DashboardsToItems.DASHBOARD_ID, dashboardId);
        values.put(DashboardsToItems.DASHBOARD_ITEM_ID, itemId);
        return values;
    }

    private static DbRow<Entry> fromCursor(Cursor cursor) {
        isNull(cursor, "Cursor object must not be null");
        return new DbRow<>(
                cursor.getLong(ID),
                new Entry(
                        cursor.getString(DASHBOARD_ID),
                        cursor.getString(DASHBOARD_ITEM_ID))
        );
    }

    private static List<DbRow<Entry>> map(Cursor cursor, boolean close) {
        List<DbRow<Entry>> entries = new ArrayList<>();

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            do {
                entries.add(fromCursor(cursor));
            } while (cursor.moveToNext());

            if (close) {
                cursor.close();
            }
        }
        return entries;
    }

    private void insert(List<ContentProviderOperation> ops,
                        String dashboardId, String itemId) {
        isNull(dashboardId, "Dashboard ID must not be null");
        isNull(itemId, "DashboardItem ID must not be null");

        Log.d(TAG, "Inserting dataset " + "[" + itemId + "]"
                + " for organisation unit " + "[" + dashboardId + "]");
        ops.add(ContentProviderOperation
                .newInsert(DashboardsToItems.CONTENT_URI)
                .withValues(toContentValues(dashboardId, itemId))
                .build());
    }

    private static void delete(List<ContentProviderOperation> ops,
                               DbRow<Entry> entry) {
        isNull(entry, "Entry object must not be null");

        Log.d(TAG, "Deleting: " + "[" + entry.item.first + ":" + entry.item.second + "]");
        Uri uri = ContentUris.withAppendedId(
                DashboardsToItems.CONTENT_URI, entry.id);
        ops.add(ContentProviderOperation
                .newDelete(uri)
                .build());
    }

    public List<DashboardItem> queryDashboardItems(String dashboardId) {
        isNull(dashboardId, "Dashboard ID must not be null");

        Cursor cursor = mContext.getContentResolver().query(
                buildUriWithItems(dashboardId), DashboardItemHandler.PROJECTION, null, null, null
        );
        return DashboardItemHandler.map(cursor, true);
    }

    public List<DbRow<Entry>> queryRelationShip() {
        Cursor cursor = mContext.getContentResolver().query(
                DashboardsToItems.CONTENT_URI, PROJECTION, null, null, null
        );
        return map(cursor, true);
    }

    private Set<String> buildRelationShipSet(List<Entry> entries) {
        Set<String> set = new HashSet<>();
        for (Entry entry : entries) {
            set.add(entry.first + entry.second);
        }
        return set;
    }

    private Map<String, Entry> buildDashboardToItemMap(
            List<Dashboard> dashboards) {
        Map<String, Entry> map = new HashMap<>();
        if (dashboards != null && !dashboards.isEmpty()) {
            for (Dashboard dashboard : dashboards) {
                if (dashboard.getDashboardItems() == null
                        || dashboard.getDashboardItems().isEmpty()) {
                    continue;
                }

                for (DashboardItem item : dashboard.getDashboardItems()) {
                    map.put(dashboard.getId() + item.getId(),
                            new Entry(dashboard.getId(), item.getId()));
                }
            }
        }
        return map;
    }

    public List<ContentProviderOperation> sync(List<Dashboard> dashboards) {
        isNull(dashboards, "List<Dashboard> object must not be null");
        List<ContentProviderOperation> ops = new ArrayList<>();

        Map<String, Entry> newRelations
                = buildDashboardToItemMap(dashboards);
        List<DbRow<Entry>> oldRelations = queryRelationShip();

        for (DbRow<Entry> oldRelation : oldRelations) {
            String key = oldRelation.item.first + oldRelation.item.second;
            Entry newRelation = newRelations.get(key);

            if (newRelation == null) {
                delete(ops, oldRelation);
                continue;
            }

            newRelations.remove(key);
        }

        for (String newItemKey : newRelations.keySet()) {
            Entry item = newRelations.get(newItemKey);
            insert(ops, item.first, item.second);
        }

        return ops;
    }
}