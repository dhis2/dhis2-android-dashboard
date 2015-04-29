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

package org.dhis2.android.dashboard.api.persistence.database;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import org.dhis2.android.dashboard.api.persistence.database.DbContract.DashboardItems;
import org.dhis2.android.dashboard.api.persistence.database.DbContract.Dashboards;
import org.dhis2.android.dashboard.api.persistence.database.DbContract.DashboardsToItems;

import java.util.ArrayList;

import static android.content.ContentUris.parseId;
import static android.content.ContentUris.withAppendedId;
import static android.text.TextUtils.isEmpty;

public final class DbContentProvider extends ContentProvider {
    private static final int DASHBOARDS = 100;
    private static final int DASHBOARD_ID = 101;
    private static final int DASHBOARD_ID_ITEMS = 102;

    private static final int DASHBOARD_ITEMS = 200;
    private static final int DASHBOARD_ITEM_ID = 201;

    private static final int DASHBOARDS_TO_ITEMS = 300;
    private static final int DASHBOARDS_TO_ITEM_ID = 301;

    private static final UriMatcher URI_MATCHER = buildMatcher();
    private DbHelper mDbHelper;

    private static UriMatcher buildMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(DbContract.AUTHORITY, Dashboards.DASHBOARDS_PATH, DASHBOARDS);
        matcher.addURI(DbContract.AUTHORITY, Dashboards.DASHBOARD_ID_PATH, DASHBOARD_ID);
        matcher.addURI(DbContract.AUTHORITY, Dashboards.DASHBOARD_ID_ITEMS_PATH, DASHBOARD_ID_ITEMS);

        matcher.addURI(DbContract.AUTHORITY, DashboardItems.DASHBOARD_ITEMS_PATH, DASHBOARD_ITEMS);
        matcher.addURI(DbContract.AUTHORITY, DashboardItems.DASHBOARD_ITEM_ID_PATH, DASHBOARD_ITEM_ID);

