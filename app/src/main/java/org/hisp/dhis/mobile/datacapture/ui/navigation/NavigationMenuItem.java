package org.hisp.dhis.mobile.datacapture.ui.navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.hisp.dhis.mobile.datacapture.R;

public class NavigationMenuItem implements NavigationItem {
    private final int mId;
    private final int mLabelId;
    private final int mIconId;
    private final LayoutInflater mInflater;

    public NavigationMenuItem(LayoutInflater inflater,
                              int id, int labelId, int iconId) {
        mId = id;
        mLabelId = labelId;
        mIconId = iconId;
        mInflater = inflater;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public int getItemId() {
        return mId;
    }

    @Override
    public View getView(View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;

        if (convertView == null) {
            view = mInflater.inflate(R.layout.listview_navigation_menu_row, parent, false);
            holder = new ViewHolder(
                    (TextView) view.findViewById(R.id.navigation_menu_item_label),
                    (ImageView) view.findViewById(R.id.navigation_menu_item_icon)
            );

            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        holder.label.setText(mLabelId);
        holder.icon.setImageResource(mIconId);

        return view;
    }

    @Override
    public int getItemViewType() {
        return NavigationItemType.NAVIGATION_MENU_ITEM.ordinal();
    }

    private static class ViewHolder {
        public final TextView label;
        public final ImageView icon;

        private ViewHolder(TextView label, ImageView icon) {
            this.label = label;
            this.icon = icon;
        }
    }
}
