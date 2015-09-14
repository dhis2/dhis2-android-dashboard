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

package org.hisp.dhis.android.dashboard.ui.adapters;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.hisp.dhis.android.dashboard.R;
import org.hisp.dhis.android.dashboard.ui.adapters.InterpretationCommentsAdapter.CommentViewHolder;
import org.hisp.dhis.android.sdk.models.interpretation.InterpretationComment;
import org.hisp.dhis.android.sdk.models.user.User;
import org.joda.time.DateTime;

import static android.text.TextUtils.isEmpty;

/**
 * @author Araz Abishov <araz.abishov.gsoc@gmail.com>.
 */
public final class InterpretationCommentsAdapter extends AbsAdapter<InterpretationComment, CommentViewHolder> {
    private static final String DATE_FORMAT = "MMMM dd, YYYY";
    private static final String EMPTY_FIELD = "";

    private final OnCommentClickListener mListener;

    /* As API does not return Access objects for each interpretation comment,
    we can use information about current user as permission. If comment was left by
    current user, it will be editable and deletable.*/
    private final User mUser;

    public InterpretationCommentsAdapter(Context context, LayoutInflater inflater,
                                         OnCommentClickListener listener, User user) {
        super(context, inflater);

        mListener = listener;
        mUser = user;
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CommentViewHolder(
                getLayoutInflater().inflate(
                        R.layout.recycler_view_interpretation_comment, parent, false),
                mListener, mUser
        );
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        InterpretationComment comment = getItem(position);
        DateTime lastUpdated = comment.getLastUpdated();
        User user = comment.getUser();

        String name = EMPTY_FIELD;
        if (user != null) {
            name = isEmpty(user.getDisplayName())
                    ? user.getName() : user.getDisplayName();
        }

        holder.usernameTextView.setText(name);
        holder.commentTextView.setText(comment.getText());
        holder.lastUpdatedTextView.setText(
                lastUpdated == null ? EMPTY_FIELD : lastUpdated.toString(DATE_FORMAT));

        holder.menuButtonHandler
                .setInterpretationComment(comment);
        holder.menuButton.setVisibility(holder.menuButtonHandler
                .isMenuVisible() ? View.VISIBLE : View.INVISIBLE);
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        final TextView usernameTextView;
        final TextView lastUpdatedTextView;
        final TextView commentTextView;
        final View menuButton;
        final MenuButtonHandler menuButtonHandler;

        public CommentViewHolder(View itemView, OnCommentClickListener listener, User user) {
            super(itemView);

            usernameTextView = (TextView) itemView.
                    findViewById(R.id.comment_username);
            lastUpdatedTextView = (TextView) itemView.
                    findViewById(R.id.comment_last_updated);
            commentTextView = (TextView) itemView.
                    findViewById(R.id.comment_text);
            menuButton = itemView.
                    findViewById(R.id.interpretation_comment_menu);
            menuButtonHandler = new MenuButtonHandler(
                    itemView.getContext(), listener, user);

            menuButton.setOnClickListener(menuButtonHandler);
        }
    }

    private static class MenuButtonHandler implements View.OnClickListener {
        /* menu item ids */
        static final int MENU_GROUP_ID = 435212;
        static final int MENU_EDIT_ITEM_ID = 3244356;
        static final int MENU_DELETE_ITEM_ID = 14913232;
        static final int MENU_EDIT_ITEM_ORDER = 100;
        static final int MENU_DELETE_ITEM_ORDER = 110;

        final Context mContext;
        final OnCommentClickListener mListener;
        final User mUser;

        InterpretationComment mComment;

        public MenuButtonHandler(Context context, OnCommentClickListener listener, User user) {
            mContext = context;
            mListener = listener;
            mUser = user;
        }

        public void setInterpretationComment(InterpretationComment comment) {
            mComment = comment;
        }

        /* helper method for client code, which allows to
        determine if we need to show 3-dot button*/
        public boolean isMenuVisible() {
            return isCommentEditable() || isCommentDeletable();
        }

        /* workaround for bug on the server */
        private boolean isCommentEditable() {
            return mComment.getUser() != null
                    && mComment.getUser().getUId().equals(mUser.getUId());
        }

        /* workaround for bug on the server */
        private boolean isCommentDeletable() {
            return mComment.getUser() != null
                    && mComment.getUser().getUId().equals(mUser.getUId());
        }

        /* here we will build popup menu and show it. */
        @Override
        public void onClick(View view) {
            PopupMenu popupMenu = new PopupMenu(mContext, view);

            if (isCommentEditable()) {
                popupMenu.getMenu().add(MENU_GROUP_ID,
                        MENU_EDIT_ITEM_ID, MENU_EDIT_ITEM_ORDER,
                        R.string.edit);
            }

            if (isCommentDeletable()) {
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

                    if (menuItem.getItemId() == MENU_EDIT_ITEM_ID) {
                        mListener.onCommentEdit(mComment);
                    } else if (menuItem.getItemId() == MENU_DELETE_ITEM_ID) {
                        mListener.onCommentDelete(mComment);
                    }

                    return true;
                }
            });

            popupMenu.show();
        }
    }

    public interface OnCommentClickListener {
        void onCommentEdit(InterpretationComment comment);

        void onCommentDelete(InterpretationComment comment);
    }
}
