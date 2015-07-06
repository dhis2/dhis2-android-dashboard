/*
 * Copyright (c) 2015, dhis2
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.dhis2.android.dashboard.ui.adapters;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.dhis2.android.dashboard.R;
import org.dhis2.android.dashboard.api.DhisManager;
import org.dhis2.android.dashboard.api.models.Interpretation;
import org.dhis2.android.dashboard.api.models.InterpretationElement;
import org.dhis2.android.dashboard.api.utils.PicassoProvider;
import org.dhis2.android.dashboard.ui.adapters.InterpretationAdapter.InterpretationHolder;

/**
 * @author Araz Abishov <araz.abishov.gsoc@gmail.com>.
 */
public final class InterpretationAdapter extends AbsAdapter<Interpretation, InterpretationHolder> {
    private static final String DATE_FORMAT = "MMMM dd, YYYY";
    private static final String EMPTY_FIELD = "";

    private static final int ITEM_WITH_IMAGE_TYPE = 0;
    private static final int ITEM_WITH_TABLE_TYPE = 1;

    /**
     * Callback which reacts to user actions on each interpretation.
     */
    private final OnItemClickListener mClickListener;

    /**
     * Image loading utility.
     */
    private final Picasso mImageLoader;

    public InterpretationAdapter(Context context, LayoutInflater inflater,
                                 OnItemClickListener clickListener) {
        super(context, inflater);

        mClickListener = clickListener;
        mImageLoader = PicassoProvider.getInstance(context);
    }

    private static String buildImageUrl(String resource, String id) {
        return DhisManager.getInstance().getServerUrl().newBuilder()
                .addPathSegment("api").addPathSegment(resource).addPathSegment(id).addPathSegment("data.png")
                .addQueryParameter("width", "480").addQueryParameter("height", "320")
                .toString();
    }

    /* returns type of row depending on item content type. */
    @Override
    public int getItemViewType(int position) {

        switch (getItem(position).getType()) {
            case Interpretation.TYPE_CHART:
            case Interpretation.TYPE_MAP:
                return ITEM_WITH_IMAGE_TYPE;
            case Interpretation.TYPE_REPORT_TABLE:
            case Interpretation.TYPE_DATA_SET_REPORT:
                return ITEM_WITH_TABLE_TYPE;
        }

        throw new IllegalArgumentException();
    }

    @Override
    public InterpretationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = getLayoutInflater().inflate(
                R.layout.recycler_view_interpretation_item, parent, false);

        View itemBody = rootView
                .findViewById(R.id.interpretation_body_container);
        TextView userTextView = (TextView) rootView
                .findViewById(R.id.interpretation_user);
        TextView lastUpdated = (TextView) rootView
                .findViewById(R.id.interpretation_created);
        ImageView menuButton = (ImageView) rootView
                .findViewById(R.id.interpretation_menu);
        TextView interpretationTextView = (TextView) rootView
                .findViewById(R.id.interpretation_text);
        ViewGroup interpretationContent = (FrameLayout) rootView
                .findViewById(R.id.interpretation_content);
        View commentsShowButton = rootView.
                findViewById(R.id.interpretation_comments_show);
        TextView commentsCountTextView = (TextView) rootView
                .findViewById(R.id.interpretation_comments_count);
        IInterpretationViewHolder viewHolder =
                onCreateContentViewHolder(interpretationContent, viewType);
        interpretationContent.addView(viewHolder.getView());

        MenuButtonHandler handler = new MenuButtonHandler(getContext(), mClickListener);
        menuButton.setOnClickListener(handler);

        OnInterpretationInternalClickListener listener
                = new OnInterpretationInternalClickListener(mClickListener);
        commentsShowButton.setOnClickListener(listener);
        interpretationTextView.setOnClickListener(listener);

