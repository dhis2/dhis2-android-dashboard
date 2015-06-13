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

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.Model;

import org.dhis2.android.dashboard.R;
import org.dhis2.android.dashboard.api.models.Access;
import org.dhis2.android.dashboard.api.models.Dashboard;
import org.dhis2.android.dashboard.api.models.DashboardElement;
import org.dhis2.android.dashboard.api.models.DashboardItem;
import org.dhis2.android.dashboard.api.models.DashboardItem$Table;
import org.dhis2.android.dashboard.api.persistence.loaders.DbLoader;
import org.dhis2.android.dashboard.api.persistence.loaders.Query;
import org.dhis2.android.dashboard.ui.adapters.DashboardItemAdapter;
import org.dhis2.android.dashboard.ui.adapters.DashboardItemAdapter.OnItemClickListener;
import org.dhis2.android.dashboard.ui.fragments.BaseFragment;
import org.dhis2.android.dashboard.ui.views.GridDividerDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DashboardFragment extends BaseFragment
        implements LoaderManager.LoaderCallbacks<List<DashboardItem>>, OnItemClickListener {
    private static final int LOADER_ID = 74734523;
    private static final String DASHBOARD_ID = "arg:dashboardId";
    private static final String DELETE = "arg:delete";
    private static final String UPDATE = "arg:update";
    private static final String READ = "arg:read";
    private static final String WRITE = "arg:write";
    private static final String MANAGE = "arg:manage";
    private static final String EXTERNALIZE = "arg:externalize";

    @InjectView(R.id.grid) RecyclerView mGridView;
    DashboardItemAdapter mAdapter;

    public static DashboardFragment newInstance(Dashboard dashboard) {
        DashboardFragment fragment = new DashboardFragment();
        Access access = dashboard.getAccess();

        Bundle args = new Bundle();
        args.putLong(DASHBOARD_ID, dashboard.getLocalId());
        args.putBoolean(DELETE, access.isDelete());
        args.putBoolean(UPDATE, access.isUpdate());
        args.putBoolean(READ, access.isRead());
        args.putBoolean(WRITE, access.isWrite());
        args.putBoolean(MANAGE, access.isManage());
        args.putBoolean(EXTERNALIZE, access.isExternalize());

        fragment.setArguments(args);
        return fragment;
    }

    private static Access getAccessFromBundle(Bundle args) {
        Access access = new Access();

        access.setDelete(args.getBoolean(DELETE));
        access.setUpdate(args.getBoolean(UPDATE));
        access.setRead(args.getBoolean(READ));
        access.setWrite(args.getBoolean(WRITE));
        access.setManage(args.getBoolean(MANAGE));
        access.setExternalize(args.getBoolean(EXTERNALIZE));

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
            List<Class<? extends Model>> tablesToTrack = new ArrayList<>();
            tablesToTrack.add(DashboardItem.class);
            tablesToTrack.add(DashboardElement.class);
            return new DbLoader<>(getActivity().getApplicationContext(),
                    tablesToTrack, new ItemsQuery(args.getLong(DASHBOARD_ID)));
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
    public void onItemClick(DashboardItem item) {
        Toast.makeText(getActivity(), "BODY CLICK: " +
                item.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemShareInterpretation(DashboardItem item) {
        Toast.makeText(getActivity(), "SHARE INTERPRETATION: " +
                item.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemDelete(DashboardItem item) {
        mAdapter.removeItem(item);
    }

    private static class ItemsQuery implements Query<List<DashboardItem>> {
        private final long mDashboardId;

        public ItemsQuery(long dashboardId) {
            mDashboardId = dashboardId;
        }

        @Override public List<DashboardItem> query(Context context) {
            List<DashboardItem> dashboardItems = new Select()
                    .from(DashboardItem.class)
                    .where(Condition.column(DashboardItem$Table
                            .DASHBOARD_DASHBOARD).is(mDashboardId))
                    .queryList();
            if (dashboardItems != null && !dashboardItems.isEmpty()) {
                for (DashboardItem dashboardItem : dashboardItems) {
                    DashboardItem.readElementsIntoItem(dashboardItem);
                }
            }
            return dashboardItems;
        }
    }
}
