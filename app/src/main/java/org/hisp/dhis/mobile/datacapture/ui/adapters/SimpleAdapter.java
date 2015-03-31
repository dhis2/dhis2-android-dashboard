package org.hisp.dhis.mobile.datacapture.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.hisp.dhis.mobile.datacapture.R;

import java.util.List;

public class SimpleAdapter<T> extends BaseAdapter {
    private List<T> mItems;
    private LayoutInflater mInflater;
    private ExtractStringCallback<T> mCallback;

    public SimpleAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    public void setCallback(ExtractStringCallback<T> callback) {
        mCallback = callback;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextViewHolder holder;
        View view;

        if (convertView == null) {
            View root = mInflater.inflate(R.layout.dialog_fragment_listview_item, parent, false);
            TextView textView = (TextView) root.findViewById(R.id.textview_item);

            holder = new TextViewHolder(textView);
            root.setTag(holder);
            view = root;
        } else {
            view = convertView;
            holder = (TextViewHolder) view.getTag();
        }

        String label = mCallback.getString(mItems.get(position));
        holder.textView.setText(label);
        return view;
    }

    @Override
    public int getCount() {
        if (mItems != null) {
            return mItems.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int pos) {
        return getItemSafely(pos);
    }

    public T getItemSafely(int pos) {
        if (mItems != null && mItems.size() > 0) {
            return mItems.get(pos);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    public void swapData(List<T> items) {
        if (mItems != items) {
            mItems = items;
            notifyDataSetChanged();
        }
    }

    public static interface ExtractStringCallback<T> {
        public String getString(T object);
    }

    private class TextViewHolder {
        final TextView textView;

        public TextViewHolder(TextView textView) {
            this.textView = textView;
        }
    }
}