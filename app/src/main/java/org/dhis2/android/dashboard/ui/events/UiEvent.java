package org.dhis2.android.dashboard.ui.events;

/**
 * Created by arazabishov on 7/27/15.
 */
public final class UiEvent {
    public enum UiEventType {
        SYNC_DASHBOARDS, USER_LOG_OUT
    }

    private final UiEventType mType;

    public UiEvent(UiEventType type) {
        mType = type;
    }

    public UiEventType getEventType() {
        return mType;
    }
}