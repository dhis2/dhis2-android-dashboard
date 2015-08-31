package org.hisp.dhis.android.dashboard.api.api;

import android.content.Context;

import com.squareup.okhttp.HttpUrl;

import org.hisp.dhis.android.dashboard.api.models.common.meta.Credentials;
import org.hisp.dhis.android.dashboard.api.models.common.meta.Session;
import org.hisp.dhis.android.dashboard.api.models.user.UserAccount;
import org.hisp.dhis.android.dashboard.api.network.APIException;
import org.hisp.dhis.android.dashboard.api.network.DhisApi;
import org.hisp.dhis.android.dashboard.api.network.RepoManager;
import org.hisp.dhis.android.dashboard.api.persistence.preferences.DateTimeManager;
import org.hisp.dhis.android.dashboard.api.persistence.preferences.LastUpdatedManager;

/**
 * Created by arazabishov on 8/28/15.
 */
public final class Dhis2 {
    private static Dhis2 dhis2;
    private Session session;
    private DhisApi dhisApi;

    private DashboardScope dashboardScope;
    private InterpretationScope interpretationScope;
    private UserAccountScope userAccountScope;

    private Dhis2(Context context) {
        Models.init(context);
        Services.init(context);

        LastUpdatedManager.init(context);
        DateTimeManager.init(context);

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
        return isUserLoggedIn(getInstance().session);
    }

    public static boolean isUserInvalidated() {
        return getInstance().session.getServerUrl() != null &&
                getInstance().session.getCredentials() == null;
    }

    public static HttpUrl getServerUrl() {
        return getInstance().session.getServerUrl();
    }

    public static Credentials getUserCredentials() {
        return getInstance().session.getCredentials();
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
        // we need to nullify all controllers since they contain
        // reference to DhisApi with outdated session
        reset();

        session = LastUpdatedManager.getInstance().get();
        if (isUserLoggedIn(session)) {
            init();
        }
    }

    private void reset() {
        Controllers.reset();
        // dhisApi = null;

        dashboardScope = null;
        interpretationScope = null;
        userAccountScope = null;
    }

    private void init() {
        dhisApi = RepoManager.createService();

        // reinitializing controllers with fresh DhisApi instance
        Controllers.init(dhisApi);

        dashboardScope = new DashboardScope(Controllers.dashboards(),
                Services.dashboards(), Services.dashboardItems(), Services.dashboardElements());
        interpretationScope = new InterpretationScope(Controllers.interpretations(),
                Services.interpretations(), Services.interpretationElements(), Services.interpretationComments());
        userAccountScope = new UserAccountScope(Controllers.userAccount(), Services.userAccount());
    }

    private UserAccount signIn(HttpUrl serverUrl, Credentials credentials) throws APIException {
        Controllers.init(RepoManager.createService(serverUrl, credentials));
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
