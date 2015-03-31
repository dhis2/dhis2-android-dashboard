package org.hisp.dhis.mobile.datacapture.ui.adapters.rows;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public interface Row {
    public View getView(LayoutInflater inflater, View convertView, ViewGroup container);
    public void setListener(OnFieldValueSetListener listener);
    public int getViewType();
}
