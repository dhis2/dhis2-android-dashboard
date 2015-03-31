package org.hisp.dhis.mobile.datacapture.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.hisp.dhis.mobile.datacapture.ui.adapters.rows.Row;
import org.hisp.dhis.mobile.datacapture.ui.adapters.rows.RowTypes;

import java.util.List;

public class FieldAdapter extends BaseAdapter {
    private List<Row> mRows;
    private LayoutInflater mInflater;

    public FieldAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if (mRows != null) {
            return mRows.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if (mRows != null && mRows.size() > 0) {
            return mRows.get(position);
        } else {
            return mRows;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mRows != null && mRows.size() > 0) {
            return mRows.get(position).getView(mInflater, convertView, parent);
        } else {
            return null;
        }
    }

    @Override
    public int getViewTypeCount() {
        return RowTypes.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        if (mRows != null) {
            return mRows.get(position).getViewType();
        } else {
            return 0;
        }
    }

    public void swapData(List<Row> rows) {
        boolean notify = mRows != rows;
        mRows = rows;

        if (notify) {
            notifyDataSetChanged();
        }
    }
}
