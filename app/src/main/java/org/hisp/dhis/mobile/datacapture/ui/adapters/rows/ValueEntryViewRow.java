package org.hisp.dhis.mobile.datacapture.ui.adapters.rows;

import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.hisp.dhis.mobile.datacapture.R;
import org.hisp.dhis.mobile.datacapture.api.android.models.DbRow;
import org.hisp.dhis.mobile.datacapture.api.models.Field;
import org.hisp.dhis.mobile.datacapture.ui.views.ValueEntryView;
import org.hisp.dhis.mobile.datacapture.ui.views.ValueEntryView.OnValueSetListener;

public class ValueEntryViewRow implements Row {
    private static final String EMPTY_FIELD = "";
    private final DbRow<Field> mField;
    private final RowTypes mRowType;
    private OnFieldValueSetListener mListener;
    
    public ValueEntryViewRow(DbRow<Field> field, RowTypes rowType) {
        mField = field;
        mRowType = rowType;

        if (!RowTypes.TEXT.equals(rowType) &&
                !RowTypes.LONG_TEXT.equals(rowType) &&
                !RowTypes.NUMBER.equals(rowType) &&
                !RowTypes.INTEGER.equals(rowType) &&
                !RowTypes.INTEGER_NEGATIVE.equals(rowType) &&
                !RowTypes.INTEGER_ZERO_OR_POSITIVE.equals(rowType) &&
                !RowTypes.INTEGER_POSITIVE.equals(rowType)) {
            throw new IllegalArgumentException("Unsupported row type");
        }
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView, ViewGroup container) {
        View view;
        ValueEntryHolder holder;
        
        if (convertView == null) {
            View root = inflater.inflate(R.layout.listview_row_value_entry, container, false);
            TextView label = (TextView) root.findViewById(R.id.text_label);
            ValueEntryView valueEntryView = (ValueEntryView) root.findViewById(R.id.value_entry_row);

            if (RowTypes.TEXT.equals(mRowType)) {
                valueEntryView.setInputType(InputType.TYPE_CLASS_TEXT);
                valueEntryView.setHint(R.string.enter_text);
            } else if (RowTypes.LONG_TEXT.equals(mRowType)) {
                valueEntryView.setInputType(InputType.TYPE_CLASS_TEXT);
                valueEntryView.setHint(R.string.enter_long_text);
            } else if (RowTypes.NUMBER.equals(mRowType)) {
                valueEntryView.setInputType(InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_FLAG_DECIMAL |
                        InputType.TYPE_NUMBER_FLAG_SIGNED);
                valueEntryView.setHint(R.string.enter_number);
            } else if (RowTypes.INTEGER.equals(mRowType)) {
                valueEntryView.setInputType(InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_FLAG_SIGNED);
                valueEntryView.setHint(R.string.enter_integer);
            } else if (RowTypes.INTEGER_NEGATIVE.equals(mRowType)) {
                valueEntryView.setInputType(InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_FLAG_SIGNED);
                valueEntryView.setHint(R.string.enter_negative_integer);
                valueEntryView.setFilters(new InputFilter[]{new NegInpFilter()});
            } else if (RowTypes.INTEGER_ZERO_OR_POSITIVE.equals(mRowType)) {
                valueEntryView.setInputType(InputType.TYPE_CLASS_NUMBER);
                valueEntryView.setHint(R.string.enter_positive_integer_or_zero);
                valueEntryView.setFilters(new InputFilter[] {new PosOrZeroFilter()});
            } else if (RowTypes.INTEGER_POSITIVE.equals(mRowType)) {
                valueEntryView.setInputType(InputType.TYPE_CLASS_NUMBER);
                valueEntryView.setHint(R.string.enter_positive_integer);
                valueEntryView.setFilters(new InputFilter[] {new PosFilter()});
            }

            ValueSetListener listener = new ValueSetListener();
            holder = new ValueEntryHolder(label, valueEntryView, listener);
            root.setTag(holder);
            view = root;
        } else {
            view = convertView;
            holder = (ValueEntryHolder) view.getTag();
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
        return mRowType.ordinal();
    }

    private static class ValueEntryHolder {
        final TextView textLabel;
        final ValueEntryView valueEntryView;
        final ValueSetListener listener;

        public ValueEntryHolder(TextView textLabel,
                                ValueEntryView valueEntryView,
                                ValueSetListener listener) {
            this.textLabel = textLabel;
            this.valueEntryView = valueEntryView;
            this.listener = listener;
        }

        public void updateViews(DbRow<Field> dbItem,
                                OnFieldValueSetListener fieldListener) {
            Field field = dbItem.getItem();
            textLabel.setText(field.getLabel());
            listener.setField(dbItem);
            listener.setListener(fieldListener);

            valueEntryView.setOnValueSetListener(listener);
            valueEntryView.setText(field.getValue());
            valueEntryView.resetView();
        }
    }

    private static class ValueSetListener implements OnValueSetListener {
        private DbRow<Field> field;
        private OnFieldValueSetListener listener;

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

    private static class NegInpFilter implements InputFilter {

        @Override
        public CharSequence filter(CharSequence str, int start, int end,
                                   Spanned spn, int spnStart, int spnEnd) {

            if ((str.length() > 0) && (spnStart == 0) && (str.charAt(0) != '-')) {
                return EMPTY_FIELD;
            }

            return str;
        }
    }

    private static class PosOrZeroFilter implements InputFilter {

        @Override
        public CharSequence filter(CharSequence str, int start, int end,
                                   Spanned spn, int spStart, int spEnd) {

            if ((str.length() > 0) && (spn.length() > 0) && (spn.charAt(0) == '0')) {
                return EMPTY_FIELD;
            }

            if ((spn.length() > 0) && (spStart == 0)
                    && (str.length() > 0) && (str.charAt(0) == '0')) {
                return EMPTY_FIELD;
            }

            return str;
        }
    }

    private static class PosFilter implements InputFilter {

        @Override
        public CharSequence filter(CharSequence str, int start, int end,
                                   Spanned spn, int spnStart, int spnEnd) {

            if ((str.length() > 0) && (spnStart == 0) && (str.charAt(0) == '0')) {
                return EMPTY_FIELD;
            }

            return str;
        }
    }
}