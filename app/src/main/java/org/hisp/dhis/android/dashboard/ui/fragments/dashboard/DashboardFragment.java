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

package org.hisp.dhis.android.dashboard.ui.fragments.dashboard;

import android.content.Context;
import android.content.Intent;
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

import org.hisp.dhis.android.dashboard.R;
import org.hisp.dhis.android.dashboard.ui.activities.DashboardElementDetailActivity;
import org.hisp.dhis.android.dashboard.ui.adapters.DashboardItemAdapter;
import org.hisp.dhis.android.dashboard.ui.events.UiEvent;
import org.hisp.dhis.android.dashboard.ui.fragments.BaseFragment;
import org.hisp.dhis.android.dashboard.ui.fragments.interpretation.InterpretationCreateFragment;
import org.hisp.dhis.android.dashboard.ui.views.GridDividerDecoration;
import org.hisp.dhis.android.dashboard.utils.EventBusProvider;
import org.hisp.dhis.android.sdk.core.api.Models;
import org.hisp.dhis.android.sdk.core.persistence.loaders.DbLoader;
import org.hisp.dhis.android.sdk.core.persistence.loaders.Query;
import org.hisp.dhis.android.sdk.core.persistence.loaders.TrackedTable;
import org.hisp.dhis.android.sdk.models.common.Access;
import org.hisp.dhis.android.sdk.models.common.meta.State;
import org.hisp.dhis.android.sdk.models.dashboard.Dashboard;
import org.hisp.dhis.android.sdk.models.dashboard.DashboardElement;
import org.hisp.dhis.android.sdk.models.dashboard.DashboardItem;
import org.hisp.dhis.android.sdk.models.dashboard.DashboardItemContent;

import java.util.Arrays;
import java.util.List;

public class DashboardFragment extends BaseFragment
        implements LoaderManager.LoaderCallbacks<List<DashboardItem>>, DashboardItemAdapter.OnItemClickListener {
    private static final int LOADER_ID = 74734523;
    private static final String DASHBOARD_ID = "arg:dashboardId";
    private static final String DELETE = "arg:delete";
    private static final String UPDATE = "arg:update";
    private static final String READ = "arg:read";
    private static final String WRITE = "arg:write";
    private static final String MANAGE = "arg:manage";
    private static final String EXTERNALIZE = "arg:externalize";

    RecyclerView mRecyclerView;

    DashboardItemAdapter mAdapter;

    public static DashboardFragment newInstance(Dashboard dashboard) {
        DashboardFragment fragment = new DashboardFragment();
        Access access = dashboard.getAccess();

        Bundle args = new Bundle();
        args.putLong(DASHBOARD_ID, dashboard.getId());
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
        return inflater.inflate(R.layout.recycler_view, group, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mRecyclerView = (RecyclerView) view;

        final int spanCount = getResources().getInteger(R.integer.column_nums);

        mAdapter = new DashboardItemAdapter(getActivity(),
                getAccessFromBundle(getArguments()), spanCount, this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), spanCount);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {

            @Override
            public int getSpanSize(int position) {
                return mAdapter.getSpanSize(position);
            }
        });

        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new GridDividerDecoration(getActivity()
                .getApplicationContext()));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, getArguments(), this);
    }

    @Override
    public Loader<List<DashboardItem>> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_ID && isAdded()) {
            /* When two tables are joined sometimes we can get empty rows.
            For example dashboard does not contain any dashboard items.
            In order to avoid strange bugs during table JOINs,
            we explicitly state that we want only not null values  */

            List<TrackedTable> trackedTables = Arrays.asList(
                    new TrackedTable(DashboardItem.class),
                    new TrackedTable(DashboardElement.class));
            return new DbLoader<>(getActivity().getApplicationContext(),
                    trackedTables, new ItemsQuery(args.getLong(DASHBOARD_ID)));
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
    public void onContentClick(DashboardElement element) {
        switch (element.getDashboardItem().getType()) {
            case DashboardItemContent.TYPE_CHART:
            case DashboardItemContent.TYPE_EVENT_CHART:
            case DashboardItemContent.TYPE_MAP:
            case DashboardItemContent.TYPE_REPORT_TABLE: {
                Intent intent = DashboardElementDetailActivity
                        .newIntentForDashboardElement(getActivity(), element.getId());
                startActivity(intent);
                break;
            }
            default: {
                String message = getString(R.string.unsupported_dashboard_item_type);
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onContentDeleteClick(DashboardElement element) {
        if (element != null) {
            // element.deleteDashboardElement();
            Models.dashboardElements().delete(element);

            if (isDhisServiceBound()) {
                getDhisService().syncDashboards();
                EventBusProvider.post(new UiEvent(UiEvent.UiEventType.SYNC_DASHBOARDS));
            }
        }
    }

    @Override
    public void onItemDeleteClick(DashboardItem item) {
        if (item != null) {
            // item.deleteDashboardItem();
            Models.dashboardItems().delete(item);

            if (isDhisServiceBound()) {
                getDhisService().syncDashboards();
                EventBusProvider.post(new UiEvent(UiEvent.UiEventType.SYNC_DASHBOARDS));
            }
        }
    }

    @Override
    public void onItemShareClick(DashboardItem item) {
        InterpretationCreateFragment
                .newInstance(item.getId())
                .show(getChildFragmentManager());
    }

    private static class ItemsQuery implements Query<List<DashboardItem>> {
        private final long mDashboardId;

        public ItemsQuery(long dashboardId) {
            mDashboardId = dashboardId;
        }

        @Override
        public List<DashboardItem> query(Context context) {
            // temporary workaround
            Dashboard dashboard = new Dashboard();
            dashboard.setId(mDashboardId);

            List<DashboardItem> dashboardItems = Models.dashboardItems()
                    .filter(dashboard, State.TO_DELETE, DashboardItemContent.TYPE_MESSAGES);
            if (dashboardItems != null && !dashboardItems.isEmpty()) {
                for (DashboardItem dashboardItem : dashboardItems) {
                    List<DashboardElement> dashboardElements = Models.dashboardElements()
                            .filter(dashboardItem, State.TO_DELETE);
                    dashboardItem.setDashboardElements(dashboardElements);
                }
            }
            return dashboardItems;
        }
    }
}