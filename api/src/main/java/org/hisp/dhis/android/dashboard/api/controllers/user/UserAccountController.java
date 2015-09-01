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

package org.hisp.dhis.android.dashboard.api.controllers.user;

import com.squareup.okhttp.HttpUrl;

import org.hisp.dhis.android.dashboard.api.models.common.meta.Credentials;
import org.hisp.dhis.android.dashboard.api.models.common.meta.Session;
import org.hisp.dhis.android.dashboard.api.models.user.IUserAccountStore;
import org.hisp.dhis.android.dashboard.api.models.user.UserAccount;
import org.hisp.dhis.android.dashboard.api.network.APIException;
import org.hisp.dhis.android.dashboard.api.network.IDhisApi;
import org.hisp.dhis.android.dashboard.api.persistence.preferences.LastUpdatedManager;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Araz Abishov <araz.abishov.gsoc@gmail.com>.
 */
public final class UserAccountController implements IUserAccountController {
    private final IDhisApi dhisApi;
    private final IUserAccountStore userAccountStore;

    public UserAccountController(IDhisApi dhisApi, IUserAccountStore userAccountStore) {
        this.dhisApi = dhisApi;
        this.userAccountStore = userAccountStore;
    }

    @Override
    public UserAccount logIn(HttpUrl serverUrl, Credentials credentials) throws APIException {
        final Map<String, String> QUERY_PARAMS = new HashMap<>();
        QUERY_PARAMS.put("fields", "id,created,lastUpdated,name,displayName," +
                "firstName,surname,gender,birthday,introduction," +
                "education,employer,interests,jobTitle,languages,email,phoneNumber," +
                "organisationUnits[id]");

        UserAccount userAccount = dhisApi
                .getCurrentUserAccount(QUERY_PARAMS);

        // if we got here, it means http
        // request was executed successfully

        /* save user credentials */
        Session session = new Session(serverUrl, credentials);
        LastUpdatedManager.getInstance().put(session);

        /* save user account details */
        userAccountStore.save(userAccount);

        return userAccount;
    }

    @Override
    public UserAccount updateAccount() throws APIException {
        final Map<String, String> QUERY_PARAMS = new HashMap<>();
        QUERY_PARAMS.put("fields", "id,created,lastUpdated,name,displayName," +
                "firstName,surname,gender,birthday,introduction," +
                "education,employer,interests,jobTitle,languages,email,phoneNumber," +
                "organisationUnits[id]");

        UserAccount userAccount =
                dhisApi.getCurrentUserAccount(QUERY_PARAMS);

        // update userAccount in database
        userAccountStore.save(userAccount);
        return userAccount;
    }
}
