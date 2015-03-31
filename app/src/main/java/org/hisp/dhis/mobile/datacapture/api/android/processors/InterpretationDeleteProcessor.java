package org.hisp.dhis.mobile.datacapture.api.android.processors;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import org.hisp.dhis.mobile.datacapture.api.APIException;
import org.hisp.dhis.mobile.datacapture.api.android.events.InterpretationDeleteEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.OnInterpretationDeleteEvent;
import org.hisp.dhis.mobile.datacapture.io.handlers.InterpretationHandler;
import org.hisp.dhis.mobile.datacapture.api.android.models.DbRow;
import org.hisp.dhis.mobile.datacapture.api.android.models.ResponseHolder;
import org.hisp.dhis.mobile.datacapture.api.android.models.State;
import org.hisp.dhis.mobile.datacapture.api.managers.DHISManager;
import org.hisp.dhis.mobile.datacapture.api.models.Interpretation;
import org.hisp.dhis.mobile.datacapture.api.network.ApiRequestCallback;
import org.hisp.dhis.mobile.datacapture.api.network.Response;
import org.hisp.dhis.mobile.datacapture.io.DBContract.Interpretations;

public class InterpretationDeleteProcessor extends AbsProcessor<InterpretationDeleteEvent, OnInterpretationDeleteEvent> {

    public InterpretationDeleteProcessor(Context context, InterpretationDeleteEvent event) {
        super(context, event);
    }

    @Override
    public OnInterpretationDeleteEvent process() {
        final DbRow<Interpretation> dbItem = readInterpretation();
        final ResponseHolder<String> holder = new ResponseHolder<>();
        final OnInterpretationDeleteEvent event = new OnInterpretationDeleteEvent();

        updateInterpretationState(State.DELETING);
        DHISManager.getInstance().deleteInterpretation(new ApiRequestCallback<String>() {
            @Override
            public void onSuccess(Response response, String s) {
                holder.setItem(s);
                holder.setResponse(response);
                deleteInterpretation();
            }

            @Override
            public void onFailure(APIException e) {
                holder.setException(e);
            }
        }, dbItem.getItem().getId());

        event.setResponseHolder(holder);
        return event;
    }

    private DbRow<Interpretation> readInterpretation() {
        Uri uri = ContentUris.withAppendedId(
                Interpretations.CONTENT_URI, getEvent().getInterpretationId()
        );
        Cursor cursor = getContext().getContentResolver().query(
                uri, InterpretationHandler.PROJECTION, null, null, null
        );

        DbRow<Interpretation> dbItem = null;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            dbItem = InterpretationHandler.fromCursor(cursor);
            cursor.close();
        }

        return dbItem;
    }

    private void deleteInterpretation() {
        Uri uri = ContentUris.withAppendedId(
                Interpretations.CONTENT_URI, getEvent().getInterpretationId()
        );

        getContext().getContentResolver().delete(uri, null, null);
    }

    private void updateInterpretationState(State state) {
        Uri uri = ContentUris.withAppendedId(
                Interpretations.CONTENT_URI, getEvent().getInterpretationId()
        );

        ContentValues values = new ContentValues();
        values.put(Interpretations.STATE, state.toString());
        getContext().getContentResolver().update(uri, values, null, null);
    }
}
