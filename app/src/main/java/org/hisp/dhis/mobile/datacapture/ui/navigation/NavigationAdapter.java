package org.hisp.dhis.mobile.datacapture.ui.navigation;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

public class NavigationAdapter extends BaseAdapter {
    private List<NavigationItem> mNavigationItems;

    public NavigationAdapter(List<NavigationItem> navigationItems) {
        if (navigationItems == null) {
            throw new IllegalArgumentException("List of NavigationItem must not be null");
        }
        mNavigationItems = navigationItems;
    }

    @Override
    public int getCount() {
        return mNavigationItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mNavigationItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mNavigationItems.get(position).getItemId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return mNavigationItems.get(position).getView(convertView, parent);
    }

    @Override
    public int getViewTypeCount() {
        return NavigationItemType.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        return mNavigationItems.get(position).getItemViewType();
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return mNavigationItems.get(position).isEnabled();
    }
}
