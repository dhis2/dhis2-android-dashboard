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

package org.dhis2.android.dashboard.api.network.tasks;

import android.net.Uri;

import org.dhis2.android.dashboard.api.persistence.models.UserAccount;
import org.dhis2.android.dashboard.api.network.APIException;
import org.dhis2.android.dashboard.api.network.http.ApiRequest;
import org.dhis2.android.dashboard.api.network.http.Request;
import org.dhis2.android.dashboard.api.network.http.RequestBuilder;
import org.dhis2.android.dashboard.api.network.managers.INetworkManager;
import org.dhis2.android.dashboard.api.network.models.Credentials;

import static org.dhis2.android.dashboard.api.utils.Preconditions.isNull;

public final class GetUserAccountTask implements ITask<UserAccount> {
    private final ApiRequest<UserAccount, UserAccount> mRequest;

    public GetUserAccountTask(INetworkManager manager,
                              Uri serverUri, Credentials credentials) {
        isNull(serverUri, "Server URI must not be null");
        isNull(credentials, "User credentials must not be null");

        String base64Credentials = manager.getBase64Manager()
                .toBase64(credentials);
        String url = serverUri.buildUpon()
                .appendEncodedPath("api/me/")
                .appendQueryParameter("fields", buildQueryParams())
                .build().toString();
        Request request = RequestBuilder.forUri(url)
                .header("Authorization", base64Credentials)
                .header("Accept", "application/json")
                .build();

        mRequest = new ApiRequest<>(
                request, manager.getHttpManager(), manager.getLogManager(),
                manager.getJsonManager().getUserAccountConverter()
        );
    }

    private static String buildQueryParams() {
        StringBuilder builder = new StringBuilder();
        builder.append("id");
        builder.append(",");
        builder.append("created");
        builder.append(",");
        builder.append("lastUpdated");
        builder.append(",");
        builder.append("name");
        builder.append(",");
        builder.append("displayName");
        builder.append(",");
        builder.append("firstName");
        builder.append(",");
        builder.append("surname");
        builder.append(",");
        builder.append("gender");
        builder.append(",");
        builder.append("birthday");
        builder.append(",");
        builder.append("introduction");
        builder.append(",");
        builder.append("education");
        builder.append(",");
        builder.append("employer");
        builder.append(",");
        builder.append("interests");
        builder.append(",");
        builder.append("jobTitle");
        builder.append(",");
        builder.append("languages");
        builder.append(",");
        builder.append("email");
        builder.append(",");
        builder.append("phoneNumber");
        return builder.toString();
    }

    @Override
    public UserAccount run() throws APIException {
        return mRequest.request();
    }
}
