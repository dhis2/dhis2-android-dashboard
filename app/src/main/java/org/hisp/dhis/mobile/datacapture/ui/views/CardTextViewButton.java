package org.hisp.dhis.mobile.datacapture.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;

import org.hisp.dhis.mobile.datacapture.R;

public class CardTextViewButton extends CardView {
    private FontTextView mTextView;
    private CharSequence mHint;

    public CardTextViewButton(Context context) {
        super(context);
        init(context);
    }

    public CardTextViewButton(Context context, AttributeSet attributes) {
        super(context, attributes);
        init(context);

        if (!isInEditMode()) {
            TypedArray attrs = context.obtainStyledAttributes(attributes, R.styleable.ButtonHint);
            mHint = attrs.getString(R.styleable.ButtonHint_hint);
            setText(mHint);
            attrs.recycle();
        }
    }

    private void init(Context context) {
        int pxs = getResources().getDimensionPixelSize(R.dimen.card_text_view_margin);
        LayoutParams textViewParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        textViewParams.setMargins(pxs, pxs, pxs, pxs);

        mTextView = new FontTextView(context);
        mTextView.setClickable(true);
        mTextView.setId(getId());
        mTextView.setBackgroundResource(R.drawable.spinner_background_holo_light);
        mTextView.setFont(getContext().getString(R.string.regular_font_name));
        mTextView.setLayoutParams(textViewParams);

        addView(mTextView);
    }

    public void setText(CharSequence sequence) {
        if (mTextView != null && sequence != null) {
            mTextView.setText(sequence);
        }
    }

    public void setOnClickListener(OnClickListener listener) {
        mTextView.setOnClickListener(listener);
    }

    @Override
    public void setEnabled(boolean isEnabled) {
        super.setEnabled(isEnabled);
        mTextView.setEnabled(isEnabled);
        setText(mHint);
    }
}
