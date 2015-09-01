package org.hisp.dhis.android.dashboard.api.models.user;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.dashboard.api.models.flow.User$Flow;
import org.hisp.dhis.android.dashboard.api.models.flow.User$Flow$Table;

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
        User$Flow userFlow =
                User$Flow.fromModel(object);
        userFlow.save();

        object.setId(userFlow.getId());
    }

    @Override
    public void delete(User object) {
        User$Flow.fromModel(object).delete();
    }

    @Override
    public List<User> query() {
        List<User$Flow> userFlows = new Select()
                .from(User$Flow.class)
                .queryList();
        return User$Flow.toModels(userFlows);
    }

    @Override
    public User query(long id) {
        User$Flow userFlow = new Select()
                .from(User$Flow.class)
                .where(Condition.column(User$Flow$Table.ID).is(id))
                .querySingle();
        return User$Flow.toModel(userFlow);
    }

    @Override
    public User query(String uid) {
        User$Flow userFlow = new Select()
                .from(User$Flow.class)
                .where(Condition.column(User$Flow$Table.UID).is(uid))
                .querySingle();
        return User$Flow.toModel(userFlow);
    }
}
