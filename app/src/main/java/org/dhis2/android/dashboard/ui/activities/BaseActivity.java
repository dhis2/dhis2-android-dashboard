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

package org.dhis2.android.dashboard.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.dhis2.android.dashboard.R;
import org.dhis2.android.dashboard.api.DhisApplication;
import org.dhis2.android.dashboard.api.DhisManager;
import org.dhis2.android.dashboard.api.DhisService;
import org.dhis2.android.dashboard.api.network.APIException;
import org.dhis2.android.dashboard.api.utils.EventBusProvider;

import static android.widget.Toast.LENGTH_SHORT;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;

public class BaseActivity extends AppCompatActivity {

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

    protected DhisService getDhisService() {
        return ((DhisApplication) getApplication()).getDhisService();
    }

    protected DhisManager getDhisManager() {
        return ((DhisApplication) getApplication()).getDhisManager();
    }

    protected void showMessage(CharSequence message) {
        Toast.makeText(
                getBaseContext(), message, LENGTH_SHORT
        ).show();
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
