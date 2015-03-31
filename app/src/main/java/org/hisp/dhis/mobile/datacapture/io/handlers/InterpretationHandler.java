package org.hisp.dhis.mobile.datacapture.io.handlers;

import android.content.ContentProviderOperation;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.hisp.dhis.mobile.datacapture.api.android.models.DbRow;
import org.hisp.dhis.mobile.datacapture.api.android.models.State;
import org.hisp.dhis.mobile.datacapture.api.models.Access;
import org.hisp.dhis.mobile.datacapture.api.models.Comment;
import org.hisp.dhis.mobile.datacapture.api.models.DashboardItemElement;
import org.hisp.dhis.mobile.datacapture.api.models.Interpretation;
import org.hisp.dhis.mobile.datacapture.api.models.InterpretationDataSet;
import org.hisp.dhis.mobile.datacapture.api.models.InterpretationDataSetPeriod;
import org.hisp.dhis.mobile.datacapture.api.models.InterpretationOrganizationUnit;
import org.hisp.dhis.mobile.datacapture.api.models.User;
import org.hisp.dhis.mobile.datacapture.io.DBContract.Interpretations;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.text.TextUtils.isEmpty;

public final class InterpretationHandler {
    public static final String[] PROJECTION = {
            Interpretations.DB_ID,
            Interpretations.ID,
            Interpretations.CREATED,
            Interpretations.LAST_UPDATED,
            Interpretations.ACCESS,
            Interpretations.TYPE,
            Interpretations.NAME,
            Interpretations.DISPLAY_NAME,
            Interpretations.TEXT,
            Interpretations.EXTERNAL_ACCESS,
            Interpretations.MAP,
            Interpretations.CHART,
            Interpretations.REPORT_TABLE,
            Interpretations.DATASET,
            Interpretations.ORGANIZATION_UNIT,
            Interpretations.PERIOD,
            Interpretations.USER,
            Interpretations.COMMENTS
    };


    private static final String TAG = InterpretationHandler.class.getSimpleName();

    private static final int DB_ID = 0;
    private static final int ID = 1;
    private static final int CREATED = 2;
    private static final int LAST_UPDATED = 3;
    private static final int ACCESS = 4;
    private static final int TYPE = 5;
    private static final int NAME = 6;
    private static final int DISPLAY_NAME = 7;
    private static final int TEXT = 8;
    private static final int EXTERNAL_ACCESS = 9;
    private static final int MAP = 10;
    private static final int CHART = 11;
    private static final int REPORT_TABLE = 12;
    private static final int DATASET = 13;
    private static final int ORGANIZATION_UNIT = 14;
    private static final int PERIOD = 15;
    private static final int USER = 16;
    private static final int COMMENTS = 17;

    private InterpretationHandler() {
    }

    public static ContentValues toContentValues(Interpretation interpretation) {
        if (interpretation == null) {
            throw new IllegalArgumentException("Interpretation object cannot be null");
        }

        ContentValues values = new ContentValues();
        Gson gson = new Gson();

        values.put(Interpretations.ID, interpretation.getId());
        values.put(Interpretations.CREATED, interpretation.getCreated());
        values.put(Interpretations.LAST_UPDATED, interpretation.getLastUpdated());
        values.put(Interpretations.ACCESS, gson.toJson(interpretation.getAccess()));
        values.put(Interpretations.TYPE, interpretation.getType());
        values.put(Interpretations.NAME, interpretation.getName());
        values.put(Interpretations.DISPLAY_NAME, interpretation.getDisplayName());
        values.put(Interpretations.TEXT, interpretation.getText());
        values.put(Interpretations.EXTERNAL_ACCESS, interpretation.isExternalAccess() ? 1 : 0);
        values.put(Interpretations.MAP, gson.toJson(interpretation.getMap()));
        values.put(Interpretations.CHART, gson.toJson(interpretation.getChart()));
        values.put(Interpretations.REPORT_TABLE, gson.toJson(interpretation.getReportTable()));
        values.put(Interpretations.DATASET, gson.toJson(interpretation.getDataSet()));
        values.put(Interpretations.ORGANIZATION_UNIT, gson.toJson(interpretation.getOrganisationUnit()));
        values.put(Interpretations.PERIOD, gson.toJson(interpretation.getPeriod()));
        values.put(Interpretations.USER, gson.toJson(interpretation.getUser()));
        values.put(Interpretations.COMMENTS, gson.toJson(interpretation.getComments()));

        return values;
    }

