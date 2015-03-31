package org.hisp.dhis.mobile.datacapture.api.android.models;

import org.hisp.dhis.mobile.datacapture.api.APIException;
import org.hisp.dhis.mobile.datacapture.api.network.Response;

public class ResponseHolder<T> {
    private T item;
    private Response response;
    private APIException exception;

    public APIException getException() {
        return exception;
    }

    public void setException(APIException exception) {
        this.exception = exception;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public T getItem() {
        return item;
    }

    public void setItem(T item) {
        this.item = item;
    }
}
