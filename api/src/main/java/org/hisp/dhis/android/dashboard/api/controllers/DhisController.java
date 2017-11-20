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

package org.hisp.dhis.android.dashboard.api.controllers;

import static org.hisp.dhis.android.dashboard.api.utils.Preconditions.isNull;

import android.content.Context;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.squareup.okhttp.HttpUrl;

import org.hisp.dhis.android.dashboard.api.models.UserAccount;
import org.hisp.dhis.android.dashboard.api.models.meta.Credentials;
import org.hisp.dhis.android.dashboard.api.models.meta.Session;
import org.hisp.dhis.android.dashboard.api.network.APIException;
import org.hisp.dhis.android.dashboard.api.network.DhisApi;
import org.hisp.dhis.android.dashboard.api.network.RepoManager;
import org.hisp.dhis.android.dashboard.api.persistence.preferences.DateTimeManager;
import org.hisp.dhis.android.dashboard.api.persistence.preferences.LastUpdatedManager;
import org.hisp.dhis.android.dashboard.api.persistence.preferences.SettingsManager;
import org.hisp.dhis.android.dashboard.api.utils.NetworkUtils;

import java.io.IOException;

public class DhisController {
    private static DhisController mDhisController;
    private Session mSession;
    private DhisApi mDhisApi;
    private Context mContext;

    public enum ImageNetworkPolicy {NO_CACHE, CACHE}

    private DhisController(Context context) {
        mContext = context;
        FlowManager.init(context);
        LastUpdatedManager.init(context);
        DateTimeManager.init(context);

        // fetch meta data from disk
        readSession();
    }

    public static void init(Context context) {
        isNull(context, "Context object must not be null");
        if (mDhisController == null) {
            mDhisController = new DhisController(context);
        }
    }

    public static DhisController getInstance() {
        if (mDhisController == null) {
            throw new IllegalArgumentException("You need to call init() first");
        }

        return mDhisController;
    }

    public static String buildImageUrl(String resource, String id, Context context) {
        if (resource.contains(PullImageController.MAPS_ENDPOINT)) {
            return getInstance().getServerUrl().newBuilder()
                    .addPathSegment("api").addPathSegment(resource).addPathSegment(
                            id).addPathSegment(
                            "data.png")
                    .addQueryParameter("width", "500").addQueryParameter("height", "391")
                    .toString();
        } else {
            String widthUserPreference = SettingsManager.getInstance(context).getPreference(
                    (SettingsManager.CHART_WIDTH), SettingsManager.MINIMUM_WIDTH);
            String heightUserPreference = SettingsManager.getInstance(context).getPreference(
                    (SettingsManager.CHART_HEIGHT), SettingsManager.MINIMUM_HEIGHT);
            return getInstance().getServerUrl().newBuilder()
                    .addPathSegment("api").addPathSegment(resource).addPathSegment(
                            id).addPathSegment(
                            "data.png")
                    .addQueryParameter("width", widthUserPreference).addQueryParameter("height",
                            heightUserPreference)
                    .toString();
        }
    }

    public UserAccount logInUser(HttpUrl serverUrl, Credentials credentials) throws APIException {
        if (NetworkUtils.isNetworkAvailable(mContext)) {
            return signInUser(serverUrl, credentials);
        } else {
            throw APIException.networkError(serverUrl.toString(),
                    new IOException("Network exception"));
        }
    }

    public UserAccount confirmUser(Credentials credentials) throws APIException {
        return signInUser(mSession.getServerUrl(), credentials);
    }

    public void logOutUser() throws APIException {
        (new UserController(mDhisApi)).logOut();

        // fetch meta data from disk
        readSession();
    }

    private UserAccount signInUser(HttpUrl serverUrl, Credentials credentials) throws APIException {
        DhisApi dhisApi = RepoManager
                .createService(serverUrl, credentials, mContext);
        UserAccount user = (new UserController(dhisApi)
                .logInUser(serverUrl, credentials));

        // fetch meta data from disk
        readSession();
        return user;
    }

    public void invalidateSession() {
        LastUpdatedManager.getInstance().invalidate();

        // fetch meta data from disk
        readSession();
    }

    public boolean isUserLoggedIn() {
        return mSession.getServerUrl() != null &&
                mSession.getCredentials() != null;
    }

    public boolean isUserInvalidated() {
        return mSession.getServerUrl() != null &&
                mSession.getCredentials() == null;
    }

    private void readSession() {
        mSession = LastUpdatedManager.getInstance().get();
        mDhisApi = null;

        if (isUserLoggedIn()) {
            mDhisApi = RepoManager.createService(
                    mSession.getServerUrl(),
                    mSession.getCredentials()
                    , mContext);
        }
    }

    public HttpUrl getServerUrl() {
        return mSession.getServerUrl();
    }

    public Credentials getUserCredentials() {
        return mSession.getCredentials();
    }

    public void syncDashboardContent() throws APIException {
        (new DashboardController(mDhisApi)).syncDashboardContent();
    }

    public void syncDashboards() throws APIException {
        (new DashboardController(mDhisApi)).syncDashboards();
    }

    public void syncInterpretations() throws APIException {
        (new InterpretationController(mDhisApi)).syncInterpretations();
    }

    public void syncDataMaps() {
        (new MapController(mDhisApi)).syncDataMaps();
    }

    public void pullDashboardImages(ImageNetworkPolicy imageNetworkPolicy, Context context) {
        (new PullImageController(mDhisApi,context)).pullDashboardImages(imageNetworkPolicy);
    }

    public void pullInterpretationImages(ImageNetworkPolicy imageNetworkPolicy, Context context) {
        (new PullImageController(mDhisApi,context)).pullInterpretationImages(imageNetworkPolicy);
    }
}
