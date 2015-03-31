package org.hisp.dhis.mobile.datacapture.ui.adapters;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.hisp.dhis.mobile.datacapture.R;
import org.hisp.dhis.mobile.datacapture.api.android.models.DbRow;
import org.hisp.dhis.mobile.datacapture.api.managers.DHISManager;
import org.hisp.dhis.mobile.datacapture.api.models.Access;
import org.hisp.dhis.mobile.datacapture.api.models.Interpretation;
import org.hisp.dhis.mobile.datacapture.utils.PicassoProvider;
import org.joda.time.DateTime;

public class InterpretationAdapter extends DBBaseAdapter<Interpretation> {
    private static final String DATE_FORMAT = "YYYY-MM-dd";

    private static final int MENU_GROUP_ID = 923659018;

    private static final int MENU_ITEM_EDIT_ID = 860237593;
    private static final int MENU_ITEM_EDIT_ORDER = 100;

    private static final int MENU_ITEM_DELETE_ID = 947523854;
    private static final int MENU_ITEM_DELETE_ORDER = 101;

    private Picasso mImageLoader;
    private ImgTransformation mImageTransformation;
    private String mServerUrl;
    private OnItemClickListener mCallback;

    public InterpretationAdapter(Context context) {
        super(context);

        mImageLoader = PicassoProvider.getInstance(context);
        mImageTransformation = new ImgTransformation();
        mServerUrl = DHISManager.getInstance().getServerUrl();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View view;

        if (convertView == null) {
            view = getInflater().inflate(R.layout.gridview_interpretation_layout, parent, false);
            View popupMenuButton = view.findViewById(R.id.interpretation_item_menu);
            PopupMenu menu = new PopupMenu(getContext(), popupMenuButton);
            menu.getMenu().add(MENU_GROUP_ID, MENU_ITEM_EDIT_ID, MENU_ITEM_EDIT_ORDER, R.string.edit);
            menu.getMenu().add(MENU_GROUP_ID, MENU_ITEM_DELETE_ID, MENU_ITEM_DELETE_ORDER, R.string.delete);

            holder = new ViewHolder(
                    (TextView) view.findViewById(R.id.interpretation_user),
                    (TextView) view.findViewById(R.id.interpretation_last_updated),
                    (TextView) view.findViewById(R.id.interpretation_text),
                    (TextView) view.findViewById(R.id.interpretation_name),
                    (ImageView) view.findViewById(R.id.interpretation_dashboard_element),
                    view.findViewById(R.id.interpretation_comments_button),
                    (TextView) view.findViewById(R.id.interpretation_comments_count),
                    view.findViewById(R.id.interpretation_body_container),
                    popupMenuButton, menu
            );
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        handleInterpretation(holder, (DbRow<Interpretation>) getItem(position));
        return view;
    }

    public void setCallback(OnItemClickListener callback) {
        mCallback = callback;
    }

    private void handleInterpretation(final ViewHolder holder,
                                      final DbRow<Interpretation> dbItem) {
        Interpretation interpretation = dbItem.getItem();
        if (interpretation == null || interpretation.getType() == null) {
            return;
        }

        if (interpretation.getUser() != null &&
                interpretation.getUser().getName() != null) {
            holder.user.setText(interpretation.getUser().getName());
        }

        if (interpretation.getLastUpdated() != null) {
            DateTime dateTime = DateTime.parse(interpretation.getLastUpdated());
            holder.lastUpdated.setText(dateTime.toString(DATE_FORMAT));
        }

        if (interpretation.getText() != null) {
            holder.text.setText(interpretation.getText());
        }

        int commentCount = 0;
        if (interpretation.getComments() != null) {
            commentCount = interpretation.getComments().size();
        }

        holder.itemMenu.setVisibility(View.GONE);
        if (interpretation.getAccess() != null) {
            Access access = interpretation.getAccess();
            if (access.isDelete() || access.isUpdate()) {
                System.out.println("Interpretation ID: " + dbItem.getItem().getId());
                System.out.println("Interpretation ID: " + dbItem.getItem().getUser().getName());
                holder.itemMenu.setVisibility(View.VISIBLE);
                holder.popupMenu.getMenu().findItem(MENU_ITEM_EDIT_ID)
                        .setVisible(access.isDelete());
                holder.popupMenu.getMenu().findItem(MENU_ITEM_DELETE_ID)
                        .setVisible(access.isUpdate());
            }
        }

        holder.commentsCount.setText(String.valueOf(commentCount));
        if (Interpretation.TYPE_CHART.equals(interpretation.getType()) &&
                interpretation.getChart() != null) {
            final String REQUEST = mServerUrl + "/api/charts/" +
                    interpretation.getChart().getId() + "/data.png";
            handleChartsAndMaps(REQUEST, holder);
        } else if (Interpretation.TYPE_MAP.equals(interpretation.getType()) &&
                interpretation.getMap() != null) {
            final String REQUEST = mServerUrl + "/api/maps/" +
                    interpretation.getMap().getId() + "/data.png";
            handleChartsAndMaps(REQUEST, holder);
        } else if (Interpretation.TYPE_REPORT_TABLE.equals(interpretation.getType()) &&
                interpretation.getReportTable() != null) {
            handleTablesAndDatasets(interpretation.getReportTable().getDisplayName(), holder);
        } else if (Interpretation.TYPE_DATASET_REPORT.equals(interpretation.getType()) &&
                interpretation.getDataSet() != null) {
            handleTablesAndDatasets(interpretation.getDataSet().getDisplayName(), holder);
        }

        holder.body.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onItemClicked(dbItem);
                }
            }
        });

        holder.commentsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onShowCommentsDialog(dbItem);
                }
            }
        });

        holder.itemMenu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (holder.popupMenu != null) {
                    holder.popupMenu.show();
                }
            }
        });

        holder.popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (mCallback == null) {
                    return false;
                }

                if (menuItem.getItemId() == MENU_ITEM_EDIT_ID) {
                    mCallback.onEditInterpretation(dbItem);
                    return true;
                } else if (menuItem.getItemId() == MENU_ITEM_DELETE_ID) {
                    mCallback.onDeleteInterpretation(dbItem);
                    return true;
                }
                return false;
            }
        });
    }

    private void handleChartsAndMaps(String request, ViewHolder holder) {
        holder.name.setVisibility(View.GONE);
        holder.element.setVisibility(View.VISIBLE);

        mImageLoader.load(request)
                .transform(mImageTransformation)
                .placeholder(R.drawable.stub_dashboard_background)
                .into(holder.element);
    }

    private void handleTablesAndDatasets(String name, ViewHolder holder) {
        holder.element.setVisibility(View.GONE);
        holder.name.setVisibility(View.VISIBLE);
        holder.name.setText(name);
    }

    public interface OnItemClickListener {
        public void onItemClicked(DbRow<Interpretation> interpretation);
        public void onEditInterpretation(DbRow<Interpretation> interpretation);
        public void onDeleteInterpretation(DbRow<Interpretation> interpretation);
        public void onShowCommentsDialog(DbRow<Interpretation> interpretation);
    }

    private static class ViewHolder {
        final TextView user;
        final TextView lastUpdated;
        final TextView text;
        final TextView name;
        final ImageView element;
        final View commentsButton;
        final TextView commentsCount;
        final View body;
        final View itemMenu;
        final PopupMenu popupMenu;

        private ViewHolder(TextView user, TextView lastUpdated,
                           TextView text, TextView name,
                           ImageView element, View commentsButton,
                           TextView commentsCount,
                           View body, View itemMenu, PopupMenu popupMenu) {
            this.user = user;
            this.lastUpdated = lastUpdated;
            this.text = text;
            this.name = name;
            this.element = element;
            this.commentsButton = commentsButton;
            this.commentsCount = commentsCount;
            this.body = body;
            this.itemMenu = itemMenu;
            this.popupMenu = popupMenu;
        }
    }
}
