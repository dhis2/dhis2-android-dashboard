package org.hisp.dhis.mobile.datacapture.ui.adapters.rows;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import org.hisp.dhis.mobile.datacapture.R;
import org.hisp.dhis.mobile.datacapture.api.android.models.DbRow;
import org.hisp.dhis.mobile.datacapture.api.models.Field;

public class RadioButtonsRow implements Row {
    private static final String EMPTY_FIELD = "";
    private static final String TRUE = "true";
    private static final String FALSE = "false";

    public static final String FEMALE = "gender_female";
    public static final String MALE = "gender_male";
    public static final String OTHER = "gender_other";

    private final DbRow<Field> mField;
    private final RowTypes mType;
    private OnFieldValueSetListener mListener;

    public RadioButtonsRow(DbRow<Field> field, RowTypes type) {
        if (!RowTypes.GENDER.equals(type) && !RowTypes.BOOLEAN.equals(type)) {
            throw new IllegalArgumentException("Unsupported row type");
        }

        mField = field;
        mType = type;
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView, ViewGroup container) {
        View view;
        BooleanRowHolder holder;

        if (convertView == null) {
            View root = inflater.inflate(
                    R.layout.listview_row_radio_buttons, container, false);
            TextView label = (TextView)
                    root.findViewById(R.id.text_label);
            CompoundButton firstButton = (CompoundButton)
                    root.findViewById(R.id.first_radio_button);
            CompoundButton secondButton = (CompoundButton)
                    root.findViewById(R.id.second_radio_button);
            CompoundButton thirdButton = (CompoundButton)
                    root.findViewById(R.id.third_radio_button);

            if (RowTypes.BOOLEAN.equals(mType)) {
                firstButton.setText(R.string.yes);
                secondButton.setText(R.string.no);
                thirdButton.setText(R.string.none);
            } else if (RowTypes.GENDER.equals(mType)) {
                firstButton.setText(R.string.gender_male);
                secondButton.setText(R.string.gender_female);
                thirdButton.setText(R.string.gender_other);
            }

            CheckedChangeListener listener = new CheckedChangeListener();
            holder = new BooleanRowHolder(mType, label, firstButton,
                    secondButton, thirdButton, listener);

            root.setTag(holder);
            view = root;
        } else {
            view = convertView;
            holder = (BooleanRowHolder) convertView.getTag();
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
        return mType.ordinal();
    }

    private static class BooleanRowHolder {
        final TextView textLabel;
        final CompoundButton firstButton;
        final CompoundButton secondButton;
        final CompoundButton thirdButton;
        final CheckedChangeListener listener;
        final RowTypes type;

        public BooleanRowHolder(RowTypes type, TextView textLabel, CompoundButton firstButton,
                                CompoundButton secondButton, CompoundButton thirdButton,
                                CheckedChangeListener listener) {
            this.type = type;
            this.textLabel = textLabel;
            this.firstButton = firstButton;
            this.secondButton = secondButton;
            this.thirdButton = thirdButton;
            this.listener = listener;
        }

        public void updateViews(DbRow<Field> field,
                                OnFieldValueSetListener onValueSetListener) {
            textLabel.setText(field.getItem().getLabel());

            listener.setType(type);
            listener.setField(field);
            listener.setListener(onValueSetListener);

            firstButton.setOnCheckedChangeListener(listener);
            secondButton.setOnCheckedChangeListener(listener);
            thirdButton.setOnCheckedChangeListener(listener);

            String value = field.getItem().getValue();
            if (RowTypes.BOOLEAN.equals(type)) {
                if (TRUE.equalsIgnoreCase(value)) {
                    firstButton.setChecked(true);
                } else if (FALSE.equalsIgnoreCase(value)) {
                    secondButton.setChecked(true);
                } else if (EMPTY_FIELD.equalsIgnoreCase(value)) {
                    thirdButton.setChecked(true);
                }
            } else if (RowTypes.GENDER.equals(type)) {
                if (MALE.equalsIgnoreCase(value)) {
                    firstButton.setChecked(true);
                } else if (FEMALE.equalsIgnoreCase(value)) {
                    secondButton.setChecked(true);
                } else if (OTHER.equalsIgnoreCase(value)) {
                    thirdButton.setChecked(true);
                }
            }
        }
    }

    private static class CheckedChangeListener implements OnCheckedChangeListener {
        private DbRow<Field> field;
        private RowTypes type;
        private OnFieldValueSetListener listener;

        public void setField(DbRow<Field> field) {
            this.field = field;
        }

        public void setType(RowTypes type) {
            this.type = type;
        }

        public void setListener(OnFieldValueSetListener listener) {
            this.listener = listener;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // if one of buttons in group is unchecked, another one has to be checked
            // So we are not interested in events where button is being unchecked
            if (!isChecked) {
                return;
            }

            if (RowTypes.BOOLEAN.equals(type)) {
                switch (buttonView.getId()) {
                    case R.id.first_radio_button: {
                        setValue(TRUE);
                        break;
                    }
                    case R.id.second_radio_button: {
                        setValue(FALSE);
                        break;
                    }
                    case R.id.third_radio_button: {
                        setValue(EMPTY_FIELD);
                        break;
                    }
                }
            }

            if (RowTypes.GENDER.equals(type)) {
                switch (buttonView.getId()) {
                    case R.id.first_radio_button: {
                        setValue(MALE);
                        break;
                    }
                    case R.id.second_radio_button: {
                        setValue(FEMALE);
                        break;
                    }
                    case R.id.third_radio_button: {
                        setValue(OTHER);
                        break;
                    }
                }
            }
        }

        private void setValue(String newValue) {
            String currentValue = field.getItem().getValue();
            if (!newValue.equals(currentValue)) {
                field.getItem().setValue(newValue);
                if (listener != null) {
                    listener.onFieldValueSet(field.getId(), newValue);
                }
            }
        }
    }

}





