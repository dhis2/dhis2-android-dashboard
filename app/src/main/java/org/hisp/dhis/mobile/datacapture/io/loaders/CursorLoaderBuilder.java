package org.hisp.dhis.mobile.datacapture.io.loaders;

import android.net.Uri;

public class CursorLoaderBuilder {
    private Uri mUri;
    private String[] mProjection;
    private String mSelection;
    private String[] mSelectionArgs;
    private String mSortOrder;
    private Transformation mTransformation;

    private CursorLoaderBuilder(Uri uri) {
        mUri = uri;
    }

    public static CursorLoaderBuilder forUri(Uri uri) {
        return new CursorLoaderBuilder(uri);
    }

    public CursorLoaderBuilder projection(String[] projection) {
        mProjection = projection;
        return this;
    }

    public CursorLoaderBuilder selection(String selection) {
        mSelection = selection;
        return this;
    }

    public CursorLoaderBuilder selectionArgs(String[] selectionArgs) {
        mSelectionArgs = selectionArgs;
        return this;
    }

    public CursorLoaderBuilder sortOrder(String sortOrder) {
        mSortOrder = sortOrder;
        return this;
    }

    public <T> TransformationCursorBuilder<T> transformation(Transformation<T> transformation) {
        mTransformation = transformation;
        return new TransformationCursorBuilder<T>(mUri, mProjection, mSelection,
                mSelectionArgs, mSortOrder, transformation);
    }
}
