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

package org.dhis2.android.dashboard.ui.fragments.interpretation;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.Model;

import org.dhis2.android.dashboard.R;
import org.dhis2.android.dashboard.api.models.IdentifiableObject;
import org.dhis2.android.dashboard.api.models.Interpretation;
import org.dhis2.android.dashboard.api.models.Interpretation$Table;
import org.dhis2.android.dashboard.api.models.InterpretationComment;
import org.dhis2.android.dashboard.api.models.InterpretationComment$Table;
import org.dhis2.android.dashboard.api.models.User;
import org.dhis2.android.dashboard.api.models.User$Table;
import org.dhis2.android.dashboard.api.models.UserAccount;
import org.dhis2.android.dashboard.api.persistence.loaders.DbLoader;
import org.dhis2.android.dashboard.api.persistence.loaders.Query;
import org.dhis2.android.dashboard.ui.adapters.InterpretationCommentsAdapter;
import org.dhis2.android.dashboard.ui.fragments.BaseFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

import static org.dhis2.android.dashboard.utils.TextUtils.isEmpty;

/**
 * @author Araz Abishov <araz.abishov.gsoc@gmail.com>.
 */
public class InterpretationCommentsFragment extends BaseFragment
        implements LoaderCallbacks<List<InterpretationComment>> {
    private static final int LOADER_ID = 89636345;
    private static final String INTERPRETATION_ID = "arg:interpretationId";
    private static final String EMPTY_FIELD = "";

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @Bind(R.id.interpretation_comment_edit_text)
    EditText mNewCommentText;

    @Bind(R.id.add_interpretation_comment_button)
    View mAddNewComment;

    InterpretationCommentsAdapter mAdapter;

    Interpretation mInterpretation;
    User mUser;

    public static InterpretationCommentsFragment newInstance(long interpretationId) {
        Bundle arguments = new Bundle();
        arguments.putLong(INTERPRETATION_ID, interpretationId);

        InterpretationCommentsFragment fragment
                = new InterpretationCommentsFragment();
        fragment.setArguments(arguments);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_interpretation_comments, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mAdapter = new InterpretationCommentsAdapter(getActivity(),
                getLayoutInflater(savedInstanceState));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

        mInterpretation = new Select()
                .from(Interpretation.class)
                .where(Condition.column(Interpretation$Table
                        .ID).is(getArguments().getLong(INTERPRETATION_ID)))
                .querySingle();
        //mUser = UserAccount.getCurrentUser();
        UserAccount account = UserAccount
                .getCurrentUserAccountFromDb();
        mUser = new Select()
                .from(User.class)
                .where(Condition.column(User$Table
                        .UID).is(account.getUId()))
                .querySingle();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, getArguments(), this);
    }

    @Override
    public Loader<List<InterpretationComment>> onCreateLoader(int id, Bundle args) {
        if (LOADER_ID == id) {
            List<Class<? extends Model>> tablesToTrack = new ArrayList<>();
            //tablesToTrack.add(InterpretationComment.class);
            return new DbLoader<>(getActivity().getApplicationContext(), tablesToTrack,
                    new CommentsQuery(args.getLong(INTERPRETATION_ID)));
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<List<InterpretationComment>> loader,
                               List<InterpretationComment> data) {
        if (LOADER_ID == loader.getId()) {
            mAdapter.swapData(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<InterpretationComment>> loader) {
        if (LOADER_ID == loader.getId()) {
            mAdapter.swapData(null);
        }
    }

    @SuppressWarnings("unused")
    @OnTextChanged(R.id.interpretation_comment_edit_text)
    public void onCommentChanged(Editable text) {
        mAddNewComment.setVisibility(isEmpty(text) ? View.INVISIBLE : View.VISIBLE);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.add_interpretation_comment_button)
    public void onAddComment() {
        String newCommentText = mNewCommentText.getText().toString();
        InterpretationComment comment = Interpretation
                .addComment(mInterpretation, mUser, newCommentText);
        comment.save();
        mAdapter.getData().add(comment);
        mAdapter.notifyItemInserted(mAdapter.getItemCount() - 1);
        mNewCommentText.setText(EMPTY_FIELD);
    }

    private static class CommentsQuery implements Query<List<InterpretationComment>> {
        private final long mInterpretationId;

        public CommentsQuery(long interpretationId) {
            mInterpretationId = interpretationId;
        }

        @Override
        public List<InterpretationComment> query(Context context) {
            List<InterpretationComment> comments = new Select().from(InterpretationComment.class)
                    .where(Condition.column(InterpretationComment$Table
                            .INTERPRETATION_INTERPRETATION).is(mInterpretationId))
                    .queryList();
            Collections.sort(comments, IdentifiableObject.CREATED_COMPARATOR);
            return comments;
        }
    }
}
