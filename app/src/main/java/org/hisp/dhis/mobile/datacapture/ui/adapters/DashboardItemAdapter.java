package org.hisp.dhis.mobile.datacapture.ui.adapters;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.hisp.dhis.mobile.datacapture.R;
import org.hisp.dhis.mobile.datacapture.api.android.models.DbRow;
import org.hisp.dhis.mobile.datacapture.api.managers.DHISManager;
import org.hisp.dhis.mobile.datacapture.api.models.Access;
import org.hisp.dhis.mobile.datacapture.api.models.DashboardItem;
import org.hisp.dhis.mobile.datacapture.utils.DateTimeTypeAdapter;
import org.hisp.dhis.mobile.datacapture.utils.PicassoProvider;
import org.joda.time.DateTime;

// TODO find bottleneck in performance of this adapter
public class DashboardItemAdapter extends DBBaseAdapter<DashboardItem> {
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
        super(context);

        mImageLoader = PicassoProvider.getInstance(context);
        mImageTransformation = new ImgTransformation();
        mServerUrl = DHISManager.getInstance().getServerUrl();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    public void setDashboardAccess(Access access) {
        mDashboardAccess = access;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;

        if (convertView == null) {
            view = getInflater().inflate(R.layout.gridview_dashboard_item_layout, parent, false);

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
                    (ImageButton) itemViewButton, menu
            );

            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        DbRow<DashboardItem> dbItem = ((DbRow<DashboardItem>) getItem(position));
        handleDashboardItems(dbItem, holder);
        return view;
    }

    // TODO Try to create only one Click Listener for each type of view and keep it inside of view holder
    public void handleDashboardItems(final DbRow<DashboardItem> dbItem,
                                     final ViewHolder holder) {
        if (dbItem == null || dbItem.getItem() == null) {
            return;
        }

        DashboardItem item = dbItem.getItem();
        boolean isItemShareable = false;
        boolean isDashboardManageable = false;
        boolean isItemDeletable = false;

        String lastUpdated = "";
        if (item.getLastUpdated() != null) {
            DateTime dateTime = DateTimeTypeAdapter.deserializeDateTime(item.getLastUpdated());
            lastUpdated = dateTime.toString(DATE_FORMAT);
        }

        holder.lastUpdated.setText(lastUpdated);
        if (DashboardItem.TYPE_CHART.equals(item.getType()) && item.getChart() != null) {
            String request = mServerUrl + "/api/charts/" + item.getChart().getId() + "/data.png";
            handleItemsWithImages(item.getChart().getName(), request, holder);
            isItemShareable = true;
        } else if (DashboardItem.TYPE_MAP.equals(item.getType()) && item.getMap() != null) {
            String request = mServerUrl + "/api/maps/" + item.getMap().getId() + "/data.png";
            handleItemsWithImages(item.getMap().getName(), request, holder);
            isItemShareable = true;
        } else if (DashboardItem.TYPE_EVENT_CHART.equals(item.getType()) && item.getEventChart() != null) {
            String request = mServerUrl + "/api/eventCharts/" + item.getEventChart().getId() + "/data.png";
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
        isItemDeletable = dbItem.getItem().getAccess().isDelete();

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
                    mClickListener.onItemClick(dbItem);
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
                    mClickListener.onItemShareInterpretation(dbItem);
                } else if (menuItem.getItemId() == MENU_DELETE_ITEM_ID) {
                    mClickListener.onItemDelete(dbItem);
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
                .transform(mImageTransformation)
                .placeholder(R.drawable.stub_dashboard_background)
                .into(holder.itemImage);
    }

    private void handleItemsWithoutImages(String text, ViewHolder holder) {
        holder.itemImage.setVisibility(View.GONE);
        holder.itemName.setVisibility(View.GONE);
        holder.itemText.setVisibility(View.VISIBLE);

        holder.itemText.setText(text);
    }

    public interface OnItemClickListener {
        public void onItemClick(DbRow<DashboardItem> dbItem);
        public void onItemShareInterpretation(DbRow<DashboardItem> dbItem);
        public void onItemDelete(DbRow<DashboardItem> dbItem);
    }

    private static class ViewHolder {
        final View itemBody;
        final TextView itemName;
        final TextView itemText;
        final TextView lastUpdated;
        final ImageView itemImage;
        final ImageButton itemMenuButton;
        final PopupMenu popupMenu;

        ViewHolder(View itemBody,
                   TextView itemName,
                   ImageView itemImage,
                   TextView itemText,
                   TextView lastUpdated,
                   ImageButton itemMenuButton,
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
}
