package org.hisp.dhis.android.dashboard.api.network;

import com.squareup.okhttp.HttpUrl;

import org.hisp.dhis.android.dashboard.api.models.common.meta.Credentials;
import org.hisp.dhis.android.dashboard.api.models.common.meta.Session;
import org.hisp.dhis.android.dashboard.api.network.RepositoryManager.AuthInterceptor;
import org.hisp.dhis.android.dashboard.api.network.RepositoryManager.ServerEndpoint;

/**
 * Created by arazabishov on 9/1/15.
 */
public final class DhisApi {
    private final ServerEndpoint endpoint;
    private final AuthInterceptor interceptor;
    private final IDhisApi dhisApi;

    private Session session;

    DhisApi(ServerEndpoint endpoint, AuthInterceptor interceptor, IDhisApi dhisApi) {
        this.endpoint = endpoint;
        this.interceptor = interceptor;
        this.dhisApi = dhisApi;
    }

    public void setSession(Session session) {
        this.session = session;

        HttpUrl url = null;
        Credentials credentials = null;

        if (session != null) {
            url = session.getServerUrl();
            credentials = session.getCredentials();
        }

        setServerUrl(url);
        setCredentials(credentials);
    }

    public Session getSession() {
        return session;
    }

    private void setServerUrl(HttpUrl httpUrl) {
        String url = null;
        if (httpUrl != null) {
            url = httpUrl.newBuilder()
                    .addPathSegment("api")
                    .build().toString();
        }
        endpoint.setServerUrl(url);
    }

    private void setCredentials(Credentials credentials) {
        String username = null;
        String password = null;

        if (credentials != null) {
            username = credentials.getUsername();
            password = credentials.getPassword();
        }
        interceptor.setUsername(username);
        interceptor.setPassword(password);
    }

    public IDhisApi getApi() {
        return dhisApi;
    }
}
