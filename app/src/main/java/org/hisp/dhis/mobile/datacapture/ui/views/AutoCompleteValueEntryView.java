package org.hisp.dhis.mobile.datacapture.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import org.hisp.dhis.mobile.datacapture.R;

import java.util.List;

public class AutoCompleteValueEntryView extends LinearLayout implements View.OnClickListener {
    private static final String EMPTY_FIELD = "";
    private static final int NORMAL_TEXT_SIZE = 16;
    private static final int SMALL_TEXT_SIZE = 11;

    private static final float AUTO_COMPLETE_CONTAINER_WEIGHT_SUM = 1f;
    private static final float AUTO_COMPLETE_WEIGHT = 0.9f;
    private static final float DROP_DOWN_BUTTON_WEIGHT = 0.1f;

    private FrameLayout mTextContainer;
    private FontTextView mTextView;

    private LinearLayout mAutoCompleteContainer;
    private AutoCompleteAdapter mAdapter;
    private FontAutoCompleteTextView mAutoComplete;
    private ImageButton mShowDropDown;

    private LinearLayout mButtonsContainer;
    private FontTextView mCancelButton;
    private FontTextView mSaveButton;

    private OnValueSetListener mListener;

    private List<String> mOptions;

    public AutoCompleteValueEntryView(Context context) {
        super(context);
        init();
    }

