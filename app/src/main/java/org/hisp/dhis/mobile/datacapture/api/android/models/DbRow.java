package org.hisp.dhis.mobile.datacapture.api.android.models;

public class DbRow<T> {
    private int id;
    private T item;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public T getItem() {
        return item;
    }

    public void setItem(T item) {
        this.item = item;
    }
}
