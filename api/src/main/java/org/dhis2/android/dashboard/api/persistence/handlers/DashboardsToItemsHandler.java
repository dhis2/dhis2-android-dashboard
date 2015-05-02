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
import org.dhis2.android.dashboard.api.persistence.models.DashboardToItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.dhis2.android.dashboard.api.utils.Preconditions.isNull;

public final class DashboardsToItemsHandler implements IModelHandler<DashboardToItem> {
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

    private static ContentValues toContentValues(DashboardToItem dashboardToItem) {
        isNull(dashboardToItem, "DashboardToItem object must not be null");

        ContentValues values = new ContentValues();
        values.put(DashboardsToItems.DASHBOARD_ID,
                dashboardToItem.getDashboardId());
        values.put(DashboardsToItems.DASHBOARD_ITEM_ID,
                dashboardToItem.getDashboardItemId());
        return values;
    }

    private static DashboardToItem fromCursor(Cursor cursor) {
        isNull(cursor, "Cursor object must not be null");
        return new DashboardToItem(
                cursor.getLong(ID),
                cursor.getString(DASHBOARD_ID),
                cursor.getString(DASHBOARD_ITEM_ID)
        );
    }


    // TODO appropriate handling of cursor
    @Override public List<DashboardToItem> map(Cursor cursor, boolean close) {
        List<DashboardToItem> entries = new ArrayList<>();

        try {
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();

                do {
                    entries.add(fromCursor(cursor));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null && close) {
                cursor.close();
            }
        }
        return entries;
    }

    @Override public String[] getProjection() {
        return PROJECTION;
    }

    @Override public ContentProviderOperation insert(DashboardToItem item) {
        isNull(item, "DashboardToItem object must not be null");

        Log.v(TAG, "Inserting dashboardItem " + "[" + item.getDashboardItemId() + "]"
                + " for dashboard " + "[" + item.getDashboardId() + "]");
        return ContentProviderOperation
                .newInsert(DashboardsToItems.CONTENT_URI)
                .withValues(toContentValues(item))
                .build();
    }

    @Override public ContentProviderOperation update(DashboardToItem object) {
        throw new IllegalArgumentException("No implementation");
    }

    @Override public ContentProviderOperation delete(DashboardToItem item) {
        isNull(item, "DashboardToItem object must not be null");

        Log.v(TAG, "Deleting: " + "[" + item.getDashboardId() + ":" + item.getDashboardItemId() + "]");
        Uri uri = ContentUris.withAppendedId(
                DashboardsToItems.CONTENT_URI, item.getId());
        return ContentProviderOperation
                .newDelete(uri)
                .build();
    }

    @Override public <T> List<T> queryRelatedModels(Class<T> clazz, Object id) {
        throw new IllegalArgumentException("Unsupported method");
    }

    @Override public List<DashboardToItem> query(String selection, String[] args) {
        Cursor cursor = mContext.getContentResolver().query(
                DashboardsToItems.CONTENT_URI, PROJECTION, selection, args, null
        );
        return map(cursor, true);
    }

    @Override public List<DashboardToItem> query() {
        return query(null, null);
    }

    @Override public List<ContentProviderOperation> sync(List<DashboardToItem> items) {
        isNull(items, "List<DashboardToItem> object must not be null");
        List<ContentProviderOperation> ops = new ArrayList<>();

        Map<String, DashboardToItem> newRelations = toMap(items);
        Map<String, DashboardToItem> oldRelations = toMap(query());

        for (String oldRelationKey : oldRelations.keySet()) {
            DashboardToItem oldRelation = oldRelations.get(oldRelationKey);
            String key = oldRelation.getDashboardId() +
                    oldRelation.getDashboardItemId();
            DashboardToItem newRelation = newRelations.get(key);

            if (newRelation == null) {
                ops.add(delete(oldRelation));
                continue;
            }

            newRelations.remove(key);
        }

        for (String newItemKey : newRelations.keySet()) {
            ops.add(insert(newRelations.get(newItemKey)));
        }

        return ops;
    }

    private static Map<String, DashboardToItem> toMap(List<DashboardToItem> items) {
        Map<String, DashboardToItem> map = new HashMap<>();
        if (items != null && !items.isEmpty()) {
            for (DashboardToItem item : items) {
                String key = item.getDashboardId() +
                        item.getDashboardItemId();
                map.put(key, item);
            }
        }
        return map;
    }
}