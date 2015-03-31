package org.hisp.dhis.mobile.datacapture.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

import org.hisp.dhis.mobile.datacapture.R;
import org.hisp.dhis.mobile.datacapture.utils.TypefaceManager;

public class FontAutoCompleteTextView extends AutoCompleteTextView {

    public FontAutoCompleteTextView(Context context) {
        super(context);
    }

    public FontAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FontAutoCompleteTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        if (!isInEditMode()) {
            TypedArray attrs = context.obtainStyledAttributes(attributeSet, R.styleable.ViewFont);
            setFont(attrs.getString(R.styleable.ViewFont_font));
            attrs.recycle();
        }
    }

    public void setFont(int restId) {
        String name = getResources().getString(restId);
        setFont(name);
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
