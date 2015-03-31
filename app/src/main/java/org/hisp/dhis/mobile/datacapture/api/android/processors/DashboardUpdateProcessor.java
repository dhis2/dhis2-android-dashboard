package org.hisp.dhis.mobile.datacapture.api.android.processors;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import org.hisp.dhis.mobile.datacapture.api.APIException;
import org.hisp.dhis.mobile.datacapture.api.android.events.DashboardUpdateEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.OnDashboardUpdateEvent;
import org.hisp.dhis.mobile.datacapture.io.handlers.DashboardHandler;
import org.hisp.dhis.mobile.datacapture.api.android.models.DbRow;
import org.hisp.dhis.mobile.datacapture.api.android.models.ResponseHolder;
import org.hisp.dhis.mobile.datacapture.api.android.models.State;
import org.hisp.dhis.mobile.datacapture.api.managers.DHISManager;
import org.hisp.dhis.mobile.datacapture.api.models.Dashboard;
import org.hisp.dhis.mobile.datacapture.api.network.ApiRequestCallback;
import org.hisp.dhis.mobile.datacapture.api.network.Response;
import org.hisp.dhis.mobile.datacapture.io.DBContract.Dashboards;

public class DashboardUpdateProcessor extends AbsProcessor<DashboardUpdateEvent, OnDashboardUpdateEvent> {

    public DashboardUpdateProcessor(Context context, DashboardUpdateEvent event) {
        super(context, event);
    }

    private static void updateDashboard(Context context, int dashboardId,
                                        String dashboardName, State state) {
        Uri uri = ContentUris.withAppendedId(Dashboards.CONTENT_URI, dashboardId);
        ContentValues values = new ContentValues();
        values.put(Dashboards.NAME, dashboardName);
        values.put(Dashboards.STATE, state.toString());
        context.getContentResolver().update(uri, values, null, null);
    }

    private static DbRow<Dashboard> readDashboard(Context context, int dashboardId) {
        Uri uri = ContentUris.withAppendedId(Dashboards.CONTENT_URI, dashboardId);
        Cursor cursor = context.getContentResolver().query(
                uri, DashboardHandler.PROJECTION, null, null, null
        );

        DbRow<Dashboard> dashboard = null;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            dashboard = DashboardHandler.fromCursor(cursor);
            cursor.close();
        }

        return dashboard;
    }

    @Override
    public OnDashboardUpdateEvent process() {
        final DbRow<Dashboard> dbItem = readDashboard(
                getContext(), getEvent().getDataBaseId());
        final ResponseHolder<String> holder = new ResponseHolder<>();
        final OnDashboardUpdateEvent event = new OnDashboardUpdateEvent();

        // change values in database first, including the state of record
        updateDashboard(getContext(), dbItem.getId(),
                getEvent().getName(), State.PUTTING);

        DHISManager.getInstance().updateDashboardName(new ApiRequestCallback<String>() {
            @Override
            public void onSuccess(Response response, String s) {
                holder.setItem(s);
                holder.setResponse(response);

                // here updateDashboard is called only for updating
                // state of Dashboard record in database
                updateDashboard(getContext(), dbItem.getId(),
                        getEvent().getName(), State.GETTING);
            }

            @Override
            public void onFailure(APIException e) {
                holder.setException(e);
            }
        }, dbItem.getItem().getId(), getEvent().getName());

        event.setResponseHolder(holder);
        return event;
    }
}