        matcher.addURI(DbContract.AUTHORITY, DashboardsToItems.DASHBOARD_TO_ITEMS_PATH, DASHBOARDS_TO_ITEMS);
        matcher.addURI(DbContract.AUTHORITY, DashboardsToItems.DASHBOARD_TO_ITEM_ID_PATH, DASHBOARDS_TO_ITEM_ID);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new DbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case DASHBOARDS:
                return Dashboards.CONTENT_TYPE;
            case DASHBOARD_ID:
                return Dashboards.CONTENT_ITEM_TYPE;
            case DASHBOARD_ID_ITEMS:
                return DashboardItems.CONTENT_TYPE;
            case DASHBOARD_ITEMS:
                return DashboardItems.CONTENT_TYPE;
            case DASHBOARD_ITEM_ID:
                return DashboardItems.CONTENT_ITEM_TYPE;
            case DASHBOARDS_TO_ITEMS:
                return DashboardsToItems.CONTENT_TYPE;
            case DASHBOARDS_TO_ITEM_ID:
                return DashboardsToItems.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("No corresponding Uri type was found");
        }
    }

    // TODO add relationship handling code for DashboardsToItems table
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        switch (URI_MATCHER.match(uri)) {
            case DASHBOARDS: {
                return query(uri, Dashboards.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
            }
            case DASHBOARD_ID: {
                String id = Dashboards.getId(uri);
                return queryId(uri, Dashboards.TABLE_NAME,
                        Dashboards.ID, projection, selection, selectionArgs, sortOrder, id);
            }
            case DASHBOARD_ID_ITEMS: {
                String id = Dashboards.getId(uri);
                return queryId(uri, DbSchema.UNIT_JOIN_DASHBOARD_ITEMS_TABLE,
                        DashboardsToItems.TABLE_NAME + "." + DashboardsToItems.DASHBOARD_ID,
                        projection, selection, selectionArgs, sortOrder, id);
            }
            case DASHBOARD_ITEMS: {
                return query(uri, DashboardItems.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
            }
            case DASHBOARD_ITEM_ID: {
                String id = DashboardItems.getId(uri);
                return queryId(uri, DashboardItems.TABLE_NAME,
                        DashboardItems.ID, projection, selection, selectionArgs, sortOrder, id);
            }
            case DASHBOARDS_TO_ITEMS: {
                return query(uri, DashboardsToItems.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
            }
            case DASHBOARDS_TO_ITEM_ID: {
                String id = parseId(uri) + "";
                return queryId(uri, DashboardsToItems.TABLE_NAME,
                        DashboardsToItems.ID, projection, selection, selectionArgs, sortOrder, id);
            }

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch (URI_MATCHER.match(uri)) {
            case DASHBOARDS: {
                return insert(Dashboards.TABLE_NAME, values, uri);
            }
            case DASHBOARD_ITEMS: {
                return insert(DashboardItems.TABLE_NAME, values, uri);
            }
            case DASHBOARDS_TO_ITEMS: {
                return insert(DashboardsToItems.TABLE_NAME, values, uri);
            }
            default:
                throw new IllegalArgumentException("Unsupported URI for insertion: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (URI_MATCHER.match(uri)) {
            case DASHBOARDS: {
                return delete(Dashboards.TABLE_NAME,
                        selection, selectionArgs);
            }
            case DASHBOARD_ID: {
                String id = Dashboards.getId(uri);
                return deleteId(Dashboards.TABLE_NAME,
                        Dashboards.ID, selection, selectionArgs, id);
            }
            case DASHBOARD_ITEMS: {
                return delete(DashboardItems.TABLE_NAME,
                        selection, selectionArgs);
            }
            case DASHBOARD_ITEM_ID: {
                String id = DashboardItems.getId(uri);
                return deleteId(DashboardItems.TABLE_NAME,
                        DashboardItems.ID, selection, selectionArgs, id);
            }
            case DASHBOARDS_TO_ITEMS: {
                return delete(DashboardsToItems.TABLE_NAME,
                        selection, selectionArgs);
            }
            case DASHBOARDS_TO_ITEM_ID: {
                String id = parseId(uri) + "";
                return deleteId(DashboardsToItems.TABLE_NAME,
                        DashboardsToItems.ID, selection, selectionArgs, id);
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
                return update(Dashboards.TABLE_NAME,
                        selection, selectionArgs, values);
            }
            case DASHBOARD_ID: {
                String id = Dashboards.getId(uri);
                return updateId(Dashboards.TABLE_NAME,
                        Dashboards.ID, selection, selectionArgs, id, values);
            }
            case DASHBOARD_ITEMS: {
                return update(DashboardItems.TABLE_NAME,
                        selection, selectionArgs, values);
            }
            case DASHBOARD_ITEM_ID: {
                String id = DashboardItems.getId(uri);
                return updateId(DashboardItems.TABLE_NAME,
                        DashboardItems.ID, selection, selectionArgs, id, values);
            }
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    private Cursor query(Uri uri, String tableName, String[] projection,
                         String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();
        qBuilder.setTables(tableName);

        Cursor cursor = qBuilder.query(
                db, projection, selection, selectionArgs, null, null, sortOrder
        );

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    private Uri insert(String tableName, ContentValues values, Uri uri) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long id = db.insertOrThrow(tableName, null, values);
        return withAppendedId(uri, id);
    }

    private int delete(String tableName, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        return db.delete(tableName, selection, selectionArgs);
    }

    private int update(String tableName, String selection,
                       String[] selectionArgs, ContentValues values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        return db.update(tableName, values, selection, selectionArgs);
    }

    private Cursor queryId(Uri uri, String tableName, String colId, String[] projection,
                           String selection, String[] selectionArgs, String sortOrder, String id) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();

        qBuilder.setTables(tableName);
        qBuilder.appendWhere(colId + " = " + "'" + id + "'");

        Cursor cursor = qBuilder.query(
                db, projection, selection, selectionArgs, null, null, sortOrder
        );

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    private int deleteId(String tableName, String colId,
                         String selection, String[] selectionArgs, String id) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String where = colId + " = " + "'" + id + "'";
        if (!isEmpty(selection)) {
            where += " AND " + selection;
        }

        return db.delete(tableName, where, selectionArgs);
    }

    private int updateId(String tableName, String colId,
                         String selection, String[] selectionArgs,
                         String id, ContentValues values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String where = colId + " = " + "'" + id + "'";
        if (!isEmpty(selection)) {
            where += " AND " + selection;
        }

        return db.update(tableName, values, where, selectionArgs);
    }

    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            final int numOperations = operations.size();
            final ContentProviderResult[] results = new ContentProviderResult[numOperations];
            for (int i = 0; i < numOperations; i++) {
                results[i] = operations.get(i).apply(this, results, i);
            }
            db.setTransactionSuccessful();
            return results;
        } finally {
            db.endTransaction();
        }
    }
}