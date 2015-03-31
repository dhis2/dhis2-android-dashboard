package org.hisp.dhis.mobile.datacapture.api.android.events;

public class InterpretationDeleteEvent {
    private int mInterpretationId;

    public int getInterpretationId() {
        return mInterpretationId;
    }

    public void setInterpretationId(int interpretationId) {
        mInterpretationId = interpretationId;
    }
}
