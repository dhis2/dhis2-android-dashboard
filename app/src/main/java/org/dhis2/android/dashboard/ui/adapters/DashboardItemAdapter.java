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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.dhis2.android.dashboard.R;
import org.dhis2.android.dashboard.api.DhisManager;
import org.dhis2.android.dashboard.api.models.Access;
import org.dhis2.android.dashboard.api.models.DashboardElement;
import org.dhis2.android.dashboard.api.models.DashboardItem;
import org.dhis2.android.dashboard.api.models.DashboardItemContent;
import org.dhis2.android.dashboard.api.utils.PicassoProvider;

import java.util.List;

public class DashboardItemAdapter extends AbsAdapter<DashboardItem, DashboardItemAdapter.ItemViewHolder> {
    private static final String DATE_FORMAT = "YYYY-MM-dd";
    private static final String EMPTY_FIELD = "";

    private static final int ITEM_WITH_IMAGE_TYPE = 0;
    private static final int ITEM_WITH_TABLE_TYPE = 1;
    private static final int ITEM_WITH_LIST_TYPE = 2;

    private final Access mDashboardAccess;
    private final OnItemClickListener mClickListener;
    private final int mMaxSpanCount;

    private final String mUsersName;
    private final String mReportsName;
    private final String mResourcesName;

    private final Picasso mImageLoader;

    public DashboardItemAdapter(Context context, Access dashboardAccess,
                                int maxSpanCount, OnItemClickListener clickListener) {
        super(context, LayoutInflater.from(context));

        mDashboardAccess = dashboardAccess;
        mMaxSpanCount = maxSpanCount;
        mClickListener = clickListener;

        mUsersName = context.getString(R.string.users);
        mReportsName = context.getString(R.string.reports);
        mResourcesName = context.getString(R.string.resources);

        mImageLoader = PicassoProvider.getInstance(context);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = getLayoutInflater().inflate(
                R.layout.gridview_dashboard_item, parent, false);

        LinearLayout itemBody = (LinearLayout) rootView
                .findViewById(R.id.dashboard_item_body_container);
        TextView itemName = (TextView) rootView
                .findViewById(R.id.dashboard_item_name);
        TextView lastUpdated = (TextView) rootView
                .findViewById(R.id.dashboard_item_last_updated);
        ImageView itemMenuButton = (ImageView) rootView
                .findViewById(R.id.dashboard_item_menu);
        IElementContentViewHolder elementContentViewHolder
                = onCreateElementContentViewHolder(itemBody, viewType);
            /* attaching custom view */
        itemBody.addView(elementContentViewHolder.getView());

        // Overflow menu button click listener
        MenuButtonHandler menuButtonHandler = new MenuButtonHandler(
                rootView.getContext(), mDashboardAccess, mClickListener);
        itemMenuButton.setOnClickListener(menuButtonHandler);

        return new ItemViewHolder(
                rootView, itemBody, itemName, lastUpdated,
                itemMenuButton, menuButtonHandler, elementContentViewHolder
        );
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        DashboardItem item = getItem(holder.getAdapterPosition());

        holder.menuButtonHandler.setDashboardItem(item);
        holder.lastUpdated.setText(item.getLastUpdated().toString(DATE_FORMAT));

        if (DashboardItemContent.TYPE_CHART.equals(item.getType()) && item.getChart() != null) {
            holder.itemName.setText(item.getChart().getDisplayName());
        } else if (DashboardItemContent.TYPE_MAP.equals(item.getType()) && item.getMap() != null) {
            holder.itemName.setText(item.getMap().getDisplayName());
        } else if (DashboardItemContent.TYPE_EVENT_CHART.equals(item.getType()) && item.getEventChart() != null) {
            holder.itemName.setText(item.getEventChart().getDisplayName());
        } else if (DashboardItemContent.TYPE_REPORT_TABLE.equals(item.getType()) && item.getReportTable() != null) {
            holder.itemName.setText(item.getReportTable().getDisplayName());
        } else if (DashboardItemContent.TYPE_EVENT_REPORT.equals(item.getType()) && item.getEventReport() != null) {
            holder.itemName.setText(item.getEventReport().getDisplayName());
        } else if (DashboardItemContent.TYPE_USERS.equals(item.getType())) {
            holder.itemName.setText(mUsersName);
        } else if (DashboardItemContent.TYPE_REPORTS.equals(item.getType())) {
            holder.itemName.setText(mReportsName);
        } else if (DashboardItemContent.TYPE_RESOURCES.equals(item.getType())) {
            holder.itemName.setText(mResourcesName);
        }

        if (holder.menuButtonHandler.isMenuVisible()) {
            holder.itemMenuButton.setVisibility(View.VISIBLE);
        } else {
            holder.itemMenuButton.setVisibility(View.GONE);
        }

        onBindElementContentViewHolder(holder.contentViewHolder,
                holder.getItemViewType(), holder.getAdapterPosition());
    }

