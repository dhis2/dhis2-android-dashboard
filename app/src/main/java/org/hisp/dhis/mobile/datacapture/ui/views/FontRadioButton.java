package org.hisp.dhis.mobile.datacapture.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.RadioButton;

import org.hisp.dhis.mobile.datacapture.R;
import org.hisp.dhis.mobile.datacapture.utils.TypefaceManager;

public class FontRadioButton extends RadioButton {
    public FontRadioButton(Context context) {
        super(context);
    }

    public FontRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FontRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        if (!isInEditMode()) {
            TypedArray attrs = context.obtainStyledAttributes(attributeSet, R.styleable.ViewFont);
            setFont(attrs.getString(R.styleable.ViewFont_font));
            attrs.recycle();
        }
    }

    private void setFont(final String fontName) {
        if (getContext() != null && getContext().getAssets() != null && fontName != null) {
            Typeface typeface = TypefaceManager.getTypeface(getContext().getAssets(), fontName);
            if (typeface != null) {
                setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
                setTypeface(typeface);
            }
        }
    }
}
