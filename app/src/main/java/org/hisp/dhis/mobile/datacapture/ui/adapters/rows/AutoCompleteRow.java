package org.hisp.dhis.mobile.datacapture.ui.adapters.rows;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.hisp.dhis.mobile.datacapture.R;
import org.hisp.dhis.mobile.datacapture.api.android.models.DbRow;
import org.hisp.dhis.mobile.datacapture.api.models.Field;
import org.hisp.dhis.mobile.datacapture.api.models.Option;
import org.hisp.dhis.mobile.datacapture.api.models.OptionSet;
import org.hisp.dhis.mobile.datacapture.ui.views.AutoCompleteValueEntryView;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteRow implements Row {
    private DbRow<Field> mField;
    private List<String> mOptions;
    private OnFieldValueSetListener mListener;

    public AutoCompleteRow(DbRow<Field> field, OptionSet optionset) {
        mField = field;
        mOptions = new ArrayList<>();
        if (optionset != null && optionset.getOptions() != null &&
                optionset.getOptions().size() > 0) {
            for (Option option : optionset.getOptions()) {
                mOptions.add(option.getName());
            }
        }
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView, ViewGroup container) {
        View view;
        AutoCompleteRowHolder holder;

        if (convertView == null) {
            View root = inflater.inflate(
                    R.layout.listview_row_autocomplete, container, false);
            TextView textLabel = (TextView)
                    root.findViewById(R.id.text_label);
            AutoCompleteValueEntryView autoComplete = (AutoCompleteValueEntryView)
                    root.findViewById(R.id.find_option);

            ValueSetListener listener = new ValueSetListener();
            holder = new AutoCompleteRowHolder(textLabel, autoComplete, listener);

            root.setTag(holder);
            view = root;
        } else {
            view = convertView;
            holder = (AutoCompleteRowHolder) view.getTag();
        }

        holder.updateViews(mField, mListener);
        return view;
    }

    @Override
    public void setListener(OnFieldValueSetListener listener) {
        mListener = listener;
    }

    @Override
    public int getViewType() {
        return RowTypes.AUTO_COMPLETE.ordinal();
    }

    private static class ValueSetListener implements AutoCompleteValueEntryView.OnValueSetListener {
        private OnFieldValueSetListener listener;
        private DbRow<Field> field;

        public void setField(DbRow<Field> field) {
            this.field = field;
        }

        public void setListener(OnFieldValueSetListener listener) {
            this.listener = listener;
        }

        @Override
        public void onValueSet(String newValue) {
            String value = field.getItem().getValue();
            if (newValue != null && !newValue.equals(value)) {
                field.getItem().setValue(newValue);
                if (listener != null) {
                    listener.onFieldValueSet(field.getId(), newValue);
                }
            }
        }
    }

    private class AutoCompleteRowHolder {
        final TextView textLabel;
        final AutoCompleteValueEntryView autoComplete;
        final ValueSetListener listener;

        public AutoCompleteRowHolder(TextView textLabel,
                                     AutoCompleteValueEntryView autoComplete,
                                     ValueSetListener listener) {
            this.textLabel = textLabel;
            this.autoComplete = autoComplete;
            this.listener = listener;
        }

        public void updateViews(DbRow<Field> field,
                                OnFieldValueSetListener onFieldValueSetListener) {
            textLabel.setText(field.getItem().getLabel());

            listener.setField(field);
            listener.setListener(onFieldValueSetListener);

            System.out.println("updateViews(): " + mField.getItem().getValue());
            autoComplete.setOnValueSetListener(listener);
            autoComplete.swapData(mOptions);
            autoComplete.setText(mField.getItem().getValue());
            autoComplete.resetView();
        }
    }
}