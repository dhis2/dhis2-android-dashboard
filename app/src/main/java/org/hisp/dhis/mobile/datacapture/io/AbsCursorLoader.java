/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hisp.dhis.mobile.datacapture.io;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;

/**
 * Static library support version of the framework's {@link android.content.CursorLoader}.
 * Used to write apps that run on platforms prior to Android 3.0.  When running
 * on Android 3.0 or above, this implementation is still used; it does not try
 * to switch to the framework's implementation.  See the framework SDK
 * documentation for a class overview.
 */
public abstract class AbsCursorLoader<T> extends AsyncTaskLoader<CursorHolder<T>> {
    final ForceLoadContentObserver mObserver;

    Uri mUri;
    String[] mProjection;
    String mSelection;
    String[] mSelectionArgs;
    String mSortOrder;

    //Cursor mCursor;
    CursorHolder<T> mData;

    /**
     * Creates an empty unspecified AbstractCursorLoader.  You must follow this with
     * calls to {@link #setUri(android.net.Uri)}, {@link #setSelection(String)}, etc
     * to specify the query to perform.
     */
    public AbsCursorLoader(Context context) {
        super(context);
        mObserver = new ForceLoadContentObserver();
    }

    /**
     * Creates a fully-specified AbstractCursorLoader.  See
     * {@link android.content.ContentResolver#query(android.net.Uri, String[], String, String[], String)
     * ContentResolver.query()} for documentation on the meaning of the
     * parameters.  These will be passed as-is to that call.
     */
    public AbsCursorLoader(Context context, Uri uri, String[] projection, String selection,
                           String[] selectionArgs, String sortOrder) {
        super(context);
        mObserver = new ForceLoadContentObserver();
        mUri = uri;
        mProjection = projection;
        mSelection = selection;
        mSelectionArgs = selectionArgs;
        mSortOrder = sortOrder;
    }

    /* Runs on a worker thread */
    @Override
    public CursorHolder<T> loadInBackground() {
        Cursor cursor = getContext().getContentResolver().query(mUri, mProjection, mSelection,
                mSelectionArgs, mSortOrder);
        if (cursor != null) {
            // Ensure the cursor window is filled
            cursor.getCount();
            cursor.registerContentObserver(mObserver);
        }
        // read data from cursor here
        return new CursorHolder<T>(cursor, readDataFromCursor(cursor));
    }

    protected abstract T readDataFromCursor(Cursor cursor);

    /* Runs on the UI thread */
    @Override
    public void deliverResult(CursorHolder<T> data) {
        if (isReset()) {
            // An async query came in while the loader is stopped
            if (data.getCursor() != null) {
                data.getCursor().close();
                data.setData(null);
            }
            return;
        }
        //Cursor oldCursor = mCursor;
        //mCursor = cursor;
        CursorHolder<T> oldData = mData;
        mData = data;

        if (isStarted()) {
            super.deliverResult(data);
        }

        if (oldData != null && oldData.getCursor() != null &&
                oldData.getCursor() != data.getCursor() && !oldData.getCursor().isClosed()) {
            oldData.getCursor().close();
            oldData.setData(null);
        }

        /* if (oldCursor != null && oldCursor != cursor && !oldCursor.isClosed()) {
            oldCursor.close();
        } */
    }

    /**
     * Starts an asynchronous load of the contacts list data. When the result is ready the callbacks
     * will be called on the UI thread. If a previous load has been completed and is still valid
     * the result may be passed to the callbacks immediately.
     * <p/>
     * Must be called from the UI thread
     */
    @Override
    protected void onStartLoading() {
        if (mData != null) {
            deliverResult(mData);
        }
        if (takeContentChanged() || mData == null) {
            forceLoad();
        }
    }

    /**
     * Must be called from the UI thread
     */
    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    @Override
    public void onCanceled(CursorHolder<T> data) {
        if (data != null && data.getCursor() != null && !data.getCursor().isClosed()) {
            data.getCursor().close();
            data.setData(null);
        }
    }

    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        if (mData != null && mData.getCursor() != null && !mData.getCursor().isClosed()) {
            mData.getCursor().close();
            mData.setData(null);
        }
        mData = null;
    }

    public Uri getUri() {
        return mUri;
    }

    public void setUri(Uri uri) {
        mUri = uri;
    }

    public String[] getProjection() {
        return mProjection;
    }

    public void setProjection(String[] projection) {
        mProjection = projection;
    }

    public String getSelection() {
        return mSelection;
    }

    public void setSelection(String selection) {
        mSelection = selection;
    }

    public String[] getSelectionArgs() {
        return mSelectionArgs;
    }

    public void setSelectionArgs(String[] selectionArgs) {
        mSelectionArgs = selectionArgs;
    }

    public String getSortOrder() {
        return mSortOrder;
    }

    public void setSortOrder(String sortOrder) {
        mSortOrder = sortOrder;
    }
}