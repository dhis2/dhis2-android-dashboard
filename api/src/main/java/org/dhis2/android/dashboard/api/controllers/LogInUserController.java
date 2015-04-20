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

package org.dhis2.android.dashboard.api.controllers;

import android.net.Uri;

import org.dhis2.android.dashboard.api.DhisManager;
import org.dhis2.android.dashboard.api.persistence.models.UserAccount;
import org.dhis2.android.dashboard.api.network.APIException;
import org.dhis2.android.dashboard.api.network.models.Credentials;
import org.dhis2.android.dashboard.api.network.tasks.ITask;
import org.dhis2.android.dashboard.api.network.tasks.LoginUserTask;
import org.dhis2.android.dashboard.api.persistence.handlers.SessionHandler;
import org.dhis2.android.dashboard.api.persistence.handlers.UserAccountHandler;
import org.dhis2.android.dashboard.api.persistence.models.Session;

import static org.dhis2.android.dashboard.api.utils.Preconditions.isNull;

public final class LogInUserController implements IController<UserAccount> {
    private final DhisManager mDhisManager;
    private final Uri mServerUri;
    private final Credentials mCredentials;
    private final SessionHandler mSessionHandler;
    private final UserAccountHandler mUserAccountHandler;

    public LogInUserController(DhisManager dhisManager,
                               SessionHandler sessionHandler,
                               UserAccountHandler userAccountHandler,
                               Uri serverUri, Credentials credentials) {
        mDhisManager = isNull(dhisManager, "DhisManager must not be null");
        mSessionHandler = isNull(sessionHandler, "SessionHandler must not be null");
        mUserAccountHandler = isNull(userAccountHandler, "UserAccountHandler must not be null");
        mServerUri = isNull(serverUri, "Server URI must not be null");
        mCredentials = isNull(credentials, "User credentials must not be null");
    }

    @Override
    public UserAccount run() throws APIException {
        UserAccount userAccount = getUserAccount();
        // if we got here, it means http
        // request was executed successfully
        saveMetaData();
        saveUserAccount(userAccount);
        return userAccount;
    }

    private UserAccount getUserAccount() {
        ITask<UserAccount> task = new LoginUserTask(
                mDhisManager, mServerUri, mCredentials
        );
        return task.run();
    }

    private void saveMetaData() {
        Session session = new Session(
                mServerUri, mCredentials
        );
        mSessionHandler.put(session);
    }

    private void saveUserAccount(UserAccount userAccount) {
        mUserAccountHandler.put(userAccount);
    }
}
