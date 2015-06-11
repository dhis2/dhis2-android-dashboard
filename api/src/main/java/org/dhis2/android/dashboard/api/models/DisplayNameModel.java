package org.dhis2.android.dashboard.api.models;


import java.util.Comparator;

public interface DisplayNameModel {
    Comparator<DisplayNameModel> DISPLAY_NAME_MODEL_COMPARATOR = new DisplayNameComparator();

    void setDisplayName(String displayName);

    String getDisplayName();

    class DisplayNameComparator implements Comparator<DisplayNameModel> {

        @Override public int compare(DisplayNameModel first, DisplayNameModel second) {
            if (first != null && first.getDisplayName() != null
                    && second != null && second.getDisplayName() != null) {
                return first.getDisplayName().compareTo(second.getDisplayName());
            } else {
                return 0;
            }
        }
    }
}
