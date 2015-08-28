package org.hisp.dhis.android.dashboard.api.controllers.user;

import com.squareup.okhttp.HttpUrl;

import org.hisp.dhis.android.dashboard.api.controllers.common.IController;
import org.hisp.dhis.android.dashboard.api.models.common.meta.Credentials;
import org.hisp.dhis.android.dashboard.api.models.user.UserAccount;
import org.hisp.dhis.android.dashboard.api.network.APIException;

/**
 * Created by arazabishov on 8/28/15.
 */
public interface IUserAccountController extends IController<UserAccount> {
    UserAccount logIn(HttpUrl serverUrl, Credentials credentials) throws APIException;

    UserAccount updateAccount() throws APIException;
}
