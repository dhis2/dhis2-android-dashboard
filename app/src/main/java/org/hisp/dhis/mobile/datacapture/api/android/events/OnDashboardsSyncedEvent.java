package org.hisp.dhis.mobile.datacapture.api.android.events;

import org.hisp.dhis.mobile.datacapture.api.android.models.ResponseHolder;

public final class OnDashboardsSyncedEvent {
    private ResponseHolder<String> mSendResponse;
    private ResponseHolder<String> mRetrieveResponse;

    public ResponseHolder<String> getSendResponse() {
        return mSendResponse;
    }

    public void setSendResponse(ResponseHolder<String> sendResponse) {
        this.mSendResponse = sendResponse;
    }

    public ResponseHolder<String> getRetrieveResponse() {
        return mRetrieveResponse;
    }

    public void setRetrieveResponse(ResponseHolder<String> retrieveResponse) {
        this.mRetrieveResponse = retrieveResponse;
    }
}
