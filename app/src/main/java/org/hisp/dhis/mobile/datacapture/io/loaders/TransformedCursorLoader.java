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

package org.hisp.dhis.mobile.datacapture.io.loaders;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.Map;

import static org.hisp.dhis.mobile.datacapture.api.utils.Preconditions.isNull;

/**
 * Static library support version of the framework's {@link android.content.CursorLoader}.
 * Used to write apps that run on platforms prior to Android 3.0.  When running
 * on Android 3.0 or above, this implementation is still used; it does not try
 * to switch to the framework's implementation.  See the framework SDK
 * documentation for a class overview.
 */
public class TransformedCursorLoader<T> extends AsyncTaskLoader<T> {
    final ForceLoadContentObserver mObserver;
    final Transformation<T> mTransformation;

    final Uri mUri;
    final String[] mProjection;
    final String mSelection;
    final String[] mSelectionArgs;
    final String mSortOrder;

    T mResult;
    Map<T, Cursor> mCursorsForResults;

    /**
     * Creates a fully-specified CursorLoader.  See
     * {@link android.content.ContentResolver#query(android.net.Uri, String[], String, String[], String)
     * ContentResolver.query()} for documentation on the meaning of the
     * parameters.  These will be passed as-is to that call.
     */
    public TransformedCursorLoader(Context context, Uri uri, String[] projection, String selection,
                                   String[] selectionArgs, String sortOrder, Transformation<T> transformation) {
        super(context);
        isNull(transformation, "Transformation object must not be null");

        mObserver = new ForceLoadContentObserver();
        mTransformation = transformation;
        mCursorsForResults = new IdentityHashMap<>();

        mUri = uri;
        mProjection = projection;
        mSelection = selection;
        mSelectionArgs = selectionArgs;
        mSortOrder = sortOrder;
    }

    private static void releaseCursor(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    /* Runs on a worker thread */
    @Override
    public T loadInBackground() {
        Cursor cursor = getContext().getContentResolver().query(
                mUri, mProjection, mSelection, mSelectionArgs, mSortOrder
        );
        if (cursor != null) {
            // Ensure the cursor window is filled
            cursor.getCount();
        }

        T newResult = mTransformation.transform(getContext(), cursor);
        isNull(newResult, "Transformed result must not be null");

        if (mCursorsForResults.get(newResult) != null) {
            releaseCursor(cursor);
        } else {
            mCursorsForResults.put(newResult, cursor);
        }

        return newResult;
    }

    /* Runs on the UI thread */
    @Override
    public void deliverResult(T newResult) {
        if (isReset()) {
            releaseResources(newResult);
            return;
        }

        T oldResult = mResult;
        mResult = newResult;

        if (isStarted()) {
            if (oldResult != newResult) {
                Cursor cursor = mCursorsForResults.get(newResult);
                cursor.registerContentObserver(mObserver);
            }
            super.deliverResult(newResult);
        }

        if (oldResult != null && oldResult != newResult) {
            releaseResources(oldResult);
        }
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
        if (mResult != null) {
            deliverResult(mResult);
        }
        if (takeContentChanged() || mResult == null) {
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
    public void onCanceled(T result) {
        super.onCanceled(result);
        releaseResources(result);
    }

    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        releaseResources(mResult);
        mResult = null;
    }

    private void releaseResources(T result) {
        releaseCursor(mCursorsForResults.remove(result));
    }

    @Override
    public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        super.dump(prefix, fd, writer, args);
        writer.print(prefix);
        writer.print("mUri=");
        writer.println(mUri);
        writer.print(prefix);
        writer.print("mProjection=");
        writer.println(Arrays.toString(mProjection));
        writer.print(prefix);
        writer.print("mSelection=");
        writer.println(mSelection);
        writer.print(prefix);
        writer.print("mSelectionArgs=");
        writer.println(Arrays.toString(mSelectionArgs));
        writer.print(prefix);
        writer.print("mSortOrder=");
        writer.println(mSortOrder);
    }
}
