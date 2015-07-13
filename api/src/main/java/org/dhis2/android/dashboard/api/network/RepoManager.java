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

package org.dhis2.android.dashboard.api.network;

import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.dhis2.android.dashboard.api.DhisManager;
import org.dhis2.android.dashboard.api.models.meta.Credentials;
import org.dhis2.android.dashboard.api.utils.ObjectMapperProvider;

import java.io.IOException;
import java.net.HttpURLConnection;

import retrofit.ErrorHandler;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.converter.Converter;
import retrofit.converter.JacksonConverter;

import static com.squareup.okhttp.Credentials.basic;


public final class RepoManager {

    private RepoManager() {
        // no instances
    }

    public static DhisApi createService(HttpUrl serverUrl, Credentials credentials) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(provideServerUrl(serverUrl))
                .setConverter(provideJacksonConverter())
                .setClient(provideOkClient(credentials))
                .setErrorHandler(new RetrofitErrorHandler())
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .build();
        return restAdapter.create(DhisApi.class);
    }

    private static String provideServerUrl(HttpUrl httpUrl) {
        return httpUrl.newBuilder()
                .addPathSegment("api")
                .build().toString();
    }

    private static Converter provideJacksonConverter() {
        return new JacksonConverter(ObjectMapperProvider.getInstance());
    }

    private static OkClient provideOkClient(Credentials credentials) {
        OkHttpClient client = new OkHttpClient();
        client.interceptors().add(provideInterceptor(credentials));
        return new OkClient(client);
    }

    private static Interceptor provideInterceptor(Credentials credentials) {
        return new AuthInterceptor(credentials.getUsername(), credentials.getPassword());
    }

    private static class AuthInterceptor implements Interceptor {
        private final String mUsername;
        private final String mPassword;

        public AuthInterceptor(String username, String password) {
            mUsername = username;
            mPassword = password;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            String base64Credentials = basic(mUsername, mPassword);
            Request request = chain.request()
                    .newBuilder()
                    .addHeader("Authorization", base64Credentials)
                    .build();

            Response response = chain.proceed(request);
            if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED &&
                    DhisManager.getInstance().isUserLoggedIn()) {
                DhisManager.getInstance().invalidateMetaData();
            }
            return response;
        }
    }

    private static class RetrofitErrorHandler implements ErrorHandler {

        @Override
        public Throwable handleError(RetrofitError cause) {
            return APIException.fromRetrofitError(cause);
        }
    }
}
