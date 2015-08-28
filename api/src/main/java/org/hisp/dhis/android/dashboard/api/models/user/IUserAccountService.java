package org.hisp.dhis.android.dashboard.api.models.user;

import org.hisp.dhis.android.dashboard.api.models.user.User;
import org.hisp.dhis.android.dashboard.api.models.user.UserAccount;
import org.hisp.dhis.android.dashboard.api.models.common.IService;

/**
 * Created by arazabishov on 8/27/15.
 */
public interface IUserAccountService extends IService {
    UserAccount getCurrentUserAccount();

    User toUser(UserAccount userAccount);

    void logOut();
}
