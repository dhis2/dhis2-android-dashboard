package org.hisp.dhis.mobile.datacapture.api.android.events;

import org.hisp.dhis.mobile.datacapture.api.android.models.ResponseHolder;

public abstract class AbsResponseEvent<T> {
    private ResponseHolder<T> mResponseHolder;

    public ResponseHolder<T> getResponseHolder() {
        return mResponseHolder;
    }

    public void setResponseHolder(ResponseHolder<T> responseHolder) {
        mResponseHolder = responseHolder;
    }
}
