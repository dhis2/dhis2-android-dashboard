package org.hisp.dhis.android.dashboard.api.meta;

import org.hisp.dhis.android.dashboard.api.models.meta.Credentials;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class CredentialsTests {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void throw_exception_if_username_is_not_provided() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Username must not be null");

        new Credentials(null, "pwd");
    }

    @Test
    public void throw_exception_if_password_is_not_provided() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Password must not be null");

        new Credentials("user", null);
    }
}
