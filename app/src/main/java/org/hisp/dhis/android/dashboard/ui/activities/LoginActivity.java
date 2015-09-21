/*
 * Copyright (c) 2015, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.dashboard.ui.activities;

import android.content.Intent;
import android.text.Editable;

import com.squareup.okhttp.HttpUrl;
import com.squareup.otto.Subscribe;

import org.hisp.dhis.android.dashboard.DhisService;
import org.hisp.dhis.android.dashboard.job.NetworkJob;
import org.hisp.dhis.android.dashboard.utils.EventBusProvider;
import org.hisp.dhis.android.sdk.core.persistence.models.common.meta.Credentials;
import org.hisp.dhis.android.sdk.core.persistence.models.common.meta.ResponseHolder;
import org.hisp.dhis.android.sdk.core.persistence.preferences.ResourceType;
import org.hisp.dhis.android.sdk.models.user.UserAccount;
import org.hisp.dhis.android.sdk.ui.activities.AbsLoginActivity;

public final class LoginActivity extends AbsLoginActivity {

    @Override
    protected void onResume() {
        super.onResume();
        EventBusProvider.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBusProvider.unregister(this);
    }

    @Override
    protected void onLogInButtonClicked(Editable url, Editable username, Editable password) {
        onStartLoading();

        final HttpUrl serverUrl = HttpUrl.parse(url.toString());
        final Credentials credentials = new Credentials(username.toString(), password.toString());

        DhisService.getInstance().logInUser(serverUrl, credentials);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onResultReceived(NetworkJob.NetworkJobResult<UserAccount> jobResult) {
        if (ResourceType.USERS.equals(jobResult.getResourceType())) {
            ResponseHolder<UserAccount> responseHolder = jobResult.getResponseHolder();

            onFinishLoading();
            if (responseHolder.getApiException() == null) {
                startActivity(new Intent(this, MenuActivity.class));
                finish();
            } else {
                // showApiExceptionMessage(responseHolder.getApiException());
            }
        }
    }
}
