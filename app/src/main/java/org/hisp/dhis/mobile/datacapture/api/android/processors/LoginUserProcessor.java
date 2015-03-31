package org.hisp.dhis.mobile.datacapture.api.android.processors;

import android.content.Context;

import org.hisp.dhis.mobile.datacapture.api.APIException;
import org.hisp.dhis.mobile.datacapture.api.android.events.LoginUserEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.OnUserLoginEvent;
import org.hisp.dhis.mobile.datacapture.api.android.models.ResponseHolder;
import org.hisp.dhis.mobile.datacapture.api.managers.DHISManager;
import org.hisp.dhis.mobile.datacapture.api.models.UserAccount;
import org.hisp.dhis.mobile.datacapture.api.network.ApiRequestCallback;
import org.hisp.dhis.mobile.datacapture.api.network.Response;
import org.hisp.dhis.mobile.datacapture.ui.activities.LoginActivity;
import org.hisp.dhis.mobile.datacapture.utils.PreferenceUtils;

public class LoginUserProcessor extends AbsProcessor<LoginUserEvent, OnUserLoginEvent> {

    public LoginUserProcessor(Context context, LoginUserEvent event) {
        super(context, event);
    }

    @Override
    public OnUserLoginEvent process() {
        final ResponseHolder<UserAccount> holder = new ResponseHolder<>();
        final OnUserLoginEvent event = new OnUserLoginEvent();

        DHISManager manager = DHISManager.getInstance();
        manager.setServerUrl(getEvent().getServerUrl());

        final String credentials = DHISManager.getInstance()
                .getBase64Manager().toBase64(
                        getEvent().getUsername(),
                        getEvent().getPassword()
                );
        manager.login(new ApiRequestCallback<UserAccount>() {
            @Override
            public void onSuccess(Response response, UserAccount userAccount) {
                holder.setItem(userAccount);

                String serverUrl = getEvent().getServerUrl();
                DHISManager.getInstance().setServerUrl(serverUrl);
                DHISManager.getInstance().setCredentials(credentials);

                PreferenceUtils.put(getContext(), LoginActivity.SERVER_URL, serverUrl);
                PreferenceUtils.put(getContext(), LoginActivity.USER_CREDENTIALS, credentials);
            }

            @Override
            public void onFailure(APIException e) {
                holder.setException(e);
            }
        }, getEvent().getUsername(), getEvent().getPassword());

        event.setResponseHolder(holder);
        return event;
    }
}