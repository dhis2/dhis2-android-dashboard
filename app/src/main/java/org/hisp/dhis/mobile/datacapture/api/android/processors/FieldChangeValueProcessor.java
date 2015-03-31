package org.hisp.dhis.mobile.datacapture.api.android.processors;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import org.hisp.dhis.mobile.datacapture.api.android.events.FieldValueChangeEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.OnFieldValueChangedEvent;
import org.hisp.dhis.mobile.datacapture.io.DBContract.ReportFields;
import org.hisp.dhis.mobile.datacapture.utils.BusProvider;

import static org.hisp.dhis.mobile.datacapture.api.utils.Preconditions.isNull;


public class FieldChangeValueProcessor extends AsyncTask<Void, Void, OnFieldValueChangedEvent> {
    private Context mContext;
    private FieldValueChangeEvent mEvent;

    public FieldChangeValueProcessor(Context context, FieldValueChangeEvent event) {
        mContext = isNull(context, "Context must not be null");
        mEvent = isNull(event, "FieldValueChangeEvent must not be null");
    }

    @Override
    protected OnFieldValueChangedEvent doInBackground(Void... params) {
        Uri uri = ContentUris.withAppendedId(
                ReportFields.CONTENT_URI, mEvent.getFieldId());
        ContentValues values = new ContentValues();
        values.put(ReportFields.VALUE, mEvent.getValue());
        mContext.getContentResolver().update(uri, values, null, null);
        return new OnFieldValueChangedEvent();
    }

    @Override
    protected void onPostExecute(OnFieldValueChangedEvent event) {
        BusProvider.getInstance().post(event);
    }
}
