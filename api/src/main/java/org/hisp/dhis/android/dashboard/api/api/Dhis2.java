package org.hisp.dhis.android.dashboard.api.api;

import android.content.Context;

import com.squareup.okhttp.HttpUrl;

import org.hisp.dhis.android.dashboard.api.controllers.DashboardController;
import org.hisp.dhis.android.dashboard.api.controllers.InterpretationController;
import org.hisp.dhis.android.dashboard.api.controllers.user.UserAccountController;
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

    private final DashboardScope dashboardScope;
    private final InterpretationScope interpretationScope;
    private final UserAccountScope userAccountScope;

    private Dhis2(Context context) {
        Models.init(context);
        Services.init(context);
        LastUpdatedManager.init(context);
        DateTimeManager.init(context);

        readSession();

        // IController objects initialization.
        DashboardController dashboardController
                = new DashboardController(null, Models.dashboards(), Models.dashboardItems(), Models.dashboardElements());
        InterpretationController interpretationController
                = new InterpretationController(null, Services.interpretations(), null);
        UserAccountController userAccountController
                = new UserAccountController(null, Models.userAccount());

        dashboardScope = new DashboardScope(dashboardController, Services.dashboards(),
                Services.dashboardItems(), Services.dashboardElements());
        interpretationScope = new InterpretationScope(interpretationController, Services.interpretations(),
                Services.interpretationElements(), Services.interpretationComments());
        userAccountScope = new UserAccountScope(userAccountController, Services.userAccount());
    }

    public static void init(Context context) {
        if (dhis2 == null) {
            dhis2 = new Dhis2(context);
        }
    }

    private static Dhis2 getInstance() {
        if (dhis2 == null) {
            throw new IllegalArgumentException("You have to call init() method first.");
        }

        return dhis2;
    }

    private void readSession() {
        session = LastUpdatedManager.getInstance().get();
        dhisApi = null;

        if (isUserLoggedIn()) {
            dhisApi = RepoManager.createService(
                    session.getServerUrl(),
                    session.getCredentials()
            );
        }
    }

    public UserAccount logInUser(HttpUrl serverUrl, Credentials credentials) throws APIException {
        return signInUser(serverUrl, credentials);
    }

    public UserAccount confirmUser(Credentials credentials) throws APIException {
        return signInUser(session.getServerUrl(), credentials);
    }

    public void logOutUser() throws APIException {
        Services.userAccount().logOut();

        // fetch meta data from disk
        readSession();
    }

    private UserAccount signInUser(HttpUrl serverUrl, Credentials credentials) throws APIException {
        DhisApi dhisApi = RepoManager.createService(serverUrl, credentials);
        UserAccount user = (new UserAccountController(dhisApi).logInUser(serverUrl, credentials));

        // fetch meta data from disk
        readSession();
        return user;
    }

    public static void invalidateSession() {
        LastUpdatedManager.getInstance().invalidate();

        // fetch meta data from disk
        getInstance().readSession();
    }

    public static boolean isUserLoggedIn() {
        return getInstance().session.getServerUrl() != null &&
                getInstance().session.getCredentials() != null;
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

    public static DashboardScope dashboards() {
        return getInstance().dashboardScope;
    }

    public static InterpretationScope interpretations() {
        return getInstance().interpretationScope;
    }

    public static UserAccountScope userAccount() {
        return getInstance().userAccountScope;
    }
}
