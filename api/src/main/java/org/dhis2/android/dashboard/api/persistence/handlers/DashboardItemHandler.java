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

import com.fasterxml.jackson.core.type.TypeReference;

import org.dhis2.android.dashboard.api.models.Access;
import org.dhis2.android.dashboard.api.models.DashboardElement;
import org.dhis2.android.dashboard.api.models.DashboardItem;
import org.dhis2.android.dashboard.api.persistence.database.DbContract.DashboardItems;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.text.TextUtils.isEmpty;
import static org.dhis2.android.dashboard.api.utils.DbUtils.toMap;
import static org.dhis2.android.dashboard.api.utils.JsonUtils.fromJson;
import static org.dhis2.android.dashboard.api.utils.JsonUtils.toJson;
import static org.dhis2.android.dashboard.api.utils.Preconditions.isNull;

public final class DashboardItemHandler implements IModelHandler<DashboardItem> {
    private static final String TAG = DashboardItemHandler.class.getSimpleName();
    private static final String EMPTY_FIELD = "";

    private static final String[] PROJECTION = {
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
            DashboardItems.TABLE_NAME + "." + DashboardItems.WRITE,
            DashboardItems.TABLE_NAME + "." + DashboardItems.MESSAGES,
            DashboardItems.TABLE_NAME + "." + DashboardItems.ELEMENT,
            DashboardItems.TABLE_NAME + "." + DashboardItems.DASHBOARD_ID,
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
    private static final int MESSAGES = 12;
    private static final int ELEMENT = 13;
    private static final int DASHBOARD_ID = 14;

    private final Context mContext;

    public DashboardItemHandler(Context context) {
        mContext = isNull(context, "Context object must not be null");
    }

    private static ContentValues toContentValues(DashboardItem item) {
        isNull(item, "DashboardItem object must not be null");

        String created = item.getCreated().toString();
        String lastUpdated = item.getLastUpdated().toString();
        String shape = isEmpty(item.getShape()) ? DashboardItem.SHAPE_NORMAL : item.getShape();
        Access access = item.getAccess();

        ContentValues values = new ContentValues();
        values.put(DashboardItems.ID, item.getId());
        values.put(DashboardItems.CREATED, created);
        values.put(DashboardItems.LAST_UPDATED, lastUpdated);
        values.put(DashboardItems.TYPE, item.getType());
        values.put(DashboardItems.SHAPE, shape);
        values.put(DashboardItems.CONTENT_COUNT, item.getContentCount());
        values.put(DashboardItems.DELETE, access.isDelete() ? 1 : 0);
        values.put(DashboardItems.EXTERNALIZE, access.isExternalize() ? 1 : 0);
        values.put(DashboardItems.MANAGE, access.isManage() ? 1 : 0);
        values.put(DashboardItems.READ, access.isRead() ? 1 : 0);
        values.put(DashboardItems.UPDATE, access.isUpdate() ? 1 : 0);
        values.put(DashboardItems.WRITE, access.isWrite() ? 1 : 0);
        values.put(DashboardItems.MESSAGES, item.isMessages() ? 1 : 0);
        values.put(DashboardItems.DASHBOARD_ID, item.getDashboardId());
        putElementToValues(item, values);

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
        item.setDashboardId(cursor.getString(DASHBOARD_ID));
        item.setAccess(access);
        item.setMessages(cursor.getInt(MESSAGES) == 1);
        putElementToItem(item, cursor);

        return item;
    }

    private static void putElementToValues(DashboardItem item, ContentValues values) {
        String element;
        switch (item.getType()) {
            case DashboardItem.TYPE_CHART: {
                element = toJson(item.getChart());
                break;
            }
            case DashboardItem.TYPE_EVENT_CHART: {
                element = toJson(item.getEventChart());
                break;
            }
            case DashboardItem.TYPE_MAP: {
                element = toJson(item.getMap());
                break;
            }
            case DashboardItem.TYPE_REPORT_TABLE: {
                element = toJson(item.getReportTable());
                break;
            }
            case DashboardItem.TYPE_EVENT_REPORT: {
                element = toJson(item.getEventReport());
                break;
            }
            case DashboardItem.TYPE_USERS: {
                element = toJson(item.getUsers());
                break;
            }
            case DashboardItem.TYPE_REPORTS: {
                element = toJson(item.getReports());
                break;
            }
            case DashboardItem.TYPE_RESOURCES: {
                element = toJson(item.getResources());
                break;
            }
            case DashboardItem.TYPE_REPORT_TABLES: {
                element = toJson(item.getReportTables());
                break;
            }
            case DashboardItem.TYPE_MESSAGES: {
                element = EMPTY_FIELD;
                break;
            }
            default:
                throw new IllegalArgumentException("Unsupported DashboardItem Type");
        }

        values.put(DashboardItems.ELEMENT, element);
    }

    private static void putElementToItem(DashboardItem item, Cursor cursor) {
        String type = cursor.getString(TYPE);
        String element = cursor.getString(ELEMENT);
        TypeReference<List<DashboardElement>> typeReference
                = new TypeReference<List<DashboardElement>>() {
        };

        switch (type) {
            case DashboardItem.TYPE_CHART: {
                item.setChart(fromJson(element, DashboardElement.class));
                break;
            }
            case DashboardItem.TYPE_EVENT_CHART: {
                item.setEventChart(fromJson(element, DashboardElement.class));
                break;
            }
            case DashboardItem.TYPE_MAP: {
                item.setMap(fromJson(element, DashboardElement.class));
                break;
            }
            case DashboardItem.TYPE_REPORT_TABLE: {
                item.setReportTable(fromJson(element, DashboardElement.class));
                break;
            }
            case DashboardItem.TYPE_EVENT_REPORT: {
                item.setEventReport(fromJson(element, DashboardElement.class));
                break;
            }
            case DashboardItem.TYPE_USERS: {
                item.setUsers(fromJson(element, typeReference));
                break;
            }
            case DashboardItem.TYPE_REPORTS: {
                item.setReports(fromJson(element, typeReference));
                break;
            }
            case DashboardItem.TYPE_RESOURCES: {
                item.setResources(fromJson(element, typeReference));
                break;
            }
            case DashboardItem.TYPE_REPORT_TABLES: {
                item.setReportTables(fromJson(element, typeReference));
                break;
            }
            case DashboardItem.TYPE_MESSAGES: {
                break;
            }
            default:
                throw new IllegalArgumentException("Unsupported DashboardItem Type");
        }
    }

    @Override public List<DashboardItem> map(Cursor cursor, boolean closeCursor) {
        List<DashboardItem> items = new ArrayList<>();
        try {
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    items.add(fromCursor(cursor));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null && closeCursor) {
                cursor.close();
            }
        }
        return items;
    }

