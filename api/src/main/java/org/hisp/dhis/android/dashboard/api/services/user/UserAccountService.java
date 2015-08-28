package org.hisp.dhis.android.dashboard.api.services.user;

import org.hisp.dhis.android.dashboard.api.models.Models;
import org.hisp.dhis.android.dashboard.api.models.user.User;
import org.hisp.dhis.android.dashboard.api.models.user.UserAccount;
import org.hisp.dhis.android.dashboard.api.network.SessionManager;
import org.hisp.dhis.android.dashboard.api.persistence.preferences.DateTimeManager;
import org.hisp.dhis.android.dashboard.api.persistence.preferences.LastUpdatedManager;

import java.util.List;

/**
 * Created by arazabishov on 8/27/15.
 */
public class UserAccountService implements IUserAccountService {

    public UserAccountService() {
        // empty constructor
    }

    @Override
    public UserAccount getCurrentUserAccount() {
        List<UserAccount> userAccounts =
                Models.userAccount().query();
        return userAccounts != null && !userAccounts.isEmpty() ? userAccounts.get(0) : null;
    }

    @Override
    public User toUser(UserAccount userAccount) {
        User user = new User();
        user.setUId(userAccount.getUId());
        user.setAccess(userAccount.getAccess());
        user.setCreated(user.getCreated());
        user.setLastUpdated(userAccount.getLastUpdated());
        user.setName(userAccount.getName());
        user.setDisplayName(userAccount.getDisplayName());
        return user;
    }

    @Override
    public void logOut() {
        LastUpdatedManager.getInstance().delete();
        DateTimeManager.getInstance().delete();
        SessionManager.getInstance().delete();

        // removing all existing data
        Models.modelsStore().deleteAllTables();
    }
}
