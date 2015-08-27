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

package org.hisp.dhis.android.dashboard.api.controllers;

import com.raizlabs.android.dbflow.sql.language.Delete;
import com.squareup.okhttp.HttpUrl;

import org.hisp.dhis.android.dashboard.api.models.Models;
import org.hisp.dhis.android.dashboard.api.models.common.meta.Credentials;
import org.hisp.dhis.android.dashboard.api.models.common.meta.Session;
import org.hisp.dhis.android.dashboard.api.models.flow.Dashboard$Flow;
import org.hisp.dhis.android.dashboard.api.models.flow.DashboardElement$Flow;
import org.hisp.dhis.android.dashboard.api.models.flow.DashboardItem$Flow;
import org.hisp.dhis.android.dashboard.api.models.flow.DashboardItemContent$Flow;
import org.hisp.dhis.android.dashboard.api.models.flow.Interpretation$Flow;
import org.hisp.dhis.android.dashboard.api.models.flow.InterpretationComment$Flow;
import org.hisp.dhis.android.dashboard.api.models.flow.InterpretationElement$Flow;
import org.hisp.dhis.android.dashboard.api.models.flow.User$Flow;
import org.hisp.dhis.android.dashboard.api.models.flow.UserAccount$Flow;
import org.hisp.dhis.android.dashboard.api.models.user.UserAccount;
import org.hisp.dhis.android.dashboard.api.network.APIException;
import org.hisp.dhis.android.dashboard.api.network.DhisApi;
import org.hisp.dhis.android.dashboard.api.network.SessionManager;
import org.hisp.dhis.android.dashboard.api.persistence.preferences.DateTimeManager;
import org.hisp.dhis.android.dashboard.api.persistence.preferences.LastUpdatedManager;

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
        LastUpdatedManager.getInstance().put(session);

        /* save user account details */
        // userAccount.save();
        if (!Models.userAccount().query().isEmpty()) {
            Models.userAccount().update(userAccount);
        } else {
            Models.userAccount().insert(userAccount);
        }

        return userAccount;
    }

    public void logOut() {
        LastUpdatedManager.getInstance().delete();
        DateTimeManager.getInstance().delete();
        SessionManager.getInstance().delete();

        // remove data
        Delete.tables(
                Dashboard$Flow.class,
                DashboardElement$Flow.class,
                DashboardItem$Flow.class,
                DashboardItemContent$Flow.class,
                Interpretation$Flow.class,
                InterpretationComment$Flow.class,
                InterpretationElement$Flow.class,
                User$Flow.class,
                UserAccount$Flow.class
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
        // userAccount.save();
        Models.userAccount().update(userAccount);
        return userAccount;
    }
}