    @Override public String[] getProjection() {
        return PROJECTION;
    }

    @Override public ContentProviderOperation insert(DashboardItem item) {
        isNull(item, "DashboardItem must not be null");

        Log.v(TAG, "Inserting " + item.getId());
        return ContentProviderOperation
                .newInsert(DashboardItems.CONTENT_URI)
                .withValues(toContentValues(item))
                .build();
    }

    @Override public ContentProviderOperation update(DashboardItem item) {
        isNull(item, "DashboardItem must not be null");

        Log.v(TAG, "Updating " + item.getId());
        Uri uri = DashboardItems.CONTENT_URI.buildUpon()
                .appendPath(item.getId()).build();
        return ContentProviderOperation
                .newUpdate(uri)
                .withValues(toContentValues(item))
                .build();
    }

    @Override public ContentProviderOperation delete(DashboardItem item) {
        isNull(item, "DashboardItem must not be null");

        Log.v(TAG, "Deleting " + item.getId());
        Uri uri = DashboardItems.CONTENT_URI.buildUpon()
                .appendPath(item.getId()).build();
        return ContentProviderOperation
                .newDelete(uri)
                .build();
    }

    @Override public <T> List<T> queryRelatedModels(Class<T> clazz, Object id) {
        throw new IllegalArgumentException("Unsupported method");
    }

    @Override public List<DashboardItem> query(String selection, String[] args) {
        Cursor cursor = mContext.getContentResolver().query(
                DashboardItems.CONTENT_URI, PROJECTION, selection, args, null
        );
        return map(cursor, true);
    }

    @Override public List<DashboardItem> query() {
        return query(null, null);
    }

    @Override public List<ContentProviderOperation> sync(List<DashboardItem> items) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        Map<String, DashboardItem> newItems = toMap(items);
        Map<String, DashboardItem> oldItems = toMap(query());

        for (String oldItemKey : oldItems.keySet()) {
            DashboardItem newItem = newItems.get(oldItemKey);
            DashboardItem oldItem = oldItems.get(oldItemKey);

            if (newItem == null) {
                ops.add(delete(oldItem));
                continue;
            }

            if (newItem.getLastUpdated().isAfter(oldItem.getLastUpdated())) {
                ops.add(update(newItem));
            }

            newItems.remove(oldItemKey);
        }

        for (String newItemKey : newItems.keySet()) {
            ops.add(insert(newItems.get(newItemKey)));
        }

        return ops;
    }
}