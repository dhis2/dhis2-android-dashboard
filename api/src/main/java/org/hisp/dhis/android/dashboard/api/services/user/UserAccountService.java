package org.hisp.dhis.android.dashboard.api.services.user;

import org.hisp.dhis.android.dashboard.api.models.user.User;
import org.hisp.dhis.android.dashboard.api.models.user.UserAccount;

/**
 * Created by arazabishov on 8/27/15.
 */
public class UserAccountService implements IUserAccountService {

    public UserAccountService() {
        // empty constructor
    }

    @Override
    public UserAccount getCurrentUserAccount() {
        return null;
    }

    @Override
    public User toUser(UserAccount userAccount) {
        return null;
    }
}
