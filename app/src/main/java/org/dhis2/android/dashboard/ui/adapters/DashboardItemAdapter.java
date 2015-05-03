/*
 * Copyright (c) 2015, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.dhis2.android.dashboard.ui.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.dhis2.android.dashboard.R;
import org.dhis2.android.dashboard.api.DhisManager;
import org.dhis2.android.dashboard.api.models.Access;
import org.dhis2.android.dashboard.api.models.DashboardItem;
import org.dhis2.android.dashboard.api.utils.PicassoProvider;

// TODO find bottleneck in performance of this adapter
public class DashboardItemAdapter extends AbsAdapter<DashboardItem> {
    private static final String DATE_FORMAT = "YYYY-MM-dd";

    private static final int MENU_GROUP_ID = 935482352;
    private static final int MENU_SHARE_ITEM_ID = 893226352;
    private static final int MENU_DELETE_ITEM_ID = 14920632;
    private static final int MENU_SHARE_ITEM_ORDER = 100;
    private static final int MENU_DELETE_ITEM_ORDER = 110;

    private String mServerUrl;
    private Access mDashboardAccess;

    private OnItemClickListener mClickListener;

    private Picasso mImageLoader;
    private Transformation mImageTransformation;

    public DashboardItemAdapter(Context context) {
        super(context, LayoutInflater.from(context));

        mImageLoader = PicassoProvider.getInstance(context);
        mImageTransformation = new ImgTransformation();
        mServerUrl = DhisManager.getInstance()
                .getServerUri().toString();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    public void setDashboardAccess(Access access) {
        mDashboardAccess = access;
    }

    @Override public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        long start = System.currentTimeMillis();
        View view;
        ViewHolder holder;

        if (convertView == null) {
            view = getLayoutInflater().inflate(
                    R.layout.gridview_dashboard_item, parent, false);

            View itemViewButton = view.findViewById(R.id.dashboard_item_menu);
            PopupMenu menu = new PopupMenu(getContext(), itemViewButton);
            menu.getMenu().add(MENU_GROUP_ID, MENU_SHARE_ITEM_ID,
                    MENU_SHARE_ITEM_ORDER, R.string.share_interpretation);
            menu.getMenu().add(MENU_GROUP_ID, MENU_DELETE_ITEM_ID,
                    MENU_DELETE_ITEM_ORDER, R.string.delete);

            holder = new ViewHolder(
                    view.findViewById(R.id.dashboard_item_body_container),
                    (TextView) view.findViewById(R.id.dashboard_item_name),
                    (ImageView) view.findViewById(R.id.dashboard_item_image),
                    (TextView) view.findViewById(R.id.dashboard_item_text),
                    (TextView) view.findViewById(R.id.dashboard_item_last_updated),
                    (ImageView) itemViewButton, menu
            );

            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        handleDashboardItems(((DashboardItem) getItem(position)), holder);
        System.out.println("GET_VIEW: " + (System.currentTimeMillis() - start));

        return view;
    }

    // TODO Try to create only one Click Listener for each type of view and keep it inside of view holder
    public void handleDashboardItems(final DashboardItem item,
                                     final ViewHolder holder) {
        boolean isItemShareable = false;
        boolean isDashboardManageable = false;
        boolean isItemDeletable = false;

        String lastUpdated = item.getLastUpdated()
                .toString(DATE_FORMAT);
        holder.lastUpdated.setText(lastUpdated);
        if (DashboardItem.TYPE_CHART.equals(item.getType()) && item.getChart() != null) {
            String request = mServerUrl + "/api/charts/" + item.getChart().getId() + "/data.png?width=480&height=320";
            handleItemsWithImages(item.getChart().getName(), request, holder);
            isItemShareable = true;
        } else if (DashboardItem.TYPE_MAP.equals(item.getType()) && item.getMap() != null) {
            String request = mServerUrl + "/api/maps/" + item.getMap().getId() + "/data.png?width=480&height=320";
            handleItemsWithImages(item.getMap().getName(), request, holder);
            isItemShareable = true;
        } else if (DashboardItem.TYPE_EVENT_CHART.equals(item.getType()) && item.getEventChart() != null) {
            String request = mServerUrl + "/api/eventCharts/" + item.getEventChart().getId() + "/data.png?width=480&height=320";
            handleItemsWithImages(item.getEventChart().getName(), request, holder);
            isItemShareable = false;
        } else if (DashboardItem.TYPE_REPORT_TABLE.equals(item.getType()) && item.getReportTable() != null) {
            handleItemsWithoutImages(item.getReportTable().getName(), holder);
            isItemShareable = true;
        } else if (DashboardItem.TYPE_USERS.equals(item.getType())) {
            handleItemsWithoutImages(getContext().getString(R.string.users), holder);
            isItemShareable = false;
        } else if (DashboardItem.TYPE_REPORTS.equals(item.getType())) {
            handleItemsWithoutImages(getContext().getString(R.string.reports), holder);
            isItemShareable = false;
        } else if (DashboardItem.TYPE_RESOURCES.equals(item.getType())) {
            handleItemsWithoutImages(getContext().getString(R.string.resources), holder);
            isItemShareable = false;
        }

        isDashboardManageable = mDashboardAccess.isManage();
        isItemDeletable = item.getAccess().isDelete();

        boolean isMenuVisible = isItemShareable || (isDashboardManageable && isItemDeletable);
        if (isMenuVisible) {
            holder.itemMenuButton.setVisibility(View.VISIBLE);

            MenuItem share = holder.popupMenu.getMenu().findItem(MENU_SHARE_ITEM_ID);
            MenuItem delete = holder.popupMenu.getMenu().findItem(MENU_DELETE_ITEM_ID);

            share.setVisible(isItemShareable);
            delete.setVisible(isDashboardManageable && isItemDeletable);
        } else {
            holder.itemMenuButton.setVisibility(View.GONE);
        }

        holder.itemMenuButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (holder.popupMenu != null) {
                    holder.popupMenu.show();
                }
            }
        });

        holder.itemBody.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (mClickListener != null) {
                    mClickListener.onItemClick(item);
                }
            }
        });

        holder.popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (mClickListener == null) {
                    return false;
                }

                if (menuItem.getItemId() == MENU_SHARE_ITEM_ID) {
                    mClickListener.onItemShareInterpretation(item);
                } else if (menuItem.getItemId() == MENU_DELETE_ITEM_ID) {
                    mClickListener.onItemDelete(item);
                }

                return true;
            }
        });
    }

    private void handleItemsWithImages(String name, String request, ViewHolder holder) {
        holder.itemName.setVisibility(View.VISIBLE);
        holder.itemImage.setVisibility(View.VISIBLE);
        holder.itemText.setVisibility(View.GONE);

        holder.itemName.setText(name);
        mImageLoader.load(request)
                //.transform(mImageTransformation)
                .placeholder(R.mipmap.stub_dashboard_background)
                .into(holder.itemImage);
    }

    private void handleItemsWithoutImages(String text, ViewHolder holder) {
        holder.itemImage.setVisibility(View.GONE);
        holder.itemName.setVisibility(View.GONE);
        holder.itemText.setVisibility(View.VISIBLE);

        holder.itemText.setText(text);
    }

    public interface OnItemClickListener {
        void onItemClick(DashboardItem item);

        void onItemShareInterpretation(DashboardItem item);

        void onItemDelete(DashboardItem item);
    }

    private static class ViewHolder {
        final View itemBody;
        final TextView itemName;
        final ImageView itemImage;
        final TextView itemText;
        final TextView lastUpdated;
        final ImageView itemMenuButton;
        final PopupMenu popupMenu;

        /*
        view.findViewById(R.id.dashboard_item_body_container),
                    (TextView) view.findViewById(R.id.dashboard_item_name),
                    (ImageView) view.findViewById(R.id.dashboard_item_image),
                    (TextView) view.findViewById(R.id.dashboard_item_text),
                    (TextView) view.findViewById(R.id.dashboard_item_last_updated),
                    (ImageButton) itemViewButton, menu
         */
        ViewHolder(View itemBody,
                   TextView itemName,
                   ImageView itemImage,
                   TextView itemText,
                   TextView lastUpdated,
                   ImageView itemMenuButton,
                   PopupMenu popupMenu) {
            this.itemBody = itemBody;
            this.itemName = itemName;
            this.itemImage = itemImage;
            this.itemText = itemText;
            this.lastUpdated = lastUpdated;
            this.itemMenuButton = itemMenuButton;
            this.popupMenu = popupMenu;
        }
    }

    private static class ImgTransformation implements Transformation {
        private static final String KEY = "imageResizeTransformation";

        @Override
        public Bitmap transform(Bitmap source) {
            float initialArea = source.getHeight() * source.getWidth();
            float compArea = calculateArea(initialArea, 10);
            Double rate = Math.sqrt(compArea / initialArea);

            int x = Math.round(source.getWidth() * rate.floatValue());
            int y = Math.round(source.getHeight() * rate.floatValue());

            Bitmap result = Bitmap.createScaledBitmap(source, x, y, false);
            if (result != source) {
                source.recycle();
            }
            return result;
        }

        @Override
        public String key() {
            return KEY;
        }

        private float calculateArea(float initialArea, int resizeRate) {
            return (initialArea * resizeRate) / 100;
        }
    }
}