package org.hisp.dhis.mobile.datacapture.api.android.events;

import org.hisp.dhis.mobile.datacapture.api.models.UserAccount;

public class UserAccountUpdateEvent {
    private UserAccount userAccount;

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }
}
