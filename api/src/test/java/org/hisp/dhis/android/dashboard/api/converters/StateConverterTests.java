package org.hisp.dhis.android.dashboard.api.converters;

import static junit.framework.Assert.assertTrue;

import org.hisp.dhis.android.dashboard.api.models.meta.State;
import org.hisp.dhis.android.dashboard.api.persistence.converters.StateConverter;
import org.junit.Test;

public class StateConverterTests {
    public static final String STATE_SYNCED = "SYNCED";
    public static final String STATE_TO_POST = "TO_POST";
    public static final String STATE_TO_UPDATE = "TO_UPDATE";
    public static final String STATE_TO_DELETE = "TO_DELETE";

    StateConverter stateConverter = new StateConverter();

    @Test
    public void convert_state_to_post_string_to_object() throws Exception {
        assertTrue(stateConverter.getModelValue(STATE_TO_POST).equals(State.TO_POST));
    }

    @Test
    public void convert_state_to_post_object_to_string() throws Exception {
        assertTrue(stateConverter.getDBValue(State.TO_POST).equals(STATE_TO_POST));
    }

    @Test
    public void convert_state_to_delete_string_to_object() throws Exception {
        assertTrue(stateConverter.getModelValue(STATE_TO_DELETE).equals(State.TO_DELETE));
    }

    @Test
    public void convert_state_to_delete_object_to_string() throws Exception {
        assertTrue(stateConverter.getDBValue(State.TO_DELETE).equals(STATE_TO_DELETE));
    }

    @Test
    public void convert_state_to_update_string_to_object() throws Exception {
        assertTrue(stateConverter.getModelValue(STATE_TO_UPDATE).equals(State.TO_UPDATE));
    }

    @Test
    public void convert_state_to_update_object_to_string() throws Exception {
        assertTrue(stateConverter.getDBValue(State.TO_UPDATE).equals(STATE_TO_UPDATE));
    }

    @Test
    public void convert_state_synced_string_to_object() throws Exception {
        assertTrue(stateConverter.getModelValue(STATE_SYNCED).equals(State.SYNCED));
    }

    @Test
    public void convert_state_synced_object_to_string() throws Exception {
        assertTrue(stateConverter.getDBValue(State.SYNCED).equals(STATE_SYNCED));
    }
}
