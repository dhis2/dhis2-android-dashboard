package org.hisp.dhis.android.dashboard.api.api;

import android.content.Context;
import android.support.annotation.Nullable;

import com.squareup.okhttp.HttpUrl;

import org.hisp.dhis.android.dashboard.api.models.common.meta.Credentials;
import org.hisp.dhis.android.dashboard.api.models.common.meta.Session;
import org.hisp.dhis.android.dashboard.api.models.user.UserAccount;
import org.hisp.dhis.android.dashboard.api.network.APIException;
import org.hisp.dhis.android.dashboard.api.network.DhisApi;
import org.hisp.dhis.android.dashboard.api.network.RepositoryManager;
import org.hisp.dhis.android.dashboard.api.persistence.preferences.DateTimeManager;
import org.hisp.dhis.android.dashboard.api.persistence.preferences.LastUpdatedManager;

/**
 * Created by arazabishov on 8/28/15.
 */
public final class Dhis2 {
    // Reference to Dhis2 instance
    private static Dhis2 dhis2;

    // Current session information
    private final DhisApi dhisApi;

    private final DashboardScope dashboardScope;
    private final InterpretationScope interpretationScope;
    private final UserAccountScope userAccountScope;

    private Dhis2(Context context) {
        dhisApi = RepositoryManager.createService();

        Models.init(context);
        Services.init(context);
        Controllers.init(dhisApi.getApi());

        LastUpdatedManager.init(context);
        DateTimeManager.init(context);

        dashboardScope = new DashboardScope(Controllers.dashboards(),
                Services.dashboards(), Services.dashboardItems(), Services.dashboardElements());
        interpretationScope = new InterpretationScope(Controllers.interpretations(),
                Services.interpretations(), Services.interpretationElements(), Services.interpretationComments());
        userAccountScope = new UserAccountScope(Controllers.userAccount(), Services.userAccount());

        readSession();
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    // Dhis2 API public methods.
    ////////////////////////////////////////////////////////////////////////////////////////

    public static void init(Context context) {
        if (dhis2 == null) {
            dhis2 = new Dhis2(context);
        }
    }

    public static UserAccount logIn(HttpUrl serverUrl, Credentials credentials) throws APIException {
        return getInstance().signIn(serverUrl, credentials);
    }

    public static void logOut() throws APIException {
        getInstance().userAccountScope.logOut();

        // fetch meta data from disk
        getInstance().readSession();
    }

    public static UserAccount confirmUser(Credentials credentials) throws APIException {
        return getInstance().signIn(getServerUrl(), credentials);
    }

    public static void invalidateSession() {
        LastUpdatedManager.getInstance().invalidate();

        // fetch meta data from disk
        getInstance().readSession();
    }

    public static UserAccount getCurrentUserAccount() {
        return getInstance().userAccountScope.getCurrentUserAccount();
    }

    public static boolean isUserLoggedIn() {
        return isUserLoggedIn(getSession());
    }

    public static boolean isUserInvalidated() {
        return getServerUrl() != null && getUserCredentials() == null;
    }

    @Nullable
    public static HttpUrl getServerUrl() {
        return getSession().getServerUrl();
    }

    @Nullable
    public static Credentials getUserCredentials() {
        return getSession().getCredentials();
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    // Utility methods.
    ////////////////////////////////////////////////////////////////////////////////////////

    private static Dhis2 getInstance() {
        if (dhis2 == null) {
            throw new IllegalArgumentException("You have to call init() method first.");
        }

        return dhis2;
    }

    private void readSession() {
        dhisApi.setSession(null);

        Session session = LastUpdatedManager.getInstance().get();
        if (isUserLoggedIn(session)) {
            dhisApi.setSession(session);
        }
    }

    private static Session getSession() {
        Session session = getInstance().dhisApi.getSession();
        if (session == null) {
            session = new Session();
        }
        return session;
    }

    private UserAccount signIn(HttpUrl serverUrl, Credentials credentials) throws APIException {
        dhisApi.setSession(new Session(serverUrl, credentials));
        UserAccount user = userAccountScope.logIn(serverUrl, credentials);

        // fetch meta data from disk
        readSession();
        return user;
    }

    private static boolean isUserLoggedIn(Session session) {
        return session.getServerUrl() != null && session.getCredentials() != null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    // Scopes
    ////////////////////////////////////////////////////////////////////////////////////////

    public static DashboardScope dashboards() {
        return getInstance().dashboardScope;
    }

    public static InterpretationScope interpretations() {
        return getInstance().interpretationScope;
    }
}
