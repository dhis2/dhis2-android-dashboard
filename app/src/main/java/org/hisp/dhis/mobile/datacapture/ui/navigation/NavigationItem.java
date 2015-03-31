package org.hisp.dhis.mobile.datacapture.ui.navigation;

import android.view.View;
import android.view.ViewGroup;

public interface NavigationItem {
    public boolean isEnabled();
    public int getItemId();
    public View getView(View convertView, ViewGroup parent);
    public int getItemViewType();
}
