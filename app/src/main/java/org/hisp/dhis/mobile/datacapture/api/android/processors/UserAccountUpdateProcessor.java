package org.hisp.dhis.mobile.datacapture.api.android.processors;

import android.content.Context;

import org.hisp.dhis.mobile.datacapture.api.APIException;
import org.hisp.dhis.mobile.datacapture.api.android.events.OnUserAccountUpdateEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.UserAccountUpdateEvent;
import org.hisp.dhis.mobile.datacapture.api.android.models.ResponseHolder;
import org.hisp.dhis.mobile.datacapture.api.android.models.State;
import org.hisp.dhis.mobile.datacapture.api.managers.DHISManager;
import org.hisp.dhis.mobile.datacapture.api.models.UserAccount;
import org.hisp.dhis.mobile.datacapture.api.network.ApiRequestCallback;
import org.hisp.dhis.mobile.datacapture.api.network.Response;
import org.hisp.dhis.mobile.datacapture.utils.UserAccountPreferences;

public class UserAccountUpdateProcessor
        extends AbsProcessor<UserAccountUpdateEvent, OnUserAccountUpdateEvent> {

    public UserAccountUpdateProcessor(Context context,
                                      UserAccountUpdateEvent event) {
        super(context, event);
    }

    @Override
    public OnUserAccountUpdateEvent process() {
        final UserAccountPreferences prefs = new UserAccountPreferences(getContext());
        final ResponseHolder<UserAccount> holder = new ResponseHolder<>();
        final OnUserAccountUpdateEvent event = new OnUserAccountUpdateEvent();

        prefs.putState(State.PUTTING);
        DHISManager.getInstance().postUserAccount(new ApiRequestCallback<UserAccount>() {
            @Override
            public void onSuccess(Response response, UserAccount userAccount) {
                holder.setItem(userAccount);
                holder.setResponse(response);
                prefs.putState(State.GETTING);
            }

            @Override
            public void onFailure(APIException e) {
                holder.setException(e);
            }
        }, getEvent().getUserAccount());

        event.setResponseHolder(holder);
        return event;
    }
}
