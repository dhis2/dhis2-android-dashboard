package org.hisp.dhis.mobile.datacapture.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.hisp.dhis.mobile.datacapture.R;

public class CardDetailedButton extends AbsCardDetailedButton {

    public CardDetailedButton(Context context) {
        super(context);
    }

    public CardDetailedButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CardDetailedButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected View initAdditionalView() {
        // configure image view
        LinearLayout.LayoutParams imgViewParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        imgViewParams.gravity = Gravity.CENTER_VERTICAL;

        ImageView imageView = new ImageView(getContext());
        imageView.setImageResource(R.drawable.ic_next);
        imageView.setLayoutParams(imgViewParams);
        return imageView;
    }

    public void show(boolean withAnim) {
        if (getVisibility() != View.VISIBLE) {
            if (withAnim) {
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.in_up);
                startAnimation(animation);
            }
            setVisibility(View.VISIBLE);
        }
    }

    public void hide(boolean withAnim) {
        if (getVisibility() == View.VISIBLE) {
            if (withAnim) {
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.out_down);
                startAnimation(animation);
            }
            setVisibility(View.INVISIBLE);
        }
    }
}
