package org.hisp.dhis.android.dashboard.api.user;

import static org.junit.Assert.assertTrue;

import org.hisp.dhis.android.dashboard.api.commons.DateTestUtils;
import org.hisp.dhis.android.dashboard.api.commons.FileReader;
import org.hisp.dhis.android.dashboard.api.commons.JsonParser;
import org.hisp.dhis.android.dashboard.api.models.User;
import org.hisp.dhis.android.dashboard.api.models.UserAccount;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

public class UserTests {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void userAccount_conversion_to_user() throws IOException {
        UserAccount userAccount = getUserAccountFromJson();
        User user = UserAccount.toUser(userAccount);
        assertTrue(user.getUId().equals("xE7jOejl9FI"));
        assertTrue(user.getName().equals("John Traore"));
        assertTrue(user.getDisplayName().equals("John Traore"));
        assertTrue(DateTestUtils.compareParsedDateWithStringDate(user.getCreated(),
                "2013-04-18T17:15:08.407"));
        assertTrue(DateTestUtils.compareParsedDateWithStringDate(user.getLastUpdated(),
                "2017-05-02T17:02:37.817"));
        assertTrue(user.getAccess() == null);
    }

    private UserAccount getUserAccountFromJson() throws IOException {
        return (UserAccount) JsonParser.getModelFromJson(UserAccount.class, new FileReader().getStringFromFile(
                "userAccount.json"));
    }
}