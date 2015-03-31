package org.hisp.dhis.mobile.datacapture.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import org.hisp.dhis.mobile.datacapture.api.android.models.DbRow;

import java.util.List;

import static org.hisp.dhis.mobile.datacapture.api.utils.Preconditions.isNull;

public abstract class DBBaseAdapter<T> extends BaseAdapter {
    private List<DbRow<T>> mData;
    private LayoutInflater mInflater;
    private Context mContext;

    public DBBaseAdapter(Context context) {
        mContext = isNull(context, "Context object must not be null");
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
    public Object getItem(int position) {
        if (mData != null && mData.size() > position) {
            return mData.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        DbRow<T> item = (DbRow<T>) getItem(position);
        if (item != null) {
            return item.getId();
        } else {
            return -1;
        }
    }

    public void swapData(List<DbRow<T>> data) {
        if (mData != data) {
            mData = data;
            notifyDataSetChanged();
        }
    }

    public List<DbRow<T>> getItems() {
        return mData;
    }

    public Context getContext() {
        return mContext;
    }

    public LayoutInflater getInflater() {
        return mInflater;
    }
}

