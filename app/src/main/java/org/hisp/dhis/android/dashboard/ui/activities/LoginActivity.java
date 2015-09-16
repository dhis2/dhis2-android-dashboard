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

import android.text.Editable;
import android.widget.Toast;

import com.squareup.okhttp.HttpUrl;

import org.hisp.dhis.android.sdk.core.api.Dhis2;
import org.hisp.dhis.android.sdk.core.persistence.models.common.meta.Credentials;
import org.hisp.dhis.android.sdk.models.user.UserAccount;
import org.hisp.dhis.android.sdk.ui.activities.AbsLoginActivity;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public final class LoginActivity extends AbsLoginActivity {
    private Subscription mSubscription;

    @Override
    protected void onLogInButtonClicked(Editable url, Editable username, Editable password) {
        Toast.makeText(getApplicationContext(), "Message", Toast.LENGTH_SHORT).show();
        onStartLoading();

        final HttpUrl serverUrl = HttpUrl.parse(url.toString());
        final Credentials credentials = new Credentials(username.toString(), password.toString());

        Observable.OnSubscribe<UserAccount> observable = new Observable.OnSubscribe<UserAccount>() {
            @Override
            public void call(Subscriber<? super UserAccount> subscriber) {
                try {
                    UserAccount userAccount = Dhis2.logIn(serverUrl, credentials);
                    subscriber.onNext(userAccount);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        };

        Subscriber<UserAccount> userAccountSubscriber = new Subscriber<UserAccount>() {

            @Override
            public void onCompleted() {
                onFinishLoading();
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(getApplicationContext(),
                        "Exception, SHiT!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(UserAccount userAccount) {
                Toast.makeText(getApplicationContext(),
                        "UserAccount: " + userAccount.getDisplayName(),
                        Toast.LENGTH_SHORT).show();
            }
        };

        mSubscription = Observable.create(observable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userAccountSubscriber);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        /* Unsubscribe in order not to leak activity */
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
    }
}
