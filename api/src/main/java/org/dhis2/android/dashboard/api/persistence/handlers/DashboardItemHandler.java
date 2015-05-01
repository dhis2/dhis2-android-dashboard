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

import org.dhis2.android.dashboard.api.persistence.database.DbContract.DashboardItems;
import org.dhis2.android.dashboard.api.persistence.models.Access;
import org.dhis2.android.dashboard.api.persistence.models.DashboardItem;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.text.TextUtils.isEmpty;
import static org.dhis2.android.dashboard.api.utils.DbUtils.toMap;
import static org.dhis2.android.dashboard.api.utils.Preconditions.isNull;

public final class DashboardItemHandler implements IDbHandler<DashboardItem> {
    private static final String TAG = DashboardItemHandler.class.getSimpleName();

    public static final String[] PROJECTION = {
            DashboardItems.TABLE_NAME + "." + DashboardItems.ID,
            DashboardItems.TABLE_NAME + "." + DashboardItems.CREATED,
            DashboardItems.TABLE_NAME + "." + DashboardItems.LAST_UPDATED,
            DashboardItems.TABLE_NAME + "." + DashboardItems.TYPE,
            DashboardItems.TABLE_NAME + "." + DashboardItems.SHAPE,
            DashboardItems.TABLE_NAME + "." + DashboardItems.CONTENT_COUNT,
            DashboardItems.TABLE_NAME + "." + DashboardItems.DELETE,
            DashboardItems.TABLE_NAME + "." + DashboardItems.EXTERNALIZE,
            DashboardItems.TABLE_NAME + "." + DashboardItems.MANAGE,
            DashboardItems.TABLE_NAME + "." + DashboardItems.READ,
            DashboardItems.TABLE_NAME + "." + DashboardItems.UPDATE,
            DashboardItems.TABLE_NAME + "." + DashboardItems.WRITE
    };

    private static final int ID = 0;
    private static final int CREATED = 1;
    private static final int LAST_UPDATED = 2;
    private static final int TYPE = 3;
    private static final int SHAPE = 4;
    private static final int CONTENT_COUNT = 5;
    private static final int DELETE = 6;
    private static final int EXTERNALIZE = 7;
    private static final int MANAGE = 8;
    private static final int READ = 9;
    private static final int UPDATE = 10;
    private static final int WRITE = 11;

    private Context mContext;

    public DashboardItemHandler(Context context) {
        mContext = isNull(context, "Context object must not be null");
    }

    private static ContentValues toContentValues(DashboardItem item) {
        isNull(item, "DashboardItem object must not be null");

        String created = item.getCreated().toString();
        String lastUpdated = item.getLastUpdated().toString();
        Access access = item.getAccess();

        ContentValues values = new ContentValues();
        values.put(DashboardItems.ID, item.getId());
        values.put(DashboardItems.CREATED, created);
        values.put(DashboardItems.LAST_UPDATED, lastUpdated);
        values.put(DashboardItems.TYPE, item.getType());
        values.put(DashboardItems.SHAPE, item.getShape());
        values.put(DashboardItems.CONTENT_COUNT, item.getContentCount());
        values.put(DashboardItems.DELETE, access.isDelete() ? 1 : 0);
        values.put(DashboardItems.EXTERNALIZE, access.isExternalize() ? 1 : 0);
        values.put(DashboardItems.MANAGE, access.isManage() ? 1 : 0);
        values.put(DashboardItems.READ, access.isRead() ? 1 : 0);
        values.put(DashboardItems.UPDATE, access.isUpdate() ? 1 : 0);
        values.put(DashboardItems.WRITE, access.isWrite() ? 1 : 0);
        return values;
    }

    private static DashboardItem fromCursor(Cursor cursor) {
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

        DashboardItem item = new DashboardItem();
        item.setId(cursor.getString(ID));
        item.setCreated(created);
        item.setLastUpdated(lastUpdated);
        item.setType(cursor.getString(TYPE));
        item.setShape(cursor.getString(SHAPE));
        item.setContentCount(cursor.getInt(CONTENT_COUNT));
        item.setAccess(access);

        return item;
    }