    public static DbRow<Interpretation> fromCursor(Cursor cursor) {
        if (cursor == null) {
            throw new IllegalArgumentException("Cursor object cannot be null");
        }

        Interpretation interpretation = new Interpretation();
        Gson gson = new Gson();

        Access access = gson.fromJson(cursor.getString(ACCESS), Access.class);
        DashboardItemElement map = gson.fromJson(cursor.getString(MAP), DashboardItemElement.class);
        DashboardItemElement chart = gson.fromJson(cursor.getString(CHART), DashboardItemElement.class);
        DashboardItemElement reportTable = gson.fromJson(cursor.getString(REPORT_TABLE), DashboardItemElement.class);
        InterpretationDataSet dataset = gson.fromJson(cursor.getString(DATASET), InterpretationDataSet.class);
        InterpretationOrganizationUnit unit = gson.fromJson(cursor.getString(ORGANIZATION_UNIT), InterpretationOrganizationUnit.class);
        InterpretationDataSetPeriod period = gson.fromJson(cursor.getString(PERIOD), InterpretationDataSetPeriod.class);
        User user = gson.fromJson(cursor.getString(USER), User.class);

        Type type = new TypeToken<List<Comment>>() { }.getType();
        ArrayList<Comment> comments = gson.fromJson(cursor.getString(COMMENTS), type);

        interpretation.setId(cursor.getString(ID));
        interpretation.setCreated(cursor.getString(CREATED));
        interpretation.setLastUpdated(cursor.getString(LAST_UPDATED));
        interpretation.setAccess(access);
        interpretation.setType(cursor.getString(TYPE));
        interpretation.setName(cursor.getString(NAME));
        interpretation.setDisplayName(cursor.getString(DISPLAY_NAME));
        interpretation.setText(cursor.getString(TEXT));
        interpretation.setExternalAccess(cursor.getInt(EXTERNAL_ACCESS) == 1);

        interpretation.setMap(map);
        interpretation.setChart(chart);
        interpretation.setReportTable(reportTable);
        interpretation.setDataSet(dataset);
        interpretation.setOrganisationUnit(unit);
        interpretation.setPeriod(period);
        interpretation.setUser(user);
        interpretation.setComments(comments);

        DbRow<Interpretation> holder = new DbRow<>();
        holder.setItem(interpretation);
        holder.setId(cursor.getInt(DB_ID));
        return holder;
    }

    public static Map<String, Interpretation> toMap(List<Interpretation> interpretations) {
        Map<String, Interpretation> map = new HashMap<>();
        for (Interpretation interpretation: interpretations) {
            map.put(interpretation.getId(), interpretation);
        }
        return map;
    }

    private static boolean isCorrect(Interpretation interpretation) {
        return (interpretation != null &&
                interpretation.getAccess() != null &&
                !isEmpty(interpretation.getId()) &&
                !isEmpty(interpretation.getCreated()) &&
                !isEmpty(interpretation.getLastUpdated()) &&
                !isEmpty(interpretation.getType()));
    }

    public static ContentProviderOperation delete(DbRow<Interpretation> dbItem) {
        Uri uri = ContentUris.withAppendedId(
                Interpretations.CONTENT_URI, dbItem.getId()
        );
        return ContentProviderOperation.newDelete(uri).build();
    }

    public static ContentProviderOperation update(DbRow<Interpretation> dbItem,
                                                  Interpretation interpretation) {
        if (!isCorrect(interpretation)) {
            return null;
        }

        Uri uri = ContentUris.withAppendedId(
                Interpretations.CONTENT_URI, dbItem.getId()
        );
        return ContentProviderOperation.newUpdate(uri)
                .withValues(InterpretationHandler.toContentValues(interpretation))
                .withValue(Interpretations.STATE, State.GETTING.toString())
                .build();
    }

    public static ContentProviderOperation insert(Interpretation interpretation) {
        if (!isCorrect(interpretation)) {
            return null;
        }
        return ContentProviderOperation.newInsert(Interpretations.CONTENT_URI)
                .withValues(InterpretationHandler.toContentValues(interpretation))
                .withValue(Interpretations.STATE, State.GETTING.toString())
                .build();
    }
}