        return new InterpretationHolder(
                rootView, itemBody, userTextView, lastUpdated,
                menuButton, interpretationTextView, viewHolder,
                commentsShowButton, commentsCountTextView, handler, listener
        );
    }

    @Override
    public void onBindViewHolder(InterpretationHolder holder, int position) {
        Interpretation interpretation = getItem(holder.getAdapterPosition());

        // handling menu visibility
        holder.menuButtonHandler.setInterpretation(interpretation);
        holder.menuButton.setVisibility(holder.menuButtonHandler
                .isMenuVisible() ? View.VISIBLE : View.GONE);

        holder.listener.setInterpretation(interpretation);

        holder.userTextView.setText(interpretation.getUser() == null
                ? EMPTY_FIELD : interpretation.getUser().getDisplayName());
        holder.createdTextView.setText(interpretation.getCreated() == null
                ? EMPTY_FIELD : interpretation.getCreated().toString(DATE_FORMAT));
        holder.interpretationTextView.setText(interpretation.getText() == null
                ? EMPTY_FIELD : interpretation.getText());

        int commentsCount = interpretation.getComments() == null
                ? 0 : interpretation.getComments().size();
        String commentsCountString = commentsCount > 99
                ? "99+" : commentsCount + "";
        holder.commentsCountTextView.setText(commentsCountString);

        onBindContentViewHolder(holder.contentViewHolder,
                holder.getItemViewType(), holder.getAdapterPosition());
    }

    private IInterpretationViewHolder onCreateContentViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_WITH_IMAGE_TYPE: {
                ImageView imageView = (ImageView) getLayoutInflater()
                        .inflate(R.layout.recycler_view_dashboard_item_imageview, parent, false);
                return new ImageItemViewHolder(imageView, mClickListener);
            }
            case ITEM_WITH_TABLE_TYPE: {
                TextView textView = (TextView) getLayoutInflater()
                        .inflate(R.layout.recycler_view_dashboard_item_textview, parent, false);
                return new TextItemViewHolder(textView, mClickListener);
            }
        }
        return null;
    }

    private void onBindContentViewHolder(IInterpretationViewHolder holder, int viewType, int position) {
        switch (viewType) {
            case ITEM_WITH_IMAGE_TYPE: {
                handleItemsWithImages((ImageItemViewHolder) holder, getItem(position));
                break;
            }
            case ITEM_WITH_TABLE_TYPE: {
                handleItemsWithTables((TextItemViewHolder) holder, getItem(position));
                break;
            }
        }
    }

    /* builds the URL to image data and loads it by means of Picasso. */
    private void handleItemsWithImages(ImageItemViewHolder holder, Interpretation item) {
        String request = null;
        if (Interpretation.TYPE_CHART.equals(item.getType()) && item.getChart() != null) {
            InterpretationElement element = item.getChart();
            request = buildImageUrl("charts", element.getUId());
        } else if (Interpretation.TYPE_MAP.equals(item.getType()) && item.getMap() != null) {
            InterpretationElement element = item.getMap();
            request = buildImageUrl("maps", element.getUId());
        }

        holder.listener.setInterpretation(item);
        mImageLoader.load(request)
                .placeholder(R.mipmap.ic_stub_dashboard_item)
                .into(holder.imageView);
    }

    private void handleItemsWithTables(TextItemViewHolder holder, Interpretation item) {
        InterpretationElement element = null;
        if (Interpretation.TYPE_REPORT_TABLE.equals(item.getType()) && item.getReportTable() != null) {
            element = item.getReportTable();
        } else if (Interpretation.TYPE_DATA_SET_REPORT.equals(item.getType()) && item.getDataSet() != null) {
            element = item.getDataSet();
        }

        String text = EMPTY_FIELD;
        if (element != null) {
            text = element.getDisplayName();
        }

        holder.listener.setInterpretation(item);
        holder.textView.setText(text);
    }

    interface IInterpretationViewHolder {
        View getView();
    }

    static class InterpretationHolder extends RecyclerView.ViewHolder {
        final View itemBodyView;
        final TextView userTextView;
        final TextView createdTextView;
        final ImageView menuButton;
        final TextView interpretationTextView;
        final IInterpretationViewHolder contentViewHolder;
        final View commentsShowButton;
        final TextView commentsCountTextView;
        final MenuButtonHandler menuButtonHandler;
        final OnInterpretationInternalClickListener listener;

        public InterpretationHolder(View itemView, View itemBodyView,
                                    TextView userTextView, TextView createdTextView,
                                    ImageView menuButton, TextView interpretationTextView,
                                    IInterpretationViewHolder contentViewHolder,
                                    View commentsShowButton, TextView commentsCountTextView,
                                    MenuButtonHandler menuButtonHandler,
                                    OnInterpretationInternalClickListener listener) {
            super(itemView);
            this.itemBodyView = itemBodyView;
            this.userTextView = userTextView;
            this.createdTextView = createdTextView;
            this.menuButton = menuButton;
            this.interpretationTextView = interpretationTextView;
            this.contentViewHolder = contentViewHolder;
            this.commentsShowButton = commentsShowButton;
            this.commentsCountTextView = commentsCountTextView;
            this.menuButtonHandler = menuButtonHandler;
            this.listener = listener;
        }
    }

    /* View holder for ImageView */
    static class ImageItemViewHolder implements IInterpretationViewHolder {
        final OnInterpretationInternalClickListener listener;
        final ImageView imageView;

        public ImageItemViewHolder(ImageView view, OnItemClickListener outerListener) {
            imageView = view;

            listener = new OnInterpretationInternalClickListener(outerListener);
            imageView.setOnClickListener(listener);
        }

        @Override
        public View getView() {
            return imageView;
        }
    }

    static class TextItemViewHolder implements IInterpretationViewHolder {
        final OnInterpretationInternalClickListener listener;
        final TextView textView;

        public TextItemViewHolder(TextView view, OnItemClickListener outerListener) {
            textView = view;

            listener = new OnInterpretationInternalClickListener(outerListener);
            textView.setOnClickListener(this.listener);
        }

        @Override
        public View getView() {
            return textView;
        }
    }

    static class OnInterpretationInternalClickListener implements View.OnClickListener {
        final OnItemClickListener mListener;
        Interpretation mInterpretation;

        OnInterpretationInternalClickListener(OnItemClickListener listener) {
            this.mListener = listener;
        }

        public void setInterpretation(Interpretation interpretation) {
            mInterpretation = interpretation;
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.dashboard_item_image: {
                    mListener.onInterpretationContentClick(mInterpretation);
                    break;
                }
                case R.id.dashboard_item_text: {
                    mListener.onInterpretationContentClick(mInterpretation);
                    break;
                }
                case R.id.interpretation_comments_show: {
                    mListener.onInterpretationCommentsClick(mInterpretation);
                    break;
                }
                case R.id.interpretation_text: {
                    mListener.onInterpretationTextClick(mInterpretation);
                    break;
                }
            }
        }
    }

    public interface OnItemClickListener {
        void onInterpretationContentClick(Interpretation interpretation);

        void onInterpretationTextClick(Interpretation interpretation);

        void onInterpretationDeleteClick(Interpretation interpretation);

        void onInterpretationEditClick(Interpretation interpretation);

        void onInterpretationCommentsClick(Interpretation interpretation);
    }

    private static class MenuButtonHandler implements View.OnClickListener {
        /* menu item ids */
        static final int MENU_GROUP_ID = 746523;
        static final int MENU_EDIT_INTERPRETATION_ID = 582345;
        static final int MENU_DELETE_INTERPRETATION_ID = 364587;

        static final int MENU_EDIT_INTERPRETATION_ORDER = 100;
        static final int MENU_DELETE_INTERPRETATION_ORDER = 110;

        final Context mContext;

        final OnItemClickListener mListener;

        /* dashboard item will change on each call to onBindViewHolder() in recycler view */
        Interpretation mInterpretation;

        public MenuButtonHandler(Context context, OnItemClickListener listener) {
            mContext = context;
            mListener = listener;
        }

        public void setInterpretation(Interpretation interpretation) {
            mInterpretation = interpretation;
        }

        /* helper method for client code, which allows to
        determine if we need to show 3-dot button*/
        public boolean isMenuVisible() {
            return isInterpretationEditable() || isInterpretationDeletable();
        }

        /* helper method which returns true if we can show edit menu item */
        private boolean isInterpretationEditable() {
            return mInterpretation.getAccess().isUpdate();
        }

        private boolean isInterpretationDeletable() {
            return mInterpretation.getAccess().isDelete();
        }

        /* here we will build popup menu and show it. */
        @Override
        public void onClick(View view) {
            PopupMenu popupMenu = new PopupMenu(mContext, view);

            if (isInterpretationEditable()) {
                popupMenu.getMenu().add(MENU_GROUP_ID, MENU_EDIT_INTERPRETATION_ID,
                        MENU_EDIT_INTERPRETATION_ORDER, R.string.edit);
            }

            if (isInterpretationDeletable()) {
                popupMenu.getMenu().add(MENU_GROUP_ID, MENU_DELETE_INTERPRETATION_ID,
                        MENU_DELETE_INTERPRETATION_ORDER, R.string.delete);
            }

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    if (mListener == null) {
                        return false;
                    }

                    if (menuItem.getItemId() == MENU_EDIT_INTERPRETATION_ID) {
                        mListener.onInterpretationEditClick(mInterpretation);
                    } else if (menuItem.getItemId() == MENU_DELETE_INTERPRETATION_ID) {
                        mListener.onInterpretationDeleteClick(mInterpretation);
                    }

                    return true;
                }
            });

            popupMenu.show();
        }
    }
}
