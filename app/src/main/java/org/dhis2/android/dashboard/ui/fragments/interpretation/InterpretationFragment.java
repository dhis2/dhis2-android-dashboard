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
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.Model;

import org.dhis2.android.dashboard.R;
import org.dhis2.android.dashboard.api.models.Interpretation;
import org.dhis2.android.dashboard.api.models.Interpretation$Table;
import org.dhis2.android.dashboard.api.models.InterpretationComment;
import org.dhis2.android.dashboard.api.models.InterpretationComment$Table;
import org.dhis2.android.dashboard.api.models.InterpretationElement;
import org.dhis2.android.dashboard.api.models.InterpretationElement$Table;
import org.dhis2.android.dashboard.api.models.meta.State;
import org.dhis2.android.dashboard.api.persistence.loaders.DbLoader;
import org.dhis2.android.dashboard.api.persistence.loaders.Query;
import org.dhis2.android.dashboard.ui.activities.InterpretationCommentsActivity;
import org.dhis2.android.dashboard.ui.adapters.InterpretationAdapter;
import org.dhis2.android.dashboard.ui.fragments.BaseFragment;
import org.dhis2.android.dashboard.ui.views.GridDividerDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Araz Abishov <araz.abishov.gsoc@gmail.com>.
 */
public final class InterpretationFragment extends BaseFragment
        implements LoaderManager.LoaderCallbacks<List<Interpretation>>,
        View.OnClickListener, Toolbar.OnMenuItemClickListener,
        InterpretationAdapter.OnItemClickListener {
    private static final int LOADER_ID = 23452435;

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    InterpretationAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_interpretations, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        mAdapter = new InterpretationAdapter(getActivity(),
                getLayoutInflater(savedInstanceState), this);

        final int spanCount = getResources().getInteger(R.integer.column_nums);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), spanCount);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new GridDividerDecoration(getActivity()
                .getApplicationContext()));
        mRecyclerView.setAdapter(mAdapter);

        mToolbar.setNavigationIcon(R.mipmap.ic_menu);
        mToolbar.setNavigationOnClickListener(this);
        mToolbar.setTitle(R.string.interpretations);
        mToolbar.inflateMenu(R.menu.menu_interpretations_fragment);
        mToolbar.setOnMenuItemClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, getArguments(), this);
    }

    @Override
    public Loader<List<Interpretation>> onCreateLoader(int id, Bundle args) {
        List<Class<? extends Model>> tablesToTrack = new ArrayList<>();
        tablesToTrack.add(InterpretationComment.class);
        return new DbLoader<>(getActivity().getApplicationContext(),
                tablesToTrack, new InterpretationsQuery());
    }

    @Override
    public void onLoadFinished(Loader<List<Interpretation>> loader, List<Interpretation> data) {
        if (loader != null && loader.getId() == LOADER_ID) {
            if (data != null) {
                mAdapter.swapData(data);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Interpretation>> loader) {
        if (loader != null && loader.getId() == LOADER_ID) {
            // resetting data here
            mAdapter.swapData(null);
        }
    }

    @Override
    public void onClick(View v) {
        toggleNavigationDrawer();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh: {
                getService().syncInterpretations();
                return true;
            }
        }
        return false;
    }

    @Override
    public void onInterpretationContentClick(Interpretation interpretation) {

    }

    @Override
    public void onInterpretationTextClick(Interpretation interpretation) {

    }

    @Override
    public void onInterpretationDeleteClick(Interpretation interpretation) {
        int position = mAdapter.getData().indexOf(interpretation);
        if (!(position < 0)) {
            mAdapter.getData().remove(position);
            mAdapter.notifyItemRemoved(position);
            interpretation.deleteInterpretation();
        }
    }

    @Override
    public void onInterpretationEditClick(Interpretation interpretation) {
        InterpretationTextFragment fragment = InterpretationTextFragment
                .newInstance(interpretation);
        fragment.show(getChildFragmentManager());
    }

    @Override
    public void onInterpretationCommentsClick(Interpretation interpretation) {
        Intent intent = InterpretationCommentsActivity
                .newIntent(getActivity(), interpretation.getId());
        startActivity(intent);
    }


    static class InterpretationsQuery implements Query<List<Interpretation>> {

        @Override
        public List<Interpretation> query(Context context) {
            List<Interpretation> interpretations
                    = new Select()
                    .from(Interpretation.class)
                    .where(Condition.column(Interpretation$Table
                            .STATE).isNot(State.TO_DELETE.toString()))
                    .queryList();
            for (Interpretation interpretation : interpretations) {
                List<InterpretationElement> elements = new Select()
                        .from(InterpretationElement.class)
                        .where(Condition.column(InterpretationElement$Table
                                .INTERPRETATION_INTERPRETATION).is(interpretation.getId()))
                        .queryList();
                List<InterpretationComment> comments = new Select()
                        .from(InterpretationComment.class)
                        .where(Condition.column(InterpretationComment$Table
                                .INTERPRETATION_INTERPRETATION).is(interpretation.getId()))
                        .and(Condition.column(InterpretationComment$Table
                                .STATE).isNot(State.TO_DELETE.toString()))
                        .queryList();
                interpretation.setInterpretationElements(elements);
                interpretation.setComments(comments);
            }

            // sort interpretations by created field in reverse order.
            Collections.sort(interpretations,
                    Collections.reverseOrder(Interpretation.CREATED_COMPARATOR));
            return interpretations;
        }
    }
}
