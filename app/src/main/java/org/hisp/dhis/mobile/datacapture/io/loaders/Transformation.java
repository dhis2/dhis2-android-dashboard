package org.hisp.dhis.mobile.datacapture.io.loaders;

import android.content.Context;
import android.database.Cursor;

public interface Transformation<T> {
    public T transform(Context context, Cursor cursor);
}