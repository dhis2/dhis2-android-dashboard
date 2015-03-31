package org.hisp.dhis.mobile.datacapture.ui.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.hisp.dhis.mobile.datacapture.R;
import org.hisp.dhis.mobile.datacapture.api.android.models.DbRow;
import org.hisp.dhis.mobile.datacapture.api.models.DashboardItem;

public class DashboardEditAdapter extends DBBaseAdapter<DashboardItem> {
    private ClickListener mListener;

    public DashboardEditAdapter(Context context) {
        super(context);
        mListener = new ClickListener();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;

        if (convertView == null) {
            view = getInflater().inflate(R.layout.listview_row_edit_dashboard_layout, parent, false);
            holder = new ViewHolder(
                    (TextView) view.findViewById(R.id.dashboard_item_name),
                    (ImageButton) view.findViewById(R.id.delete_dashboard_item)
            );
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        handleDashboardItem((DbRow<DashboardItem>) getItem(position), holder);
        return view;
    }

    private void handleDashboardItem(DbRow<DashboardItem> dbItem, ViewHolder holder) {
        DashboardItem item = dbItem.getItem();
        if (DashboardItem.TYPE_MAP.equals(item.getType()) && item.getMap() != null) {
            holder.textView.setText(item.getMap().getName());
        } else if (DashboardItem.TYPE_CHART.equals(item.getType()) && item.getChart() != null) {
            holder.textView.setText(item.getChart().getName());
        } else if (DashboardItem.TYPE_EVENT_CHART.equals(item.getType()) && item.getEventChart() != null) {
            holder.textView.setText(item.getEventChart().getName());
        } else if (DashboardItem.TYPE_REPORT_TABLE.equals(item.getType()) && item.getReportTable() != null) {
            holder.textView.setText(item.getReportTable().getName());
        } else if (DashboardItem.TYPE_USERS.equals(item.getType()) && item.getUsers() != null) {
            holder.textView.setText(getContext().getString(R.string.users));
        } else if (DashboardItem.TYPE_RESOURCES.equals(item.getType()) && item.getResources() != null) {
            holder.textView.setText(getContext().getString(R.string.resources));
        } else if (DashboardItem.TYPE_REPORTS.equals(item.getType()) && item.getReports() != null) {
            holder.textView.setText(getContext().getString(R.string.reports));
        }

        mListener.setItem(dbItem);
        holder.button.setOnClickListener(mListener);
    }

    public void setOnClickListener(OnDeleteClickListener listener) {
        mListener.setListener(listener);
    }

    public interface OnDeleteClickListener {
        public void onDeleteButtonClicked(DbRow<DashboardItem> item);
    }

    private static class ClickListener implements View.OnClickListener {
        private OnDeleteClickListener mListener;
        private DbRow<DashboardItem> mItem;

        public void setListener(OnDeleteClickListener listener) {
            mListener = listener;
        }

        public void setItem(DbRow<DashboardItem> item) {
            mItem = item;
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onDeleteButtonClicked(mItem);
            }
        }
    }

    private static class ViewHolder {
        final TextView textView;
        final ImageButton button;

        ViewHolder(TextView textView, ImageButton button) {
            this.textView = textView;
            this.button = button;
        }
    }
}
