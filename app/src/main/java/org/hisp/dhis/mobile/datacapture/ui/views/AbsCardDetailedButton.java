package org.hisp.dhis.mobile.datacapture.ui.views;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.hisp.dhis.mobile.datacapture.R;

public abstract class AbsCardDetailedButton extends CardView {
    private static final float WEIGHT_SUM = 1.0f;
    private static final float TEXT_VIEW_CONTAINER_WEIGHT = 0.95f;
    private static final float ADDITIONAL_VIEW_WEIGHT = 0.05f;

    private static final int BIG_TEXT_SIZE = 17;
    private static final int SMALL_TEXT_SIZE = 13;

    private FontTextView mFirstLine;
    private FontTextView mSecondLine;
    private FontTextView mThirdLine;
    private LinearLayout mContainer;

    public AbsCardDetailedButton(Context context) {
        super(context);
        init();
    }

    public AbsCardDetailedButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AbsCardDetailedButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // configure CardView first
        ViewGroup.LayoutParams cardViewParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        setLayoutParams(cardViewParams);

        mFirstLine = initTextView(R.string.regular_font_name, BIG_TEXT_SIZE, true);
        mSecondLine = initTextView(R.string.light_font_name, SMALL_TEXT_SIZE, false);
        mThirdLine = initTextView(R.string.light_font_name, SMALL_TEXT_SIZE, true);
        mContainer = initContainer();

        LinearLayout textViewsContainer = initTextViewContainer();
        textViewsContainer.addView(mFirstLine);
        textViewsContainer.addView(mSecondLine);
        textViewsContainer.addView(mThirdLine);

        View view = initAdditionalView();
        if (view != null) {
            LinearLayout.LayoutParams viewParams = (LinearLayout.LayoutParams)
                    view.getLayoutParams();
            viewParams.weight = ADDITIONAL_VIEW_WEIGHT;
        }

        // attach both text view container and image view to main container
        mContainer.addView(textViewsContainer);
        if (view != null) {
            mContainer.addView(view);
        }

        // attach main container to card view
        addView(mContainer);
    }

    private LinearLayout initContainer() {
        // configure container with contents
        ViewGroup.LayoutParams containerParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        int containerPadding = getResources().getDimensionPixelSize(
                R.dimen.card_detail_button_padding);

        LinearLayout container = new LinearLayout(getContext());
        container.setId(getId());
        container.setBackgroundResource(R.drawable.transparent_selector);
        container.setLayoutParams(containerParams);
        container.setOrientation(LinearLayout.HORIZONTAL);
        container.setPadding(containerPadding, containerPadding,
                containerPadding, containerPadding);
        container.setWeightSum(WEIGHT_SUM);
        container.setClickable(true);

        return container;
    }

    private FontTextView initTextView(int fontId, int textSize, boolean margin) {
        int color = getResources().getColor(R.color.grey);

        FontTextView textView = new FontTextView(getContext());
        textView.setSingleLine(true);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setFont(getResources().getString(fontId));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        textView.setTextColor(color);

        if (margin) {
            LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
            );
            int textViewMargin = getResources().getDimensionPixelSize(
                    R.dimen.card_detail_text_view_margin);
            textViewParams.setMargins(0, 0, 0, textViewMargin);
            textView.setLayoutParams(textViewParams);
        }

        return textView;
    }

    private LinearLayout initTextViewContainer() {
        // configure container with text views
        LinearLayout.LayoutParams textViewContainerParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                TEXT_VIEW_CONTAINER_WEIGHT
        );
        LinearLayout textViewsContainer = new LinearLayout(getContext());
        textViewsContainer.setLayoutParams(textViewContainerParams);
        textViewsContainer.setOrientation(LinearLayout.VERTICAL);
        return textViewsContainer;
    }

    public void setFirstLineText(CharSequence text) {
        mFirstLine.setText(text);
    }

    public void setSecondLineText(CharSequence text) {
        mSecondLine.setText(text);
    }

    public void setThirdLineText(CharSequence text) {
        mThirdLine.setText(text);
    }

    @Override
    public void setOnClickListener(OnClickListener clickListener) {
        mContainer.setOnClickListener(clickListener);
    }

    protected abstract View initAdditionalView();
}
