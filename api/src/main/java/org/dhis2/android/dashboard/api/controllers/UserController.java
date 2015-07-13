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

package org.dhis2.android.dashboard.api.controllers;

import com.raizlabs.android.dbflow.sql.language.Delete;
import com.squareup.okhttp.HttpUrl;

import org.dhis2.android.dashboard.api.models.Dashboard;
import org.dhis2.android.dashboard.api.models.DashboardElement;
import org.dhis2.android.dashboard.api.models.DashboardItem;
import org.dhis2.android.dashboard.api.models.DashboardItemContent;
import org.dhis2.android.dashboard.api.models.Interpretation;
import org.dhis2.android.dashboard.api.models.InterpretationComment;
import org.dhis2.android.dashboard.api.models.InterpretationElement;
import org.dhis2.android.dashboard.api.models.User;
import org.dhis2.android.dashboard.api.models.UserAccount;
import org.dhis2.android.dashboard.api.models.meta.Credentials;
import org.dhis2.android.dashboard.api.models.meta.Session;
import org.dhis2.android.dashboard.api.network.APIException;
import org.dhis2.android.dashboard.api.network.DhisApi;
import org.dhis2.android.dashboard.api.persistence.preferences.SessionManager;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Araz Abishov <araz.abishov.gsoc@gmail.com>.
 */
final class UserController {
    private final DhisApi mDhisApi;

    public UserController(DhisApi dhisApi) {
        mDhisApi = dhisApi;
    }

    public UserAccount logInUser(HttpUrl serverUrl, Credentials credentials) throws APIException {
        final Map<String, String> QUERY_PARAMS = new HashMap<>();
        QUERY_PARAMS.put("fields", "id,created,lastUpdated,name,displayName," +
                "firstName,surname,gender,birthday,introduction," +
                "education,employer,interests,jobTitle,languages,email,phoneNumber," +
                "organisationUnits[id]");
        UserAccount userAccount = mDhisApi
                .getCurrentUserAccount(QUERY_PARAMS);

        // if we got here, it means http
        // request was executed successfully

        /* save user credentials */
        Session session = new Session(serverUrl, credentials);
        SessionManager.getInstance().put(session);

        /* save user account details */
        userAccount.save();

        return userAccount;
    }

    public void logOut() {
        SessionManager sessionManager = SessionManager
                .getInstance();
        sessionManager.delete();

        // remove data
        Delete.tables(
                Dashboard.class,
                DashboardElement.class,
                DashboardItem.class,
                DashboardItemContent.class,
                Interpretation.class,
                InterpretationComment.class,
                InterpretationElement.class,
                User.class,
                UserAccount.class
        );
    }

    public UserAccount updateUserAccount() throws APIException {
        final Map<String, String> QUERY_PARAMS = new HashMap<>();
        QUERY_PARAMS.put("fields", "id,created,lastUpdated,name,displayName," +
                "firstName,surname,gender,birthday,introduction," +
                "education,employer,interests,jobTitle,languages,email,phoneNumber," +
                "organisationUnits[id]");
        UserAccount userAccount =
                mDhisApi.getCurrentUserAccount(QUERY_PARAMS);
        // update userAccount in database
        userAccount.save();
        return userAccount;
    }
}
