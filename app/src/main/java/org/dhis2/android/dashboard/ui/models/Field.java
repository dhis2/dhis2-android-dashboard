package org.dhis2.android.dashboard.ui.models;

/**
 * Created by arazabishov on 7/27/15.
 */
public final class Field {
    private final String label;
    private final String value;

    public Field(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }
}
