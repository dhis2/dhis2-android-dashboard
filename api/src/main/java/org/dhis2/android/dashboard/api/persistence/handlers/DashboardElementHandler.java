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

import org.dhis2.android.dashboard.api.models.DashboardElement;
import org.dhis2.android.dashboard.api.persistence.database.DbContract.DashboardElements;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.dhis2.android.dashboard.api.utils.DbUtils.toMap;
import static org.dhis2.android.dashboard.api.utils.Preconditions.isNull;

/**
 * Created by araz on 24.05.2015.
 */
public final class DashboardElementHandler implements IModelHandler<DashboardElement> {
    private static final String TAG = DashboardElementHandler.class.getSimpleName();

    private static final String[] PROJECTION = {
            DashboardElements.ID,
            DashboardElements.CREATED,
            DashboardElements.LAST_UPDATED,
            DashboardElements.NAME,
            DashboardElements.DISPLAY_NAME,
            DashboardElements.TYPE
    };

    private static final int ID = 0;
    private static final int CREATED = 1;
    private static final int LAST_UPDATED = 2;
    private static final int NAME = 3;
    private static final int DISPLAY_NAME = 4;
    private static final int TYPE = 5;

    private final Context mContext;

    public DashboardElementHandler(Context context) {
        mContext = context;
    }

    private static ContentValues toContentValues(DashboardElement element) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(DashboardElements.ID, element.getId());
        contentValues.put(DashboardElements.CREATED, element.getCreated().toString());
        contentValues.put(DashboardElements.LAST_UPDATED, element.getLastUpdated().toString());
        contentValues.put(DashboardElements.NAME, element.getName());
        contentValues.put(DashboardElements.DISPLAY_NAME, element.getName());
        contentValues.put(DashboardElements.TYPE, element.getType());

        return contentValues;
    }

    private static DashboardElement fromCursor(Cursor cursor) {
        DashboardElement element = new DashboardElement();

        DateTime created = DateTime.parse(cursor.getString(CREATED));
        DateTime lastUpdated = DateTime.parse(cursor.getString(LAST_UPDATED));

        element.setId(cursor.getString(ID));
        element.setCreated(created);
        element.setLastUpdated(lastUpdated);
        element.setName(cursor.getString(NAME));
        element.setDisplayName(cursor.getString(DISPLAY_NAME));
        element.setType(cursor.getString(TYPE));

        return element;
    }

    @Override public List<DashboardElement> map(Cursor cursor, boolean closeCursor) {
        List<DashboardElement> elements = new ArrayList<>();

        try {
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();

                do {
                    elements.add(fromCursor(cursor));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null && closeCursor) {
                cursor.close();
            }
        }
        return elements;
    }

    @Override public String[] getProjection() {
        return PROJECTION;
    }

    @Override public ContentProviderOperation insert(DashboardElement element) {
        isNull(element, "DashboardElement must not be null");

        Log.v(TAG, "Inserting " + element.getId());
        return ContentProviderOperation
                .newInsert(DashboardElements.CONTENT_URI)
                .withValues(toContentValues(element))
                .build();
    }

    @Override public ContentProviderOperation update(DashboardElement element) {
        isNull(element, "DashboardElement must not be null");

        Log.v(TAG, "Updating " + element.getId());
        Uri uri = DashboardElements.CONTENT_URI.buildUpon()
                .appendPath(element.getId()).build();
        return ContentProviderOperation
                .newUpdate(uri)
                .withValues(toContentValues(element))
                .build();
    }

    @Override public ContentProviderOperation delete(DashboardElement element) {
        isNull(element, "DashboardElement must not be null");

        Log.v(TAG, "Deleting " + element.getId());
        Uri uri = DashboardElements.CONTENT_URI.buildUpon()
                .appendPath(element.getId()).build();
        return ContentProviderOperation
                .newDelete(uri)
                .build();
    }

    @Override public <T> List<T> queryRelatedModels(Class<T> clazz, Object id) {
        throw new IllegalArgumentException("Unsupported method");
    }

    @Override public List<DashboardElement> query(String selection, String[] args) {
        Cursor cursor = mContext.getContentResolver().query(
                DashboardElements.CONTENT_URI, PROJECTION, selection, args, null
        );
        return map(cursor, true);
    }

    @Override public List<DashboardElement> query() {
        return query(null, null);
    }

    @Override public List<ContentProviderOperation> sync(List<DashboardElement> elements) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        Map<String, DashboardElement> newElements = toMap(elements);
        Map<String, DashboardElement> oldElements = toMap(query());

        for (String oldElementKey : oldElements.keySet()) {
            DashboardElement newElement = newElements.get(oldElementKey);
            DashboardElement oldElement = oldElements.get(oldElementKey);

            if (newElement == null) {
                ops.add(delete(oldElement));
                continue;
            }

            if (newElement.getLastUpdated().isAfter(oldElement.getLastUpdated())) {
                ops.add(update(newElement));
            }

            newElements.remove(oldElementKey);
        }

        for (String newElementKey : newElements.keySet()) {
            ops.add(insert(newElements.get(newElementKey)));
        }

        return ops;
    }
}
