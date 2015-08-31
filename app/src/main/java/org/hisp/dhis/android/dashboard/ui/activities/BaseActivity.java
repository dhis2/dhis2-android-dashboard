/*
 * Copyright (c) 2015, University of Oslo
 * All rights reserved.
 *
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

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import org.hisp.dhis.android.dashboard.DhisService;
import org.hisp.dhis.android.dashboard.R;
import org.hisp.dhis.android.dashboard.api.job.NetworkJob;
import org.hisp.dhis.android.dashboard.api.network.APIException;
import org.hisp.dhis.android.dashboard.api.utils.EventBusProvider;

import static android.widget.Toast.LENGTH_SHORT;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;

public class BaseActivity extends AppCompatActivity {

    /**
     * Android Service object
     */
    private DhisService mService;

    /**
     * If true, it means we have Service connected to this Activity.
     */
    private boolean mIsBound = false;

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to DhisService, cast the IBinder and get DhisService instance
            DhisService.ServiceBinder binder
                    = (DhisService.ServiceBinder) service;
            mService = binder.getService();
            mIsBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mIsBound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();

        // Bind to LocalService
        Intent intent = new Intent(this, DhisService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Unbind from the service
        if (mIsBound) {
            unbindService(mConnection);
            mIsBound = false;
        }
    }

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

    public DhisService getDhisService() {
        return mService;
    }

    public boolean isDhisServiceBound() {
        return mIsBound;
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onResponseReceived(NetworkJob.NetworkJobResult<?> result) {
        if (result != null && result.getResponseHolder().getApiException() != null) {
            showApiExceptionMessage(result.getResponseHolder().getApiException());
        }
    }

    protected void showMessage(CharSequence message) {
        Toast.makeText(
                getBaseContext(), message, LENGTH_SHORT).show();
    }

    protected void showMessage(int id) {
        showMessage(getString(id));
    }

    protected void showApiExceptionMessage(APIException apiException) {
        apiException.printStackTrace();

        if (apiException.getKind() == APIException.Kind.UNEXPECTED) {
            throw new IllegalArgumentException("Unexpected error");
        }

        if (apiException.getKind() == APIException.Kind.NETWORK) {
            showMessage(R.string.no_network_connection);
            return;
        }

        if (apiException.getKind() == APIException.Kind.CONVERSION) {
            showMessage(R.string.bad_response);
            return;
        }

        int code = apiException.getResponse().getStatus();
        switch (code) {
            case HTTP_UNAUTHORIZED: {
                showMessage(R.string.wrong_credentials);
                break;
            }
            case HTTP_NOT_FOUND: {
                showMessage(getString(R.string.wrong_address));
                break;
            }
        }
    }
}