    public AutoCompleteValueEntryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AutoCompleteValueEntryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutParams rootViewParams = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );

        setLayoutParams(rootViewParams);
        setOrientation(LinearLayout.VERTICAL);

        // init text views inside of container
        mTextContainer = initTextContainer();
        mTextView = initTextView();

        // init auto complete with drop down button
        mAutoCompleteContainer = initAutoCompleteContainer();
        mAdapter = new AutoCompleteAdapter(getContext());
        mAutoComplete = initAutoComplete();
        mAutoComplete.setAdapter(mAdapter);

        mShowDropDown = initDropDownButton();
        mAutoCompleteContainer.addView(mAutoComplete);
        mAutoCompleteContainer.addView(mShowDropDown);
        mAutoCompleteContainer.setVisibility(View.GONE);

        mTextContainer.addView(mTextView);
        mTextContainer.addView(mAutoCompleteContainer);

        // init buttons inside of container
        mButtonsContainer = initButtonsContainer();
        mButtonsContainer.setVisibility(View.GONE);

        String cancel = getResources().getString(R.string.cancel);
        String save = getResources().getString(R.string.save);

        mCancelButton = initButton(cancel.toUpperCase());
        mSaveButton = initButton(save.toUpperCase());

        mButtonsContainer.addView(mCancelButton);
        mButtonsContainer.addView(mSaveButton);

        addView(mTextContainer);
        addView(mButtonsContainer);
    }

    private FrameLayout initTextContainer() {
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT
        );

        FrameLayout editTextContainer = new FrameLayout(getContext());
        editTextContainer.setLayoutParams(params);

        return editTextContainer;
    }

    private FontTextView initTextView() {
        LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT
        );

        int padding = getResources().getDimensionPixelSize(
                R.dimen.value_entry_view_text_view_padding);

        FontTextView textView = new FontTextView(getContext());
        textView.setFont(R.string.regular_font_name);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, NORMAL_TEXT_SIZE);
        textView.setLayoutParams(params);
        textView.setClickable(true);
        textView.setOnClickListener(this);
        textView.setHint(R.string.find_option);
        textView.setBackgroundResource(R.drawable.textfield_disabled_holo_light);
        textView.setPadding(padding, padding, padding, padding);

        return textView;
    }

    private LinearLayout initAutoCompleteContainer() {
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                AUTO_COMPLETE_CONTAINER_WEIGHT_SUM
        );

        LinearLayout container = new LinearLayout(getContext());
        container.setLayoutParams(params);
        container.setOrientation(LinearLayout.HORIZONTAL);

        return container;
    }

    private FontAutoCompleteTextView initAutoComplete() {
        LayoutParams params = new LayoutParams(
                0, LayoutParams.WRAP_CONTENT,
                AUTO_COMPLETE_WEIGHT
        );

        int padding = getResources().getDimensionPixelSize(
                R.dimen.value_entry_view_edit_text_padding);
        int black = getResources().getColor(R.color.black);

        FontAutoCompleteTextView autoComplete = new FontAutoCompleteTextView(getContext());
        autoComplete.setLayoutParams(params);
        autoComplete.setFont(R.string.regular_font_name);
        autoComplete.setTextSize(TypedValue.COMPLEX_UNIT_SP, NORMAL_TEXT_SIZE);
        autoComplete.setBackgroundResource(R.drawable.editbox_background_normal);
        autoComplete.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        autoComplete.setPadding(padding, padding, padding, padding);
        autoComplete.setHint(R.string.find_option);
        autoComplete.setSingleLine(true);
        autoComplete.setThreshold(1);
        autoComplete.setTextColor(black);

        return autoComplete;
    }

    private ImageButton initDropDownButton() {
        LayoutParams params = new LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT,
                DROP_DOWN_BUTTON_WEIGHT
        );

        ImageButton button = new ImageButton(getContext());
        button.setLayoutParams(params);
        button.setImageResource(R.drawable.ic_drop_down);
        button.setBackgroundResource(R.drawable.transparent_selector);
        button.setOnClickListener(this);
        button.setPadding(40, 20, 40, 20);

        return button;
    }

    private LinearLayout initButtonsContainer() {
        LayoutParams buttonsContainerParams = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        LinearLayout buttonsContainer = new LinearLayout(getContext());
        buttonsContainer.setLayoutParams(buttonsContainerParams);
        buttonsContainer.setOrientation(LinearLayout.HORIZONTAL);
        buttonsContainer.setGravity(Gravity.RIGHT);

        return buttonsContainer;
    }

    private FontTextView initButton(String text) {
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );

        int navyBlue = getResources().getColor(R.color.navy_blue);
        int padding = getResources().getDimensionPixelSize(
                R.dimen.value_entry_view_edit_text_padding);
        int margin = getResources().getDimensionPixelSize(
                R.dimen.value_entry_view_button_margin);
        params.setMargins(margin, 0, margin, 0);

        FontTextView button = new FontTextView(getContext());
        button.setLayoutParams(params);
        button.setFont(R.string.bold_font_name);
        button.setText(text);
        button.setTextColor(navyBlue);
        button.setBackgroundResource(R.drawable.transparent_selector);
        button.setTextSize(TypedValue.COMPLEX_UNIT_SP, SMALL_TEXT_SIZE);
        button.setPadding(padding, padding, padding, padding);
        button.setClickable(true);
        button.setOnClickListener(this);

        return button;
    }

    @Override
    public void onClick(View view) {
        if (view == mTextView) {
            showEntryView();
        } else if (view == mShowDropDown) {
            triggerDropDown();
        } else if (view == mCancelButton) {
            hideEntryView(false);
        } else if (view == mSaveButton) {
            hideEntryView(true);
        }
    }

    private void triggerDropDown() {
        mAutoComplete.showDropDown();
    }

    private void showEntryView() {
        mTextView.setVisibility(View.GONE);
        mAutoCompleteContainer.setVisibility(View.VISIBLE);
        mAutoComplete.requestFocus();
        mAutoComplete.setHint(mTextView.getHint());
        mAutoComplete.setText(mTextView.getText());
        mButtonsContainer.setVisibility(View.VISIBLE);
    }

    private void hideEntryView(boolean saveValue) {
        if (saveValue) {
            String value = mAutoComplete.getText().toString();
            if (mOptions != null) {
                if (EMPTY_FIELD.equals(value) || mOptions.contains(value)) {
                    mTextView.setText(value);
                    hideEntryView();
                    if (mListener != null) {
                        mListener.onValueSet(value);
                    }
                } else {
                    mAutoComplete.setError("Incorrect value");
                }
            }
        } else {
            hideEntryView();
        }
    }

    private void hideEntryView() {
        mTextView.setVisibility(View.VISIBLE);
        mAutoComplete.clearFocus();
        mAutoCompleteContainer.setVisibility(View.GONE);
        mButtonsContainer.setVisibility(View.GONE);
    }

    public void resetView() {
        hideEntryView(false);
    }

    public void setOnValueSetListener(OnValueSetListener listener) {
        mListener = listener;
    }

    public void setText(CharSequence text) {
        mTextView.setText(text);
    }

    public void setText(int resId) {
        mTextView.setText(resId);
    }

    public void swapData(List<String> options) {
        mOptions = options;
        mAdapter.swapData(options);
    }

    public static interface OnValueSetListener {
        public void onValueSet(String value);
    }
}
