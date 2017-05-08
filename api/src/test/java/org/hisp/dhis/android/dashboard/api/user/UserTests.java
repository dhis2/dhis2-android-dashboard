package org.hisp.dhis.android.core.user;

import static org.junit.Assert.assertTrue;

import org.hisp.dhis.android.core.commons.DateTestUtils;
import org.hisp.dhis.android.core.commons.FileReader;
import org.hisp.dhis.android.core.commons.JsonParser;
import org.hisp.dhis.android.dashboard.api.models.User;
import org.hisp.dhis.android.dashboard.api.models.UserAccount;
import org.hisp.dhis.android.dashboard.api.models.meta.State;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

public class UserTests {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void userAccount_should_map_to_user() throws IOException {
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

    @Test
    public void user_should_map_from_json_string() throws IOException {
        UserAccount userAccount = getUserAccountFromJson();
        assertTrue(userAccount.getUId().equals("xE7jOejl9FI"));
        assertTrue(userAccount.getName().equals("John Traore"));
        assertTrue(userAccount.getDisplayName().equals("John Traore"));
        assertTrue(DateTestUtils.compareParsedDateWithStringDate(userAccount.getCreated(),
                "2013-04-18T17:15:08.407"));
        assertTrue(DateTestUtils.compareParsedDateWithStringDate(userAccount.getLastUpdated(),
                "2017-05-02T17:02:37.817"));
        assertTrue(userAccount.getAccess() == null);
        assertTrue(userAccount.getState().equals(State.SYNCED));
        assertTrue(userAccount.getFirstName().equals("John"));
        assertTrue(userAccount.getSurname().equals("Traore"));
        assertTrue(userAccount.getGender().equals("gender_male"));
        assertTrue(userAccount.getBirthday().equals("1971-04-08T00:00:00.000"));
        assertTrue(userAccount.getIntroduction().equals("I am the super user of DHIS 2"));
        assertTrue(userAccount.getEducation().equals("Master of super using"));
        assertTrue(userAccount.getEmployer().equals("DHIS"));
        assertTrue(userAccount.getInterests().equals("Football, swimming, singing, dancing"));
        assertTrue(userAccount.getJobTitle().equals("Super user"));
        assertTrue(userAccount.getLanguages().equals("English"));
        assertTrue(userAccount.getEmail().equals("someone@dhis2.org"));
        assertTrue(userAccount.getPhoneNumber() == null);
    }

    private UserAccount getUserAccountFromJson() throws IOException {
        return (UserAccount) JsonParser.getModelFromJson(UserAccount.class, new FileReader().getStringFromFile("user.json"));
    }
}