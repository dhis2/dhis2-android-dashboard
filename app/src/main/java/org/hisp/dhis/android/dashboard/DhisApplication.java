/*
 * Copyright (c) 2015, dhis2
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.dashboard;

import android.app.Application;
import android.content.Intent;
import android.widget.Toast;

import org.hisp.dhis.android.dashboard.api.controllers.DhisController;
import org.hisp.dhis.android.dashboard.api.network.APIException;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;

/**
 * @author Araz Abishov <araz.abishov.gsoc@gmail.com>.
 */
public final class DhisApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        DhisController.init(getApplicationContext());
        startService(new Intent(this, DhisService.class));
    }

    protected void showMessage(CharSequence message) {
        Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
    }

    protected void showMessage(int id) {
        showMessage(getString(id));
    }

    public void showApiExceptionMessage(APIException apiException) {
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
