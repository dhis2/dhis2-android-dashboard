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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.dhis2.android.dashboard.R;
import org.dhis2.android.dashboard.api.models.InterpretationComment;
import org.dhis2.android.dashboard.api.models.User;
import org.dhis2.android.dashboard.ui.adapters.InterpretationCommentsAdapter.CommentViewHolder;
import org.joda.time.DateTime;

/**
 * @author Araz Abishov <araz.abishov.gsoc@gmail.com>.
 */
public final class InterpretationCommentsAdapter extends AbsAdapter<InterpretationComment, CommentViewHolder> {
    private static final String DATE_FORMAT = "YYYY-MM-dd";
    private static final String EMPTY_FIELD = "";

    public InterpretationCommentsAdapter(Context context, LayoutInflater inflater) {
        super(context, inflater);
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CommentViewHolder(getLayoutInflater()
                .inflate(R.layout.recycler_view_interpretation_comment, parent, false));
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        InterpretationComment comment = getItem(position);
        DateTime created = comment.getCreated();
        User user = comment.getUser();

        holder.commentTextView.setText(comment.getText());
        holder.createdTextView.setText(created == null ? EMPTY_FIELD : created.toString(DATE_FORMAT));
        holder.usernameTextView.setText(user == null ? EMPTY_FIELD : user.getDisplayName());
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        final TextView usernameTextView;
        final TextView createdTextView;
        final TextView commentTextView;

        public CommentViewHolder(View itemView) {
            super(itemView);

            usernameTextView = (TextView) itemView.findViewById(R.id.comment_username);
            createdTextView = (TextView) itemView.findViewById(R.id.comment_created);
            commentTextView = (TextView) itemView.findViewById(R.id.comment_text);
        }
    }
}
