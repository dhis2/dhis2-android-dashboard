package org.hisp.dhis.mobile.datacapture.ui.views;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import org.hisp.dhis.mobile.datacapture.R;

public class CardDetailedButtonOverflow extends AbsCardDetailedButton {
    private static final int MENU_GROUP_ID = 234127542;

    private PopupMenu mMenu;
    private ImageButton mMenuButton;

    public CardDetailedButtonOverflow(Context context) {
        super(context);
        initMenu();
    }

    public CardDetailedButtonOverflow(Context context, AttributeSet attrs) {
        super(context, attrs);
        initMenu();
    }

    public CardDetailedButtonOverflow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initMenu();
    }

    @Override
    protected View initAdditionalView() {
        int pxs = toPx(32);
        LinearLayout.LayoutParams imgViewParams = new LinearLayout.LayoutParams(pxs, pxs);
        imgViewParams.gravity = Gravity.CENTER_HORIZONTAL;
        mMenuButton = new ImageButton(getContext());
        mMenuButton.setImageResource(R.drawable.ic_overflow_menu);
        mMenuButton.setLayoutParams(imgViewParams);
        mMenuButton.setBackgroundResource(R.drawable.transparent_selector);
        return mMenuButton;
    }

    private void initMenu() {
        mMenu = new PopupMenu(getContext(), mMenuButton);
        mMenuButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mMenu.show();
            }
        });
    }

    public void addMenuItem(int menuItemId, int menuItemOrder, int label) {
        mMenu.getMenu().add(MENU_GROUP_ID, menuItemId, menuItemOrder, "Hello");
    }

    public void setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener listener) {
        mMenu.setOnMenuItemClickListener(listener);
    }

    private int toPx(int dps) {
        final float scale = getContext().getResources()
                .getDisplayMetrics().density;
        return (int) (dps * scale + 0.5f);
    }
}
