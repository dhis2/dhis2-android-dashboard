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
import org.dhis2.android.dashboard.api.models.DashboardItem;
import org.dhis2.android.dashboard.api.utils.PicassoProvider;


public class DashboardItemAdapter extends AbsAdapter<DashboardItem, DashboardItemAdapter.DashboardViewHolder> {
    private static final String DATE_FORMAT = "YYYY-MM-dd";

    private static final int MENU_GROUP_ID = 9382352;
    private static final int MENU_SHARE_ITEM_ID = 8936352;
    private static final int MENU_DELETE_ITEM_ID = 149232;
    private static final int MENU_SHARE_ITEM_ORDER = 100;
    private static final int MENU_DELETE_ITEM_ORDER = 110;

    private Access mDashboardAccess;
    private Picasso mImageLoader;

    private OnItemClickListener mClickListener;

    public DashboardItemAdapter(Context context) {
        super(context, LayoutInflater.from(context));
        mImageLoader = PicassoProvider.getInstance(context);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    public void setDashboardAccess(Access access) {
        mDashboardAccess = access;
    }

    @Override
    public DashboardViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        View view = getLayoutInflater().inflate(
                R.layout.gridview_dashboard_item, parent, false);

        ImageView itemViewButton = (ImageView) view
                .findViewById(R.id.dashboard_item_menu);
        PopupMenu menu = new PopupMenu(getContext(), itemViewButton);
        menu.getMenu().add(MENU_GROUP_ID, MENU_SHARE_ITEM_ID,
                MENU_SHARE_ITEM_ORDER, R.string.share_interpretation);
        menu.getMenu().add(MENU_GROUP_ID, MENU_DELETE_ITEM_ID,
                MENU_DELETE_ITEM_ORDER, R.string.delete);

        return new DashboardViewHolder(
                view, itemViewButton, menu
        );
    }

    @Override
    public void onBindViewHolder(DashboardViewHolder holder, int position) {
        long start = System.currentTimeMillis();
        handleDashboardItems((getItem(position)), holder);
        System.out.println("GET_VIEW: " + (System.currentTimeMillis() - start));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // TODO Try to create only one Click Listener for each type of view and keep it inside of view holder
    public void handleDashboardItems(final DashboardItem item, final DashboardViewHolder holder) {
        boolean isItemShareable = false;
        boolean isDashboardManageable = mDashboardAccess.isManage();
        boolean isItemDeletable = item.getAccess().isDelete();

        holder.lastUpdated.setText(item.getLastUpdated().toString(DATE_FORMAT));
        if (DashboardItem.TYPE_CHART.equals(item.getType()) && item.getChart() != null) {
            String request = buildImageUrl("api/charts/", item.getChart().getId());
            handleItemsWithImages(item.getChart().getName(), request, holder);
            isItemShareable = true;
        } else if (DashboardItem.TYPE_MAP.equals(item.getType()) && item.getMap() != null) {
            String request = buildImageUrl("api/maps/", item.getMap().getId());
            handleItemsWithImages(item.getMap().getName(), request, holder);
            isItemShareable = true;
        } else if (DashboardItem.TYPE_EVENT_CHART.equals(item.getType()) && item.getEventChart() != null) {
            String request = buildImageUrl("api/eventCharts/", item.getEventChart().getId());
            handleItemsWithImages(item.getEventChart().getName(), request, holder);
            isItemShareable = false;
        } else if (DashboardItem.TYPE_REPORT_TABLE.equals(item.getType()) && item.getReportTable() != null) {
            handleItemsWithoutImages(item.getReportTable().getName(), holder);
            isItemShareable = true;
        } else if (DashboardItem.TYPE_EVENT_REPORT.equals(item.getType()) && item.getEventReport() != null) {
            handleItemsWithoutImages(item.getEventReport().getName(), holder);
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

    private String buildImageUrl(String resource, String id) {
        return DhisManager.getInstance().getServerUri().buildUpon()
                .appendEncodedPath(resource).appendEncodedPath(id).appendEncodedPath("data.png")
                .appendQueryParameter("width", "480").appendQueryParameter("height", "320")
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
        final PopupMenu popupMenu;

        DashboardViewHolder(View view, ImageView menuButton, PopupMenu menu) {
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
            itemMenuButton = menuButton;
            popupMenu = menu;
        }
    }
}