    public IElementContentViewHolder onCreateElementContentViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_WITH_IMAGE_TYPE: {
                ImageView imageView = (ImageView) getLayoutInflater()
                        .inflate(R.layout.gridview_dashboard_item_imageview, parent, false);
                return new ImageItemViewHolder(imageView);
            }
            case ITEM_WITH_TABLE_TYPE: {
                TextView textView = (TextView) getLayoutInflater()
                        .inflate(R.layout.gridview_dashboard_item_textview, parent, false);
                return new TextItemViewHolder(textView);
            }
            case ITEM_WITH_LIST_TYPE: {
                LinearLayout textViewContainer = (LinearLayout) getLayoutInflater()
                        .inflate(R.layout.gridview_dashboard_item_list, parent, false);
                return new ListItemViewHolder(textViewContainer);
            }
        }
        return null;
    }

    public void onBindElementContentViewHolder(IElementContentViewHolder holder, int viewType, int position) {
        switch (viewType) {
            case ITEM_WITH_IMAGE_TYPE: {
                handleItemsWithImages((ImageItemViewHolder) holder, getItem(position));
                break;
            }
            case ITEM_WITH_TABLE_TYPE: {
                handleItemsWithTables((TextItemViewHolder) holder, getItem(position));
                break;
            }
            case ITEM_WITH_LIST_TYPE: {
                handleItemsWithLists((ListItemViewHolder) holder, getItem(position));
                break;
            }
        }
    }

    private void handleItemsWithImages(ImageItemViewHolder holder, DashboardItem item) {
        String request = null;
        if (DashboardItemContent.TYPE_CHART.equals(item.getType()) && item.getChart() != null) {
            request = buildImageUrl("charts", item.getChart().getUId());
        } else if (DashboardItemContent.TYPE_MAP.equals(item.getType()) && item.getMap() != null) {
            request = buildImageUrl("maps", item.getMap().getUId());
        } else if (DashboardItemContent.TYPE_EVENT_CHART.equals(item.getType()) && item.getEventChart() != null) {
            request = buildImageUrl("eventCharts", item.getEventChart().getUId());
        }

        mImageLoader.load(request)
                .placeholder(R.mipmap.ic_stub_dashboard_item)
                .into(holder.imageView);
    }

    private static String buildImageUrl(String resource, String id) {
        return DhisManager.getInstance().getServerUrl().newBuilder()
                .addPathSegment("api").addPathSegment(resource).addPathSegment(id).addPathSegment("data.png")
                .addQueryParameter("width", "480").addQueryParameter("height", "320")
                .toString();
    }

    private void handleItemsWithTables(TextItemViewHolder holder, DashboardItem item) {
        if (DashboardItemContent.TYPE_REPORT_TABLE.equals(item.getType()) && item.getReportTable() != null) {
            holder.textView.setText(item.getReportTable().getDisplayName());
        } else if (DashboardItemContent.TYPE_EVENT_REPORT.equals(item.getType()) && item.getEventReport() != null) {
            holder.textView.setText(item.getEventReport().getDisplayName());
        }
    }

    private void handleItemsWithLists(ListItemViewHolder holder, DashboardItem item) {
        List<DashboardElement> elementList = null;
        if (DashboardItemContent.TYPE_USERS.equals(item.getType())) {
            elementList = item.getUsers();
        } else if (DashboardItemContent.TYPE_REPORTS.equals(item.getType())) {
            elementList = item.getReports();
        } else if (DashboardItemContent.TYPE_RESOURCES.equals(item.getType())) {
            elementList = item.getResources();
        }

        holder.itemElement0.setText(getElementFromListSafely(elementList, 0));
        holder.itemElement1.setText(getElementFromListSafely(elementList, 1));
        holder.itemElement2.setText(getElementFromListSafely(elementList, 2));
        holder.itemElement3.setText(getElementFromListSafely(elementList, 3));
        holder.itemElement4.setText(getElementFromListSafely(elementList, 4));
        holder.itemElement5.setText(getElementFromListSafely(elementList, 5));
        holder.itemElement6.setText(getElementFromListSafely(elementList, 6));
        holder.itemElement7.setText(getElementFromListSafely(elementList, 7));
    }

    private static String getElementFromListSafely(List<DashboardElement> elements, int position) {
        if (elements != null && elements.size() > position) {
            DashboardElement element = elements.get(position);

            if (element != null) {
                return element.getDisplayName();
            }
        }

        return EMPTY_FIELD;
    }

    @Override
    public int getItemViewType(int position) {

        switch (getItem(position).getType()) {
            case DashboardItemContent.TYPE_CHART:
            case DashboardItemContent.TYPE_EVENT_CHART:
            case DashboardItemContent.TYPE_MAP:
                return ITEM_WITH_IMAGE_TYPE;
            case DashboardItemContent.TYPE_REPORT_TABLE:
            case DashboardItemContent.TYPE_EVENT_REPORT:
                return ITEM_WITH_TABLE_TYPE;
            case DashboardItemContent.TYPE_USERS:
            case DashboardItemContent.TYPE_REPORTS:
            case DashboardItemContent.TYPE_RESOURCES:
                return ITEM_WITH_LIST_TYPE;
        }

        throw new IllegalArgumentException();
    }

    public final int getSpanSize(int position) {
        if (getItemCount() > position) {
            DashboardItem dashboardItem = getItem(position);

            String itemShape = dashboardItem.getShape();
            if (itemShape == null) {
                itemShape = DashboardItem.SHAPE_NORMAL;
            }

            switch (itemShape) {
                case DashboardItem.SHAPE_NORMAL: {
                    return getSpanSizeNormal();
                }
                case DashboardItem.SHAPE_FULL_WIDTH: {
                    return getSpanSizeFull();
                }
                case DashboardItem.SHAPE_DOUBLE_WIDTH: {
                    return getSpanSizeDouble();
                }
            }
        }

        return 1;
    }

    private int getSpanSizeNormal() {
        return 1;
    }

    private int getSpanSizeDouble() {
        switch (mMaxSpanCount) {
            case 1:
                return 1;
            case 2:
                return 1;
            case 3:
                return 2;
            case 4:
                return 2;
            default:
                return 1;
        }
    }

    private int getSpanSizeFull() {
        return mMaxSpanCount;
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

    interface IElementContentViewHolder {
        View getView();
    }

    public interface OnItemClickListener {
        void onItemClick(DashboardItem item);

        void onItemElementClick(DashboardElement element);

        void onItemShareInterpretation(DashboardItem item);

        void onItemDelete(DashboardItem item);
    }

    static class ImageItemViewHolder implements IElementContentViewHolder {
        final ImageView imageView;

        public ImageItemViewHolder(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        public View getView() {
            return imageView;
        }
    }

    static class TextItemViewHolder implements IElementContentViewHolder {
        final TextView textView;

        public TextItemViewHolder(TextView textView) {
            this.textView = textView;
        }

        @Override
        public View getView() {
            return textView;
        }
    }

    static class ListItemViewHolder implements IElementContentViewHolder {
        final View itemElementsContainer;
        final TextView itemElement0;
        final TextView itemElement1;
        final TextView itemElement2;
        final TextView itemElement3;
        final TextView itemElement4;
        final TextView itemElement5;
        final TextView itemElement6;
        final TextView itemElement7;

        public ListItemViewHolder(View view) {
            itemElementsContainer = view;

            itemElement0 = (TextView) view.findViewById(R.id.element_item_0);
            itemElement1 = (TextView) view.findViewById(R.id.element_item_1);
            itemElement2 = (TextView) view.findViewById(R.id.element_item_2);
            itemElement3 = (TextView) view.findViewById(R.id.element_item_3);
            itemElement4 = (TextView) view.findViewById(R.id.element_item_4);
            itemElement5 = (TextView) view.findViewById(R.id.element_item_5);
            itemElement6 = (TextView) view.findViewById(R.id.element_item_6);
            itemElement7 = (TextView) view.findViewById(R.id.element_item_7);
        }

        @Override
        public View getView() {
            return itemElementsContainer;
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        final View itemBody;
        final TextView itemName;
        final TextView lastUpdated;
        final ImageView itemMenuButton;
        final MenuButtonHandler menuButtonHandler;
        final IElementContentViewHolder contentViewHolder;

        public ItemViewHolder(View itemView, View itemBody,
                              TextView itemName, TextView lastUpdated,
                              ImageView itemMenuButton, MenuButtonHandler menuButtonHandler,
                              IElementContentViewHolder elementContentViewHolder) {
            super(itemView);
            this.itemBody = itemBody;
            this.itemName = itemName;
            this.lastUpdated = lastUpdated;
            this.itemMenuButton = itemMenuButton;
            this.menuButtonHandler = menuButtonHandler;
            this.contentViewHolder = elementContentViewHolder;
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
                    DashboardItemContent.TYPE_CHART.equals(mDashboardItem.getType()) ||
                            DashboardItemContent.TYPE_MAP.equals(mDashboardItem.getType()) ||
                            DashboardItemContent.TYPE_REPORT_TABLE.equals(mDashboardItem.getType())
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

    private static class OnItemBodyClickListener implements View.OnClickListener {
        private final OnItemClickListener mListener;
        private DashboardItem mDashboardItem;

        public OnItemBodyClickListener(OnItemClickListener listener) {
            mListener = listener;
        }

        public void setDashboardItem(DashboardItem dashboardItem) {
            mDashboardItem = dashboardItem;
        }

        @Override
        public void onClick(View view) {
            mListener.onItemClick(mDashboardItem);
        }
    }
}