package org.hisp.dhis.mobile.datacapture.utils;

import org.hisp.dhis.mobile.datacapture.api.android.models.DbRow;

import java.util.ArrayList;
import java.util.List;

public final class DbUtils {

    private DbUtils() {
        // no instances
    }

    public static <T> T stripRow(DbRow<T> row) {
        T item = null;
        if (row != null) {
            item = row.getItem();
        }
        return item;
    }

    public static <T> List<T> stripRows(List<DbRow<T>> rows) {
        List<T> items = new ArrayList<>();
        if (rows != null && rows.size() > 0) {
            for (DbRow<T> dbRow: rows) {
                items.add(dbRow.getItem());
            }
        }
        return items;
    }
}
