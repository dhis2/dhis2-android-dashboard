package org.hisp.dhis.android.dashboard.api.api;

import com.squareup.okhttp.HttpUrl;

import org.hisp.dhis.android.dashboard.api.controllers.user.IUserAccountController;
import org.hisp.dhis.android.dashboard.api.models.common.meta.Credentials;
import org.hisp.dhis.android.dashboard.api.models.user.IUserAccountService;
import org.hisp.dhis.android.dashboard.api.models.user.User;
import org.hisp.dhis.android.dashboard.api.models.user.UserAccount;
import org.hisp.dhis.android.dashboard.api.network.APIException;

/**
 * Created by arazabishov on 8/28/15.
 */
public final class UserAccountScope implements IUserAccountController, IUserAccountService {
    private final IUserAccountController userAccountController;
    private final IUserAccountService userAccountService;

    public UserAccountScope(IUserAccountController userAccountController,
                            IUserAccountService userAccountService) {
        this.userAccountController = userAccountController;
        this.userAccountService = userAccountService;
    }

    @Override
    public UserAccount logIn(HttpUrl serverUrl, Credentials credentials) throws APIException {
        return userAccountController.logIn(serverUrl, credentials);
    }

    @Override
    public UserAccount updateAccount() throws APIException {
        return userAccountController.updateAccount();
    }

    @Override
    public UserAccount getCurrentUserAccount() {
        return userAccountService.getCurrentUserAccount();
    }

    @Override
    public User toUser(UserAccount userAccount) {
        return userAccountService.toUser(userAccount);
    }

    @Override
    public void logOut() {
        userAccountService.logOut();
    }
}
