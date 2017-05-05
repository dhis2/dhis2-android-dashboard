package org.hisp.dhis.android.core;

import org.hisp.dhis.android.dashboard.api.utils.Preconditions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class PreconditionsTests {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void precondition_null_exception() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("test_message");
        Preconditions.isNull(null, "test_message");
    }
}
