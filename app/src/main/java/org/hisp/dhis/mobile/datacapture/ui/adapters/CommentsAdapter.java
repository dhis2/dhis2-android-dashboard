package org.hisp.dhis.mobile.datacapture.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.hisp.dhis.mobile.datacapture.R;
import org.hisp.dhis.mobile.datacapture.api.models.Comment;
import org.joda.time.DateTime;

import java.util.List;

public class CommentsAdapter extends BaseAdapter {
    private static final String DATE_FORMAT = "YYYY-MM-dd";

    private List<Comment> mData;
    private LayoutInflater mInflater;

    public CommentsAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if (mData != null) {
            return mData.size();
        } else {
            return 0;
        }
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public Object getItem(int position) {
        if (mData != null && mData.size() > position) {
            return mData.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;

        if (convertView == null) {
            view = mInflater.inflate(R.layout.listview_row_comment_layout, parent, false);
            holder = new ViewHolder(
                    (TextView) view.findViewById(R.id.comment_user_name),
                    (TextView) view.findViewById(R.id.interpretation_comment),
                    (TextView) view.findViewById(R.id.comment_last_updated)
            );
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        Comment comment = (Comment) getItem(position);
        if (comment != null) {
            if (comment.getUser() != null &&
                    comment.getUser().getDisplayName() != null) {
                holder.userName.setText(comment.getUser().getDisplayName());
            }

            if (comment.getText() != null) {
                holder.comment.setText(comment.getText());
            }

            if (comment.getLastUpdated() != null) {
                DateTime dateTime = DateTime.parse(comment.getLastUpdated());
                holder.lastUpdated.setText(dateTime.toString(DATE_FORMAT));
            }
        }

        return view;
    }

    public void swapData(List<Comment> data) {
        if (mData != data) {
            mData = data;
            notifyDataSetChanged();
        }
    }

    private class ViewHolder {
        final TextView userName;
        final TextView comment;
        final TextView lastUpdated;

        private ViewHolder(TextView userName, TextView comment, TextView lastUpdated) {
            this.userName = userName;
            this.comment = comment;
            this.lastUpdated = lastUpdated;
        }
    }
}
