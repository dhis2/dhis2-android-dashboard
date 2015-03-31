package org.hisp.dhis.mobile.datacapture.api.android.date;

public interface CustomDateIterator<T> {
    boolean hasNext();

    boolean hasPrevious();

    T current();

    T next();

    T previous();
}
