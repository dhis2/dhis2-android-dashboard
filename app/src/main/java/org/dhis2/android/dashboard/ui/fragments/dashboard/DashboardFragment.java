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

package org.dhis2.android.dashboard.ui.fragments.dashboard;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.dhis2.android.dashboard.R;
import org.dhis2.android.dashboard.api.models.Access;
import org.dhis2.android.dashboard.api.models.Dashboard;
import org.dhis2.android.dashboard.api.models.DashboardItem;
import org.dhis2.android.dashboard.api.persistence.DbManager;
import org.dhis2.android.dashboard.api.persistence.database.DbContract.DashboardItems;
import org.dhis2.android.dashboard.api.persistence.database.DbContract.Dashboards;
import org.dhis2.android.dashboard.api.persistence.loaders.CursorLoaderBuilder;
import org.dhis2.android.dashboard.api.persistence.loaders.Transformation;
import org.dhis2.android.dashboard.ui.adapters.DashboardItemAdapter;
import org.dhis2.android.dashboard.ui.adapters.DashboardItemAdapter.OnItemClickListener;
import org.dhis2.android.dashboard.ui.fragments.BaseFragment;
import org.dhis2.android.dashboard.ui.views.GridDividerDecoration;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DashboardFragment extends BaseFragment
        implements LoaderManager.LoaderCallbacks<List<DashboardItem>>, OnItemClickListener {
    private static final int LOADER_ID = 74734523;

    @InjectView(R.id.grid) RecyclerView mGridView;
    DashboardItemAdapter mAdapter;

    public static DashboardFragment newInstance(Dashboard dashboard) {
        DashboardFragment fragment = new DashboardFragment();
        Access access = dashboard.getAccess();

        Bundle args = new Bundle();
        args.putString(Dashboards.ID, dashboard.getId());
        args.putBoolean(Dashboards.DELETE, access.isDelete());
        args.putBoolean(Dashboards.UPDATE, access.isUpdate());
        args.putBoolean(Dashboards.READ, access.isRead());
        args.putBoolean(Dashboards.WRITE, access.isWrite());
        args.putBoolean(Dashboards.MANAGE, access.isManage());
        args.putBoolean(Dashboards.EXTERNALIZE, access.isExternalize());

        fragment.setArguments(args);
        return fragment;
    }

    private static Access getAccessFromBundle(Bundle args) {
        Access access = new Access();

        access.setDelete(args.getBoolean(Dashboards.DELETE));
        access.setUpdate(args.getBoolean(Dashboards.UPDATE));
        access.setRead(args.getBoolean(Dashboards.READ));
        access.setWrite(args.getBoolean(Dashboards.WRITE));
        access.setManage(args.getBoolean(Dashboards.MANAGE));
        access.setExternalize(args.getBoolean(Dashboards.EXTERNALIZE));

        return access;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle bundle) {
        return inflater.inflate(R.layout.fragment_dashboard, group, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.inject(this, view);

        mAdapter = new DashboardItemAdapter(getActivity(),
                getAccessFromBundle(getArguments()), this);

        int spanCount = getResources().getInteger(R.integer.column_nums);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), spanCount);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mGridView.setLayoutManager(gridLayoutManager);
        mGridView.setItemAnimator(new DefaultItemAnimator());
        mGridView.addItemDecoration(new GridDividerDecoration(getActivity()
                .getApplicationContext()));
        mGridView.setAdapter(mAdapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isAdded()) {
            getLoaderManager().initLoader(LOADER_ID, getArguments(), this);
        }
    }

    @Override
    public Loader<List<DashboardItem>> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_ID && isAdded()) {
            /* When two tables are joined sometimes we can get empty rows.
            For example dashboard does not contain any dashboard items.
            In order to avoid strange bugs during table JOINs,
            we explicitly state that we want only not null values  */
            final String NON_NULL_DASHBOARD_ITEMS = DashboardItems.TABLE_NAME + "." +
                    DashboardItems.ID + " IS NOT NULL";
            Uri uri = Dashboards.buildUriWithItems(
                    args.getString(Dashboards.ID));
            return CursorLoaderBuilder.forUri(uri)
                    .projection(DbManager.with(DashboardItem.class).getProjection())
                    .selection(NON_NULL_DASHBOARD_ITEMS)
                    .transformation(new Transform())
                    .build(getActivity().getApplicationContext());
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<List<DashboardItem>> loader,
                               List<DashboardItem> dashboardItems) {
        if (loader.getId() == LOADER_ID) {
            mAdapter.swapData(dashboardItems);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<DashboardItem>> loader) {
        if (loader.getId() == LOADER_ID) {
            mAdapter.swapData(null);
        }
    }

    @Override
    public void onItemClick(int position, DashboardItem item) {
        Toast.makeText(getActivity(), "BODY CLICK: " +
                position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemShareInterpretation(int position, DashboardItem item) {
        Toast.makeText(getActivity(), "SHARE INTERPRETATION: " +
                position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemDelete(int position, DashboardItem item) {
        int truePosition = mAdapter.getData().indexOf(item);
        mAdapter.getData().remove(truePosition);
        mAdapter.notifyItemRemoved(truePosition);
    }

    private static class Transform implements Transformation<List<DashboardItem>> {

        @Override public List<DashboardItem> transform(Context context, Cursor cursor) {
            return DbManager.with(DashboardItem.class).map(cursor, false);
        }
    }
}
