package org.hisp.dhis.mobile.datacapture.api.android.processors;

import org.hisp.dhis.mobile.datacapture.api.APIException;
import org.hisp.dhis.mobile.datacapture.api.android.events.GetReportTableEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.OnGotReportTableEvent;
import org.hisp.dhis.mobile.datacapture.api.android.models.ResponseHolder;
import org.hisp.dhis.mobile.datacapture.api.managers.DHISManager;
import org.hisp.dhis.mobile.datacapture.api.network.ApiRequestCallback;
import org.hisp.dhis.mobile.datacapture.api.network.Response;

public class GetReportTableProcessor extends AbsProcessor<GetReportTableEvent, OnGotReportTableEvent> {

    public GetReportTableProcessor(GetReportTableEvent event) {
        super(event);
    }

    @Override
    public OnGotReportTableEvent process() {
        final ResponseHolder<String> holder = new ResponseHolder<>();
        DHISManager.getInstance().getReportTableData(new ApiRequestCallback<String>() {
            @Override
            public void onSuccess(Response response, String s) {
                holder.setResponse(response);
                holder.setItem(s);
            }

            @Override
            public void onFailure(APIException e) {
                holder.setException(e);
            }
        }, getEvent().getId());

        OnGotReportTableEvent event = new OnGotReportTableEvent();
        event.setResponseHolder(holder);
        return event;
    }
}
