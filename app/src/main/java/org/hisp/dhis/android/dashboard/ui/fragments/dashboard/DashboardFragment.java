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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.squareup.otto.Subscribe;

import org.hisp.dhis.android.dashboard.R;
import org.hisp.dhis.android.dashboard.api.job.NetworkJob;
import org.hisp.dhis.android.dashboard.api.models.Access;
import org.hisp.dhis.android.dashboard.api.models.Dashboard;
import org.hisp.dhis.android.dashboard.api.models.DashboardElement;
import org.hisp.dhis.android.dashboard.api.models.DashboardItem;
import org.hisp.dhis.android.dashboard.api.models.DashboardItem$Table;
import org.hisp.dhis.android.dashboard.api.models.DashboardItemContent;
import org.hisp.dhis.android.dashboard.api.models.meta.State;
import org.hisp.dhis.android.dashboard.api.persistence.loaders.DbLoader;
import org.hisp.dhis.android.dashboard.api.persistence.loaders.Query;
import org.hisp.dhis.android.dashboard.api.utils.EventBusProvider;
import org.hisp.dhis.android.dashboard.ui.activities.DashboardElementDetailActivity;
import org.hisp.dhis.android.dashboard.ui.adapters.DashboardItemAdapter;
import org.hisp.dhis.android.dashboard.ui.events.UiEvent;
import org.hisp.dhis.android.dashboard.ui.fragments.BaseFragment;
import org.hisp.dhis.android.dashboard.ui.fragments.interpretation.InterpretationCreateFragment;
import org.hisp.dhis.android.dashboard.ui.views.GridDividerDecoration;

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
    public static final String TAG = DashboardFragment.class.getSimpleName();

    ViewSwitcher mViewSwitcher;

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
        return inflater.inflate(R.layout.fragment_dashboard_list, group, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mViewSwitcher = (ViewSwitcher) view;
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

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

            List<DbLoader.TrackedTable> trackedTables = Arrays.asList(
                    new DbLoader.TrackedTable(DashboardItem.class),
                    new DbLoader.TrackedTable(DashboardElement.class));
            return new DbLoader<>(getActivity().getApplicationContext(),
                    trackedTables, new ItemsQuery(args.getLong(DASHBOARD_ID)));
        }
        return null;
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onResponseReceived(NetworkJob.NetworkJobResult<?> result) {
        Log.d(TAG, "Received " + result.getResourceType());
    }
    @Override
    public void onLoadFinished(Loader<List<DashboardItem>> loader,
            List<DashboardItem> dashboardItems) {
        if (loader.getId() == LOADER_ID) {
            boolean isDashboardItemListEmpty = dashboardItems == null || dashboardItems.isEmpty();
            boolean isEmptyListMessageShown = mViewSwitcher.getCurrentView().getId() ==
                    R.id.text_view_empty_dashboard_message;

            if (isDashboardItemListEmpty && !isEmptyListMessageShown) {
                mViewSwitcher.showNext();
            } else if (!isDashboardItemListEmpty && isEmptyListMessageShown) {
                mViewSwitcher.showNext();
            }

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
            case DashboardItemContent.TYPE_REPORT_TABLE:
            case DashboardItemContent.TYPE_EVENT_REPORT: {
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
            element.deleteDashboardElement();

            if (isDhisServiceBound()) {
                getDhisService().syncDashboards();
                EventBusProvider.post(new UiEvent(UiEvent.UiEventType.SYNC_DASHBOARDS));
            }
        }
    }

    @Override
    public void onItemDeleteClick(DashboardItem item) {
        if (item != null) {
            item.deleteDashboardItem();

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
            List<DashboardItem> dashboardItems = new Select()
                    .from(DashboardItem.class)
                    .where(
                            Condition.column(DashboardItem$Table.DASHBOARD_DASHBOARD).is(mDashboardId),
                            Condition.column(DashboardItem$Table.STATE).isNot(State.TO_DELETE.toString()),
                            Condition.column(DashboardItem$Table.TYPE).in(
                                    DashboardItemContent.TYPE_CHART,
                                    DashboardItemContent.TYPE_EVENT_CHART,
                                    DashboardItemContent.TYPE_MAP,
                                    DashboardItemContent.TYPE_REPORT_TABLE,
                                    DashboardItemContent.TYPE_EVENT_REPORT,
                                    DashboardItemContent.TYPE_USERS,
                                    DashboardItemContent.TYPE_REPORTS,
                                    DashboardItemContent.TYPE_RESOURCES,
                                    DashboardItemContent.TYPE_MESSAGES)
                    ).orderBy(DashboardItem$Table.ORDERPOSITION)
                    .queryList();
            if (dashboardItems != null && !dashboardItems.isEmpty()) {
                for (DashboardItem dashboardItem : dashboardItems) {
                    List<DashboardElement> dashboardElements
                            = dashboardItem.queryRelatedDashboardElements();
                    dashboardItem.setDashboardElements(dashboardElements);
                }
            }
            return dashboardItems;
        }
    }
}