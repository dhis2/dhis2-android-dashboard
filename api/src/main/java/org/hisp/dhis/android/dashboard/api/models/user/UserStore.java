package org.hisp.dhis.android.dashboard.api.models.user;

import org.hisp.dhis.android.dashboard.api.models.flow.User$Flow;

import java.util.List;

/**
 * Created by arazabishov on 8/27/15.
 */
public final class UserStore implements IUserStore {

    public UserStore() {
        // empty constructor
    }

    @Override
    public void insert(User object) {
        User$Flow userFlow = User$Flow.fromModel(object);
        userFlow.insert();

        object.setId(userFlow.getId());
    }

    @Override
    public void update(User object) {
        User$Flow.fromModel(object).update();
    }

    @Override
    public void save(User object) {
        User$Flow.fromModel(object).update();
    }

    @Override
    public void delete(User object) {
        User$Flow.fromModel(object).delete();
    }

    @Override
    public List<User> query() {
        return null;
    }

    @Override
    public User query(String uid) {
        return null;
    }
}
