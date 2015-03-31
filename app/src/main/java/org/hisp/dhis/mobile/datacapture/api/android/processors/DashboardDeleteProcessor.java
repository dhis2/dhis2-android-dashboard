package org.hisp.dhis.mobile.datacapture.api.android.processors;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import org.hisp.dhis.mobile.datacapture.api.android.models.DbRow;
import org.hisp.dhis.mobile.datacapture.utils.BusProvider;
import org.hisp.dhis.mobile.datacapture.api.APIException;
import org.hisp.dhis.mobile.datacapture.api.android.events.DashboardDeleteEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.OnDashboardDeleteEvent;
import org.hisp.dhis.mobile.datacapture.io.handlers.DashboardHandler;
import org.hisp.dhis.mobile.datacapture.api.android.models.ResponseHolder;
import org.hisp.dhis.mobile.datacapture.api.android.models.State;
import org.hisp.dhis.mobile.datacapture.api.managers.DHISManager;
import org.hisp.dhis.mobile.datacapture.api.models.Dashboard;
import org.hisp.dhis.mobile.datacapture.api.network.ApiRequestCallback;
import org.hisp.dhis.mobile.datacapture.api.network.Response;
import org.hisp.dhis.mobile.datacapture.io.DBContract.Dashboards;

import static org.hisp.dhis.mobile.datacapture.api.utils.Preconditions.isNull;

public class DashboardDeleteProcessor extends AsyncTask<Void, Void, OnDashboardDeleteEvent> {
    private Context mContext;
    private DashboardDeleteEvent mEvent;

    public DashboardDeleteProcessor(Context context, DashboardDeleteEvent event) {
        mContext = isNull(context, "Context must not be null");
        mEvent = isNull(event, "DashboardDeleteEvent must not be null");
    }

    private DbRow<Dashboard> readDashboard() {
        Uri uri = ContentUris.withAppendedId(
                Dashboards.CONTENT_URI, mEvent.getDashboardDbId());
        Cursor cursor = mContext.getContentResolver().query(
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

    private void deleteDashboard() {
        Uri uri = ContentUris.withAppendedId(
                Dashboards.CONTENT_URI, mEvent.getDashboardDbId());
        mContext.getContentResolver().delete(uri, null, null);
    }

    @Override
    protected OnDashboardDeleteEvent doInBackground(Void... params) {
        final DbRow<Dashboard> dbItem = readDashboard();
        final ResponseHolder<String> holder = new ResponseHolder<>();
        final OnDashboardDeleteEvent event = new OnDashboardDeleteEvent();

        updateDashboardState(State.DELETING);
        DHISManager.getInstance().deleteDashboard(new ApiRequestCallback<String>() {
            @Override
            public void onSuccess(Response response, String string) {
                holder.setItem(string);
                holder.setResponse(response);
                deleteDashboard();
            }

            @Override
            public void onFailure(APIException e) {
                holder.setException(e);
            }
        }, dbItem.getItem().getId());

        event.setResponseHolder(holder);
        return event;
    }

    @Override
    protected void onPostExecute(OnDashboardDeleteEvent event) {
        BusProvider.getInstance().post(event);
    }

    private void updateDashboardState(State state) {
        Uri uri = ContentUris.withAppendedId(
                Dashboards.CONTENT_URI, mEvent.getDashboardDbId());
        ContentValues values = new ContentValues();
        values.put(Dashboards.STATE, state.toString());
        mContext.getContentResolver().update(uri, values, null, null);
    }
}
