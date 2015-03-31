package org.hisp.dhis.mobile.datacapture.api.android.events;

public class FieldValueChangeEvent {
    private int fieldId;
    private String value;

    public int getFieldId() {
        return fieldId;
    }

    public void setFieldId(int fieldId) {
        this.fieldId = fieldId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
