package org.hisp.dhis.mobile.datacapture.io.loaders;

import android.content.Context;
import android.net.Uri;

import static org.hisp.dhis.mobile.datacapture.api.utils.Preconditions.isNull;

public class TransformationCursorBuilder<T> {
    private Uri mUri;
    private String[] mProjection;
    private String mSelection;
    private String[] mSelectionArgs;
    private String mSortOrder;
    private Transformation<T> mTransformation;

    public TransformationCursorBuilder(Uri uri, String[] projection,
                                       String selection, String[] selectionArgs,
                                       String sortOrder, Transformation<T> transformation) {
        mUri = uri;
        mProjection = projection;
        mSelection = selection;
        mSelectionArgs = selectionArgs;
        mSortOrder = sortOrder;
        mTransformation = transformation;
    }

    public TransformedCursorLoader<T> build(Context context) {
        isNull(context, "Context object must not be null");
        isNull(mUri, "Uri object must not be null");
        isNull(mProjection, "String[] projection object must not be null");
        isNull(mTransformation, "Transformation object must not be null");

        return new TransformedCursorLoader<>(
                context, mUri, mProjection, mSelection,
                mSelectionArgs, mSortOrder, mTransformation
        );
    }
}
