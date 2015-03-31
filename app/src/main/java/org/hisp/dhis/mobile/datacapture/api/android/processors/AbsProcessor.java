package org.hisp.dhis.mobile.datacapture.api.android.processors;

import android.content.Context;
import android.os.AsyncTask;

import org.hisp.dhis.mobile.datacapture.utils.BusProvider;

import static org.hisp.dhis.mobile.datacapture.api.utils.Preconditions.isNull;

public abstract class AbsProcessor<InEventType, OutEventType> extends AsyncTask<Void, Void, OutEventType>
        implements IProcessor<OutEventType> {
    private Context mContext;
    private InEventType mEvent;

    public AbsProcessor(Context context) {
        mContext = isNull(context, "Context must not be null");
    }

    public AbsProcessor(InEventType event) {
        mEvent = isNull(event, "Event must not be null");
    }

    public AbsProcessor(Context context, InEventType event) {
        mContext = isNull(context, "Context must not be null");
        mEvent = isNull(event, "Event must not be null");
    }

    protected InEventType getEvent() {
        return mEvent;
    }

    protected Context getContext() {
        return mContext;
    }


    @Override
    protected OutEventType doInBackground(Void... params) {
        return process();
    }

    @Override
    protected void onPostExecute(OutEventType event) {
        BusProvider.getInstance().post(event);
    }
}