    public static List<DashboardItem> map(Cursor cursor, boolean closeCursor) {
        List<DashboardItem> items = new ArrayList<>();
        try {
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    items.add(fromCursor(cursor));
                } while (cursor.moveToNext());

                if (closeCursor) {
                    cursor.close();
                }
            }
        } finally {
            if (cursor != null && closeCursor) {
                cursor.close();
            }
        }
        return items;
    }

    private static void insert(List<ContentProviderOperation> ops,
                               DashboardItem item) {
        isNull(item, "DashboardItem must not be null");

        if (isItemComplete(item)) {
            Log.d(TAG, "Inserting " + item.getId());
            ops.add(ContentProviderOperation
                    .newInsert(DashboardItems.CONTENT_URI)
                    .withValues(toContentValues(item))
                    .build());
        }
    }

    private static void update(List<ContentProviderOperation> ops,
                               DashboardItem item) {
        isNull(item, "DashboardItem must not be null");

        if (isItemComplete(item)) {
            Log.d(TAG, "Updating " + item.getName());
            Uri uri = DashboardItems.CONTENT_URI.buildUpon()
                    .appendPath(item.getId()).build();
            ops.add(ContentProviderOperation
                    .newUpdate(uri)
                    .withValues(toContentValues(item))
                    .build());
        }
    }

    private static void delete(List<ContentProviderOperation> ops,
                               DashboardItem item) {
        isNull(item, "DashboardItem must not be null");

        Log.d(TAG, "Deleting " + item.getName());
        Uri uri = DashboardItems.CONTENT_URI.buildUpon()
                .appendPath(item.getId()).build();
        ops.add(ContentProviderOperation
                .newDelete(uri)
                .build());
    }

    public static boolean isItemComplete(DashboardItem item) {
        return item != null && !(isEmpty(item.getId()) ||
                isEmpty(item.getType()) ||
                isEmpty(item.getShape()) ||
                item.getAccess() == null);
    }

    @Override public ContentProviderOperation insert(DashboardItem item) {
        isNull(item, "DashboardItem must not be null");

        if (isItemComplete(item)) {
            Log.d(TAG, "Inserting " + item.getId());
            return ContentProviderOperation
                    .newInsert(DashboardItems.CONTENT_URI)
                    .withValues(toContentValues(item))
                    .build();
        } else {
            return null;
        }
    }

    @Override public ContentProviderOperation update(DashboardItem item) {
        isNull(item, "DashboardItem must not be null");

        if (isItemComplete(item)) {
            Log.d(TAG, "Updating " + item.getName());
            Uri uri = DashboardItems.CONTENT_URI.buildUpon()
                    .appendPath(item.getId()).build();
            return ContentProviderOperation
                    .newUpdate(uri)
                    .withValues(toContentValues(item))
                    .build();
        } else {
            return null;
        }
    }

    @Override public ContentProviderOperation delete(DashboardItem item) {
            isNull(item, "DashboardItem must not be null");

            Log.d(TAG, "Deleting " + item.getName());
            Uri uri = DashboardItems.CONTENT_URI.buildUpon()
                    .appendPath(item.getId()).build();
            return ContentProviderOperation
                    .newDelete(uri)
                    .build();
    }

    @Override public List<DashboardItem> query(String selection, String[] selectionArgs) {
        return query(selection);
    }

    @Override public List<DashboardItem> query() {
        return query(null);
    }

    public List<DashboardItem> query(String selection) {
        Cursor cursor = mContext.getContentResolver().query(
                DashboardItems.CONTENT_URI, PROJECTION, selection, null, null
        );

        return map(cursor, true);
    }

    public List<ContentProviderOperation> sync(List<DashboardItem> items) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        Map<String, DashboardItem> newItems = toMap(items);
        Map<String, DashboardItem> oldItems = toMap(query());

        for (String oldItemKey : oldItems.keySet()) {
            DashboardItem newItem = newItems.get(oldItemKey);
            DashboardItem oldItem = oldItems.get(oldItemKey);

            if (newItem == null) {
                delete(ops, oldItem);
                continue;
            }

            if (newItem.getLastUpdated().isAfter(oldItem.getLastUpdated())) {
                update(ops, newItem);
            }

            newItems.remove(oldItemKey);
        }

        for (String newItemKey : newItems.keySet()) {
            DashboardItem item = newItems.get(newItemKey);
            insert(ops, item);
        }

        return ops;
    }
}