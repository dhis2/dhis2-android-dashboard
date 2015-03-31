package org.hisp.dhis.mobile.datacapture.api.android.processors;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import org.hisp.dhis.mobile.datacapture.api.APIException;
import org.hisp.dhis.mobile.datacapture.api.android.events.InterpretationUpdateTextEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.OnInterpretationTextUpdateEvent;
import org.hisp.dhis.mobile.datacapture.io.handlers.InterpretationHandler;
import org.hisp.dhis.mobile.datacapture.api.android.models.DbRow;
import org.hisp.dhis.mobile.datacapture.api.android.models.ResponseHolder;
import org.hisp.dhis.mobile.datacapture.api.android.models.State;
import org.hisp.dhis.mobile.datacapture.api.managers.DHISManager;
import org.hisp.dhis.mobile.datacapture.api.models.Interpretation;
import org.hisp.dhis.mobile.datacapture.api.network.ApiRequestCallback;
import org.hisp.dhis.mobile.datacapture.api.network.Response;
import org.hisp.dhis.mobile.datacapture.io.DBContract.Interpretations;

public class InterpretationUpdateTextProcessor extends AbsProcessor<InterpretationUpdateTextEvent, OnInterpretationTextUpdateEvent> {

    public InterpretationUpdateTextProcessor(Context context,
                                             InterpretationUpdateTextEvent event) {
        super(context, event);
    }

    @Override
    public OnInterpretationTextUpdateEvent process() {
        final DbRow<Interpretation> dbItem = readInterpretation();
        final ResponseHolder<String> holder = new ResponseHolder<>();
        final OnInterpretationTextUpdateEvent event = new OnInterpretationTextUpdateEvent();

        updateInterpretation(State.PUTTING);
        DHISManager.getInstance().updateInterpretationText(new ApiRequestCallback<String>() {
            @Override
            public void onSuccess(Response response, String s) {
                holder.setResponse(response);
                holder.setItem(s);
                updateInterpretation(State.GETTING);
            }

            @Override
            public void onFailure(APIException e) {
                e.printStackTrace();
                holder.setException(e);
            }
        }, dbItem.getItem().getId(), getEvent().getText());

        event.setResponseHolder(holder);
        return event;
    }

    private DbRow<Interpretation> readInterpretation() {
        Uri uri = ContentUris.withAppendedId(
                Interpretations.CONTENT_URI, getEvent().getDbId()
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

    private void updateInterpretation(State state) {
        Uri uri = ContentUris.withAppendedId(
                Interpretations.CONTENT_URI, getEvent().getDbId()
        );

        ContentValues values = new ContentValues();
        values.put(Interpretations.TEXT, getEvent().getText());
        values.put(Interpretations.STATE, state.toString());
        getContext().getContentResolver().update(uri, values, null, null);
    }
}
