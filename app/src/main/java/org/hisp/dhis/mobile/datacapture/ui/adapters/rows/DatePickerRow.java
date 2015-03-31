package org.hisp.dhis.mobile.datacapture.ui.adapters.rows;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.hisp.dhis.mobile.datacapture.R;
import org.hisp.dhis.mobile.datacapture.api.android.models.DbRow;
import org.hisp.dhis.mobile.datacapture.api.models.Field;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

public class DatePickerRow implements Row {
    private static final String EMPTY_FIELD = "";

    private DbRow<Field> mField;
    private OnFieldValueSetListener mListener;

    public DatePickerRow(DbRow<Field> field) {
        mField = field;
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView, ViewGroup container) {
        View view;
        DatePickerRowHolder holder;

        if (convertView == null) {
            View root = inflater.inflate(
                    R.layout.listview_row_datepicker, container, false);

            TextView textLabel = (TextView)
                    root.findViewById(R.id.text_label);
            ImageButton clearButton = (ImageButton)
                    root.findViewById(R.id.clear_edit_text);
            EditText pickerInvoker = (EditText)
                    root.findViewById(R.id.date_picker_edit_text);

            DateSetListener dateSetListener = new DateSetListener();
            OnEditTextClickListener invokerListener = new OnEditTextClickListener(inflater.getContext());
            ClearButtonListener clearButtonListener = new ClearButtonListener();

            holder = new DatePickerRowHolder(textLabel, pickerInvoker, clearButton,
                    clearButtonListener, dateSetListener, invokerListener);

            root.setTag(holder);
            view = root;
        } else {
            view = convertView;
            holder = (DatePickerRowHolder) view.getTag();
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
        return RowTypes.DATE.ordinal();
    }

    private class DatePickerRowHolder {
        final TextView textLabel;
        final EditText editText;
        final ImageButton clearButton;

        final DateSetListener dateSetListener;
        final OnEditTextClickListener invokerListener;
        final ClearButtonListener clearButtonListener;

        public DatePickerRowHolder(TextView textLabel, EditText editText,
                                   ImageButton clearButton, ClearButtonListener clearButtonListener,
                                   DateSetListener dateSetListener, OnEditTextClickListener invokerListener) {
            this.textLabel = textLabel;
            this.editText = editText;
            this.clearButton = clearButton;

            this.dateSetListener = dateSetListener;
            this.invokerListener = invokerListener;
            this.clearButtonListener = clearButtonListener;
        }

        public void updateViews(DbRow<Field> field, OnFieldValueSetListener listener) {
            textLabel.setText(field.getItem().getLabel());

            dateSetListener.setField(field);
            dateSetListener.setEditText(editText);
            dateSetListener.setListener(listener);

            invokerListener.setListener(dateSetListener);

            editText.setText(field.getItem().getValue());
            editText.setOnClickListener(invokerListener);

            clearButtonListener.setEditText(editText);
            clearButtonListener.setField(field);
            clearButtonListener.setListener(listener);

            clearButton.setOnClickListener(clearButtonListener);
        }
    }

    private static class OnEditTextClickListener implements OnClickListener {
        private DateSetListener listener;
        private LocalDate currentDate;
        private Context context;

        public OnEditTextClickListener(Context context) {
            this.context = context;
            currentDate = new LocalDate();
        }

        public void setListener(DateSetListener listener) {
            this.listener = listener;
        }

        public void setContext(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View view) {
            DatePickerDialog picker = new DatePickerDialog(context, listener,
                    currentDate.getYear(), currentDate.getMonthOfYear() - 1, currentDate.getDayOfMonth());
            picker.getDatePicker().setMaxDate(DateTime.now().getMillis());
            picker.show();
        }
    }

    private static class ClearButtonListener implements OnClickListener {
        private EditText editText;
        private DbRow<Field> field;
        private OnFieldValueSetListener listener;

        public void setEditText(EditText editText) {
            this.editText = editText;
        }

        public void setField(DbRow<Field> field) {
            this.field = field;
        }

        public void setListener(OnFieldValueSetListener listener) {
            this.listener = listener;
        }

        @Override
        public void onClick(View view) {
            editText.setText(EMPTY_FIELD);
            setValue(field, EMPTY_FIELD, listener);
        }
    }

    private class DateSetListener implements DatePickerDialog.OnDateSetListener {
        private static final String DATE_FORMAT = "YYYY-MM-dd";
        private DbRow<Field> field;
        private EditText editText;
        private OnFieldValueSetListener listener;

        public void setField(DbRow<Field> field) {
            this.field = field;
        }

        public void setEditText(EditText editText) {
            this.editText = editText;
        }

        public void setListener(OnFieldValueSetListener listener) {
            this.listener = listener;
        }

        @Override
        public void onDateSet(DatePicker view, int year,
                              int monthOfYear, int dayOfMonth) {
            LocalDate date = new LocalDate(year, monthOfYear + 1, dayOfMonth);
            String newValue = date.toString(DATE_FORMAT);
            editText.setText(newValue);
            setValue(field, newValue, listener);
        }
    }

    private static void setValue(DbRow<Field> field,
                                 String newValue,
                                 OnFieldValueSetListener listener) {
        String currentValue = field.getItem().getValue();
        if (newValue != null && !newValue.equals(currentValue)) {
            field.getItem().setValue(newValue);
            if (listener != null) {
                listener.onFieldValueSet(field.getId(), newValue);
            }
        }
    }
}
