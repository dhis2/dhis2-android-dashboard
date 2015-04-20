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

package org.dhis2.android.dashboard.api.persistence.handlers;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import org.dhis2.android.dashboard.api.network.models.Credentials;
import org.dhis2.android.dashboard.api.persistence.models.Session;

import static org.dhis2.android.dashboard.api.utils.Preconditions.isNull;

public final class SessionHandler implements IPreferenceHandler<Session> {
    private static final String META_DATA = "preferences:Session";
    private static final String SERVER_URI = "key:Uri";
    private static final String USERNAME = "key:username";
    private static final String PASSWORD = "key:password";

    private SharedPreferences mPrefs;

    public SessionHandler(Context context) {
        isNull(context, "Context object must not be null");
        mPrefs = context.getSharedPreferences(META_DATA, Context.MODE_PRIVATE);
    }

    @Override
    public Session get() {
        String serverUriString = getString(SERVER_URI);
        String userNameString = getString(USERNAME);
        String passwordString = getString(PASSWORD);

        Uri serverUri = null;
        if (serverUriString != null) {
            serverUri = Uri.parse(serverUriString);
        }

        Credentials credentials = null;
        if (userNameString != null && passwordString != null) {
            credentials = new Credentials(
                    userNameString, passwordString
            );
        }
        return new Session(serverUri, credentials);
    }

    @Override
    public void put(Session session) {
        isNull(session, "Session object must not be null");
        Uri serverUri = session.getServerUri();
        Credentials credentials = session.getCredentials();

        String uri = null;
        String username = null;
        String password = null;

        if (serverUri != null) {
            uri = serverUri.toString();
        }

        if (credentials != null) {
            username = credentials.getUsername();
            password = credentials.getPassword();
        }

        putString(SERVER_URI, uri);
        putString(USERNAME, username);
        putString(PASSWORD, password);
    }

    @Override
    public void delete() {
        mPrefs.edit().clear().apply();
    }

    public void invalidate() {
        putString(USERNAME, null);
        putString(PASSWORD, null);
    }

    public boolean isInvalid() {
        return getString(USERNAME) == null &&
                getString(PASSWORD) == null;
    }

    private void putString(String key, String value) {
        mPrefs.edit().putString(key, value).apply();
    }

    private String getString(String key) {
        return mPrefs.getString(key, null);
    }
}
