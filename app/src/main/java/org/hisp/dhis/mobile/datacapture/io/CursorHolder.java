package org.hisp.dhis.mobile.datacapture.io;

import android.database.Cursor;

public class CursorHolder<T> {
    private Cursor mCursor;
    private T mData;

    public CursorHolder(Cursor cursor, T data) {
        mCursor = cursor;
        mData = data;
    }

    public T getData() {
        return mData;
    }

    public void setData(T data) {
        mData = data;
    }

    public Cursor getCursor() {
        return mCursor;
    }

    public void setCursor(Cursor cursor) {
        mCursor = cursor;
    }
}