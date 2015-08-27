package org.hisp.dhis.android.dashboard.api.services.user;

import org.hisp.dhis.android.dashboard.api.models.user.User;
import org.hisp.dhis.android.dashboard.api.models.user.UserAccount;
import org.hisp.dhis.android.dashboard.api.services.common.IService;

/**
 * Created by arazabishov on 8/27/15.
 */
public interface IUserAccountService extends IService {
    UserAccount getCurrentUserAccount();

    User toUser(UserAccount userAccount);
}
