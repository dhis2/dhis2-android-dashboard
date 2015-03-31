package org.hisp.dhis.mobile.datacapture.api.android.processors;

import org.hisp.dhis.mobile.datacapture.api.APIException;
import org.hisp.dhis.mobile.datacapture.api.android.events.DashboardCreateEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.OnDashboardCreateEvent;
import org.hisp.dhis.mobile.datacapture.api.android.models.ResponseHolder;
import org.hisp.dhis.mobile.datacapture.api.managers.DHISManager;
import org.hisp.dhis.mobile.datacapture.api.network.ApiRequestCallback;
import org.hisp.dhis.mobile.datacapture.api.network.Response;

public class DashboardCreateProcessor extends AbsProcessor<DashboardCreateEvent, OnDashboardCreateEvent> {

    public DashboardCreateProcessor(DashboardCreateEvent event) {
        super(event);
    }

    @Override
    public OnDashboardCreateEvent process() {
        final ResponseHolder<String> holder = new ResponseHolder<>();
        final OnDashboardCreateEvent event = new OnDashboardCreateEvent();

        DHISManager.getInstance().postDashboard(new ApiRequestCallback<String>() {
            @Override
            public void onSuccess(Response response, String s) {
                holder.setItem(s);
                holder.setResponse(response);
            }

            @Override
            public void onFailure(APIException e) {
                holder.setException(e);
            }
        }, getEvent().getDashboardName());

        event.setResponseHolder(holder);
        return event;
    }
}
