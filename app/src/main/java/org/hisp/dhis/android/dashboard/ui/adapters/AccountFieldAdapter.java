package org.hisp.dhis.android.dashboard.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.hisp.dhis.android.dashboard.R;
import org.hisp.dhis.android.dashboard.ui.models.Field;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by arazabishov on 7/27/15.
 */
public class AccountFieldAdapter extends AbsAdapter<Field, AccountFieldAdapter.FieldViewHolder> {

    public AccountFieldAdapter(Context context, LayoutInflater inflater) {
        super(context, inflater);
    }

    @Override
    public FieldViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FieldViewHolder(getLayoutInflater()
                .inflate(R.layout.recycler_view_field_item, parent, false));
    }

    @Override
    public void onBindViewHolder(FieldViewHolder holder, int position) {
        Field field = getData().get(position);
        holder.labelTextView.setText(field.getLabel());
        holder.valueTextView.setText(field.getValue());
    }

    public static class FieldViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.field_label_text_view)
        public TextView labelTextView;

        @Bind(R.id.field_value_text_view)
        public TextView valueTextView;

        public FieldViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
