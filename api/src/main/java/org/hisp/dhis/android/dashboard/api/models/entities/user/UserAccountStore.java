package org.hisp.dhis.android.dashboard.api.models.entities.user;

import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.dashboard.api.models.entities.flow.UserAccount$Flow;

import java.util.List;

/**
 * Created by arazabishov on 8/26/15.
 */
public final class UserAccountStore implements IUserAccountStore {

    public UserAccountStore() {
        // empty constructor
    }

    @Override
    public void insert(UserAccount object) {
        UserAccount$Flow userAccountFlow = UserAccount$Flow.fromModel(object);
        userAccountFlow.insert();

        object.setId(userAccountFlow.getId());
    }

    @Override
    public void update(UserAccount object) {
        UserAccount$Flow.fromModel(object).update();
    }

    @Override
    public void delete(UserAccount object) {
        UserAccount$Flow.fromModel(object).delete();
    }

    @Override
    public List<UserAccount> query() {
        List<UserAccount$Flow> userAccounts = new Select()
                .from(UserAccount$Flow.class)
                .queryList();
        return UserAccount$Flow.toModels(userAccounts);
    }
}
