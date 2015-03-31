package org.hisp.dhis.mobile.datacapture.ui.navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.hisp.dhis.mobile.datacapture.R;

public class NavigationSection implements NavigationItem {
    private final LayoutInflater mLayoutInflater;
    private final int mItemId;
    private final int mLabelId;

    public NavigationSection(LayoutInflater layoutInflater,
                             int itemId, int labelId) {
        mLayoutInflater = layoutInflater;
        mItemId = itemId;
        mLabelId = labelId;
    }


    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public int getItemId() {
        return mItemId;
    }

    @Override
    public View getView(View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;

        if (convertView == null) {
            view = mLayoutInflater.inflate(R.layout.listview_navigation_section_row, parent, false);
            holder = new ViewHolder((TextView) view.findViewById(R.id.navigation_section_label));
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        holder.label.setText(mLabelId);
        return view;
    }

    @Override
    public int getItemViewType() {
        return NavigationItemType.NAVIGATION_SECTION.ordinal();
    }

    private static class ViewHolder {
        public final TextView label;

        private ViewHolder(TextView label) {
            this.label = label;
        }
    }
}
