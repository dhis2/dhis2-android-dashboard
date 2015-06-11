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
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.dhis2.android.dashboard.R;
import org.dhis2.android.dashboard.api.DhisManager;
import org.dhis2.android.dashboard.api.models.Access;
import org.dhis2.android.dashboard.api.models.DashboardElement;
import org.dhis2.android.dashboard.api.models.DashboardItem;
import org.dhis2.android.dashboard.api.utils.PicassoProvider;


public class DashboardItemAdapter extends AbsAdapter<DashboardItem, DashboardItemAdapter.DashboardViewHolder> {
    private static final String DATE_FORMAT = "YYYY-MM-dd";

    private final Access mDashboardAccess;
    private final OnItemClickListener mClickListener;

    private Picasso mImageLoader;

    public DashboardItemAdapter(Context context, Access dashboardAccess,
                                OnItemClickListener clickListener) {
        super(context, LayoutInflater.from(context));

        mDashboardAccess = dashboardAccess;
        mClickListener = clickListener;
        mImageLoader = PicassoProvider.getInstance(context);
    }

    @Override
    public DashboardViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        return new DashboardViewHolder(getLayoutInflater().inflate(
                R.layout.gridview_dashboard_item, parent, false), mDashboardAccess, mClickListener);
    }

    @Override
    public void onBindViewHolder(DashboardViewHolder holder, int position) {
        handleDashboardItems(position, holder);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void removeItem(DashboardItem item) {
        if (getData() != null) {
            int truePosition = getData().indexOf(item);
            if (!(truePosition < 0)) {
                getData().remove(truePosition);
                notifyItemRemoved(truePosition);
            }
        }
    }

    private void handleDashboardItems(int position, DashboardViewHolder holder) {
        DashboardItem item = getItem(position);

        holder.menuButtonHandler.setDashboardItem(item);
        holder.onItemBodyClickListener.setDashboardItem(item);
        holder.lastUpdated.setText(item.getLastUpdated().toString(DATE_FORMAT));

        if (DashboardElement.TYPE_CHART.equals(item.getType()) && item.getChart() != null) {
            String request = buildImageUrl("api/charts/", item.getChart().getId());
            handleItemsWithImages(item.getChart().getName(), request, holder);
        } else if (DashboardElement.TYPE_MAP.equals(item.getType()) && item.getMap() != null) {
            String request = buildImageUrl("api/maps/", item.getMap().getId());
            handleItemsWithImages(item.getMap().getName(), request, holder);
        } else if (DashboardElement.TYPE_EVENT_CHART.equals(item.getType()) && item.getEventChart() != null) {
            String request = buildImageUrl("api/eventCharts/", item.getEventChart().getId());
            handleItemsWithImages(item.getEventChart().getName(), request, holder);
        } else if (DashboardElement.TYPE_REPORT_TABLE.equals(item.getType()) && item.getReportTable() != null) {
            handleItemsWithoutImages(item.getReportTable().getName(), holder);
        } else if (DashboardElement.TYPE_EVENT_REPORT.equals(item.getType()) && item.getEventReport() != null) {
            handleItemsWithoutImages(item.getEventReport().getName(), holder);
        } else if (DashboardElement.TYPE_USERS.equals(item.getType())) {
            handleItemsWithoutImages(getContext().getString(R.string.users), holder);
        } else if (DashboardElement.TYPE_REPORTS.equals(item.getType())) {
            handleItemsWithoutImages(getContext().getString(R.string.reports), holder);
        } else if (DashboardElement.TYPE_RESOURCES.equals(item.getType())) {
            handleItemsWithoutImages(getContext().getString(R.string.resources), holder);
        }

        if (holder.menuButtonHandler.isMenuVisible()) {
            holder.itemMenuButton.setVisibility(View.VISIBLE);
        } else {
            holder.itemMenuButton.setVisibility(View.GONE);
        }
    }

    private static class OnItemBodyClickListener implements View.OnClickListener {
        private final OnItemClickListener mListener;
        private DashboardItem mDashboardItem;

        public OnItemBodyClickListener(OnItemClickListener listener) {
            mListener = listener;
        }

        public void setDashboardItem(DashboardItem dashboardItem) {
            mDashboardItem = dashboardItem;
        }

        @Override public void onClick(View view) {
            mListener.onItemClick(mDashboardItem);
        }
    }

    private static class MenuButtonHandler implements View.OnClickListener {
        private static final int MENU_GROUP_ID = 9382352;
        private static final int MENU_SHARE_ITEM_ID = 8936352;
        private static final int MENU_DELETE_ITEM_ID = 149232;
        private static final int MENU_SHARE_ITEM_ORDER = 100;
        private static final int MENU_DELETE_ITEM_ORDER = 110;

        private final Context mContext;
        private final Access mDashboardAccess;
        private final OnItemClickListener mListener;
        private DashboardItem mDashboardItem;

        public MenuButtonHandler(Context context, Access dashboardAccess,
                                 OnItemClickListener listener) {
            mContext = context;
            mDashboardAccess = dashboardAccess;
            mListener = listener;
        }

        public void setDashboardItem(DashboardItem dashboardItem) {
            mDashboardItem = dashboardItem;
        }

        public boolean isMenuVisible() {
            return isItemShareable() ||
                    (isDashboardManageable() && isItemDeletable());
        }

        private boolean isItemShareable() {
            return mDashboardItem != null && (
                    DashboardElement.TYPE_CHART.equals(mDashboardItem.getType()) ||
                            DashboardElement.TYPE_MAP.equals(mDashboardItem.getType()) ||
                            DashboardElement.TYPE_REPORT_TABLE.equals(mDashboardItem.getType())
            );
        }

        private boolean isDashboardManageable() {
            return mDashboardAccess.isManage();
        }

        private boolean isItemDeletable() {
            return mDashboardItem.getAccess().isDelete();
        }

        @Override
        public void onClick(View view) {
            PopupMenu popupMenu = new PopupMenu(mContext, view);

            if (isItemShareable()) {
                popupMenu.getMenu().add(MENU_GROUP_ID,
                        MENU_SHARE_ITEM_ID, MENU_SHARE_ITEM_ORDER,
                        R.string.share_interpretation);
            }

            if (isItemDeletable()) {
                popupMenu.getMenu().add(MENU_GROUP_ID,
                        MENU_DELETE_ITEM_ID, MENU_DELETE_ITEM_ORDER,
                        R.string.delete);
            }

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    if (mListener == null) {
                        return false;
                    }

                    if (menuItem.getItemId() == MENU_SHARE_ITEM_ID) {
                        mListener.onItemShareInterpretation(mDashboardItem);
                    } else if (menuItem.getItemId() == MENU_DELETE_ITEM_ID) {
                        mListener.onItemDelete(mDashboardItem);
                    }

                    return true;
                }
            });

            popupMenu.show();
        }
    }

    private String buildImageUrl(String resource, String id) {
        return DhisManager.getInstance().getServerUrl().newBuilder()
                .addEncodedPathSegment(resource).addEncodedPathSegment(id).addEncodedPathSegment("data.png")
                .addEncodedQueryParameter("width", "480").addEncodedQueryParameter("height", "320")
                .toString();
    }

    private void handleItemsWithImages(String name, String request,
                                       DashboardViewHolder holder) {
        holder.itemName.setVisibility(View.VISIBLE);
        holder.itemImage.setVisibility(View.VISIBLE);
        holder.itemText.setVisibility(View.GONE);

        holder.itemName.setText(name);
        mImageLoader.load(request)
                .placeholder(R.mipmap.stub_dashboard_background)
                .into(holder.itemImage);
    }

    private void handleItemsWithoutImages(String text, DashboardViewHolder holder) {
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

    static class DashboardViewHolder extends RecyclerView.ViewHolder {
        final View itemBody;
        final TextView itemName;
        final ImageView itemImage;
        final TextView itemText;
        final TextView lastUpdated;
        final ImageView itemMenuButton;
        final MenuButtonHandler menuButtonHandler;
        final OnItemBodyClickListener onItemBodyClickListener;

        DashboardViewHolder(View view, Access dashboardAccess,
                            OnItemClickListener listener) {
            super(view);

            itemBody = view
                    .findViewById(R.id.dashboard_item_body_container);
            itemName = (TextView) view
                    .findViewById(R.id.dashboard_item_name);
            itemImage = (ImageView) view
                    .findViewById(R.id.dashboard_item_image);
            itemText = (TextView) view
                    .findViewById(R.id.dashboard_item_text);
            lastUpdated = (TextView) view
                    .findViewById(R.id.dashboard_item_last_updated);
            itemMenuButton = (ImageView) view
                    .findViewById(R.id.dashboard_item_menu);

            onItemBodyClickListener = new OnItemBodyClickListener(listener);
            itemBody.setOnClickListener(onItemBodyClickListener);

            // Overflow menu button click listener
            menuButtonHandler = new MenuButtonHandler(
                    view.getContext(), dashboardAccess, listener);
            itemMenuButton.setOnClickListener(menuButtonHandler);
        }
    }
}