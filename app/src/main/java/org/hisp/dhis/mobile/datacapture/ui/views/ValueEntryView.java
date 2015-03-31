package org.hisp.dhis.mobile.datacapture.ui.views;

import android.content.Context;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import org.hisp.dhis.mobile.datacapture.R;

public class ValueEntryView extends LinearLayout implements View.OnClickListener {
    private static final int NORMAL_TEXT_SIZE = 16;
    private static final int SMALL_TEXT_SIZE = 11;

    private FrameLayout mTextContainer;
    private FontTextView mTextView;
    private FontEditText mEditText;

    private LinearLayout mButtonsContainer;
    private FontTextView mCancelButton;
    private FontTextView mSaveButton;

    private OnValueSetListener mListener;

    public ValueEntryView(Context context) {
        super(context);
        init();
    }

    public ValueEntryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ValueEntryView(Context context, AttributeSet attrs, int defStyleAttr) {
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
        mEditText = initEditText();
        mEditText.setVisibility(View.GONE);

        mTextContainer.addView(mTextView);
        mTextContainer.addView(mEditText);

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
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        );

        int padding = getResources().getDimensionPixelSize(
                R.dimen.value_entry_view_text_view_padding);

        FontTextView textView = new FontTextView(getContext());
        textView.setFont(R.string.regular_font_name);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, NORMAL_TEXT_SIZE);
        textView.setLayoutParams(params);
        textView.setClickable(true);
        textView.setOnClickListener(this);
        textView.setBackgroundResource(R.drawable.textfield_disabled_holo_light);
        textView.setPadding(padding, padding, padding, padding);

        return textView;
    }

    private FontEditText initEditText() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        );

        int padding = getResources().getDimensionPixelSize(
                R.dimen.value_entry_view_edit_text_padding);

        FontEditText editText = new FontEditText(getContext());
        editText.setLayoutParams(params);
        editText.setFont(R.string.regular_font_name);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, NORMAL_TEXT_SIZE);
        editText.setBackgroundResource(R.drawable.editbox_background_normal);
        editText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        editText.setPadding(padding, padding, padding, padding);
        editText.setSingleLine(true);

        return editText;
    }

    private LinearLayout initButtonsContainer() {
        LayoutParams buttonsContainerParams = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
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
        } else if (view == mCancelButton) {
            hideEntryView(false);
        } else if (view == mSaveButton) {
            hideEntryView(true);
        }
    }

    private void showEntryView() {
        mTextView.setVisibility(View.GONE);
        mEditText.setVisibility(View.VISIBLE);
        mEditText.requestFocus();
        mEditText.setHint(mTextView.getHint());
        mEditText.setText(mTextView.getText());
        mButtonsContainer.setVisibility(View.VISIBLE);
    }

    private void hideEntryView(boolean saveValue) {
        mTextView.setVisibility(View.VISIBLE);
        mEditText.setVisibility(View.GONE);
        mEditText.clearFocus();
        if (saveValue) {
            String value = mEditText.getText().toString();
            mTextView.setText(value);
            if (mListener != null) {
                mListener.onValueSet(value);
            }
        }
        mButtonsContainer.setVisibility(View.GONE);
    }

    public void resetView() {
        hideEntryView(false);
    }

    public void setOnValueSetListener(OnValueSetListener listener) {
        mListener = listener;
    }

    public void setHint(int resId) {
        mTextView.setHint(resId);
    }

    public void setHint(CharSequence hint) {
        mTextView.setHint(hint);
    }

    public void setText(CharSequence text) {
        mTextView.setText(text);
    }

    public void setText(int resId) {
        mTextView.setText(resId);
    }

    public void setInputType(int type) {
        mEditText.setInputType(type);
    }

    public void setFilters(InputFilter[] filters) {
        mEditText.setFilters(filters);
    }

    public static interface OnValueSetListener {
        public void onValueSet(String value);
    }
}
