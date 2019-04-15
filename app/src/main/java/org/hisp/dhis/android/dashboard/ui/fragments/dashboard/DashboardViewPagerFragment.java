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
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.squareup.otto.Subscribe;

import org.hisp.dhis.android.dashboard.DhisService;
import org.hisp.dhis.android.dashboard.R;
import org.hisp.dhis.android.dashboard.api.controllers.DhisController;
import org.hisp.dhis.android.dashboard.api.job.NetworkJob;
import org.hisp.dhis.android.dashboard.api.models.Access;
import org.hisp.dhis.android.dashboard.api.models.Dashboard;
import org.hisp.dhis.android.dashboard.api.models.Dashboard$Table;
import org.hisp.dhis.android.dashboard.api.models.meta.State;
import org.hisp.dhis.android.dashboard.api.network.SessionManager;
import org.hisp.dhis.android.dashboard.api.persistence.loaders.DbLoader;
import org.hisp.dhis.android.dashboard.api.persistence.loaders.Query;
import org.hisp.dhis.android.dashboard.api.persistence.preferences.ResourceType;
import org.hisp.dhis.android.dashboard.api.utils.SyncStrategy;
import org.hisp.dhis.android.dashboard.ui.adapters.DashboardAdapter;
import org.hisp.dhis.android.dashboard.ui.events.UiEvent;
import org.hisp.dhis.android.dashboard.ui.fragments.BaseFragment;
import org.hisp.dhis.android.dashboard.ui.fragments.SyncingController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class DashboardViewPagerFragment extends BaseFragment
        implements LoaderCallbacks<List<Dashboard>>, View.OnClickListener,
        ViewPager.OnPageChangeListener, SyncingController {

    static final String TAG = DashboardViewPagerFragment.class.getSimpleName();
    static final String IS_LOADING = "state:isLoading";
    static final int LOADER_ID = 1233432;

    @BindView(R.id.dashboard_tabs)
    TabLayout mTabs;

    @BindView(R.id.dashboard_view_pager)
    ViewPager mViewPager;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.progress_bar)
    SmoothProgressBar mProgressBar;

    DashboardAdapter mDashboardAdapter;

    long lastDashboardId;

    private DhisController.ImageNetworkPolicy mImageNetworkPolicy =
            DhisController.ImageNetworkPolicy.CACHE;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboards, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        mDashboardAdapter = new DashboardAdapter(getChildFragmentManager(), this, new ArrayList
                <DashboardFragment>(), new ArrayList<Dashboard>());
        mViewPager.setAdapter(mDashboardAdapter);
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setOffscreenPageLimit(2);

        mToolbar.setNavigationIcon(R.mipmap.ic_menu);
        mToolbar.setNavigationOnClickListener(this);
        mToolbar.setTitle(R.string.dashboard);
        mToolbar.inflateMenu(R.menu.menu_dashboard_fragment);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return onMenuItemClicked(item);
            }
        });

        if (isDhisServiceBound() &&
                !getDhisService().isJobRunning(DhisService.SYNC_DASHBOARDS) &&
                !SessionManager.getInstance().isResourceTypeSynced(ResourceType.DASHBOARDS)) {
            syncDashboards(SyncStrategy.DOWNLOAD_ONLY_NEW);
        }

        boolean isLoading = isDhisServiceBound() &&
                (getDhisService().isJobRunning(DhisService.SYNC_DASHBOARDS)
                ||
                getDhisService().isJobRunning(DhisService.SYNC_DASHBOARD_CONTENT)
                ||
                getDhisService().isJobRunning(DhisService.PULL_DASHBOARD_IMAGES));
        if ((savedInstanceState != null &&
                savedInstanceState.getBoolean(IS_LOADING)) || isLoading) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, savedInstanceState, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(IS_LOADING, mProgressBar
                .getVisibility() == View.VISIBLE);
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<List<Dashboard>> onCreateLoader(int id, Bundle state) {
        if (id == LOADER_ID && isAdded()) {
            List<DbLoader.TrackedTable> trackedTables = Arrays.asList(
                    new DbLoader.TrackedTable(Dashboard.class));
            return new DbLoader<>(getActivity().getApplicationContext(),
                    trackedTables, new DashboardQuery());
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<List<Dashboard>> loader, List<Dashboard> data) {
        if (loader.getId() == LOADER_ID && data != null) {
            setDashboards(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Dashboard>> loader) {
        if (loader.getId() == LOADER_ID) {
            setDashboards(null);
        }
    }

    @Override
    public void onClick(View view) {
        toggleNavigationDrawer();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {
        // stub implementation
    }

    @Override
    public void onPageSelected(int position) {
        Access dashboardAccess = mDashboardAdapter.getDashboardAccess(position);

        Menu menu = mToolbar.getMenu();
        menu.findItem(R.id.add_dashboard_item)
                .setVisible(dashboardAccess.isUpdate());
        menu.findItem(R.id.manage_dashboard)
                .setVisible(dashboardAccess.isUpdate());
        lastDashboardId = mDashboardAdapter.getDashboardID(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // stub implementation
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onUiEventReceived(UiEvent uiEvent) {
        if (uiEvent.getEventType() == UiEvent.UiEventType.SYNC_DASHBOARDS) {
            boolean isLoading = isDhisServiceBound() &&
                    getDhisService().isJobRunning(DhisService.SYNC_DASHBOARDS);
            if (isLoading) {
                mProgressBar.setVisibility(View.VISIBLE);
            } else {
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void setDashboards(List<Dashboard> dashboards) {
        if (dashboards != null) {
            List<DashboardFragment> dashboardFragments=new ArrayList<>();
            for (Dashboard dashboard : dashboards) {
                DashboardFragment dashboardFragment = DashboardFragment
                        .newInstance(dashboard);
                dashboardFragment.setSyncingController(this);
                dashboardFragments.add(dashboardFragment);
            }

            mDashboardAdapter = new DashboardAdapter(getChildFragmentManager(), this, dashboardFragments,dashboards);
            mViewPager.setAdapter(mDashboardAdapter);
        }
        mTabs.removeAllTabs();

        if (dashboards != null && !dashboards.isEmpty()) {
            mTabs.setupWithViewPager(mViewPager);
        }
        if(lastDashboardId > 0){
            Integer position = mDashboardAdapter.getDashboardPosition(lastDashboardId);
            if(position!=null && position!=-1){
                onPageSelected(position);mViewPager.setCurrentItem(position);
            }
        }
    }

    public boolean onMenuItemClicked(MenuItem item) {
        if (isSyncing()) {
            Toast.makeText(getContext(),
                    getString(R.string.action_not_allowed_during_sync),
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        switch (item.getItemId()) {
            case R.id.add_dashboard_item: {
                long dashboardId = mDashboardAdapter
                        .getDashboardID(mViewPager.getCurrentItem());
                DashboardItemAddFragment
                        .newInstance(dashboardId)
                        .show(getChildFragmentManager());
                return true;
            }
            case R.id.refresh: {
                mImageNetworkPolicy = DhisController.ImageNetworkPolicy.NO_CACHE;
                syncDashboards(SyncStrategy.DOWNLOAD_ALL);
                return true;
            }
            case R.id.add_dashboard: {
                new DashboardAddFragment()
                        .show(getChildFragmentManager());
                return true;
            }
            case R.id.manage_dashboard: {
                Long idDashboard = mDashboardAdapter
                        .getDashboardID(mViewPager.getCurrentItem());
                DashboardManageFragment
                        .newInstance(idDashboard)
                        .show(getChildFragmentManager());
                return true;
            }
        }
        return false;
    }

    private void syncDashboards(SyncStrategy syncStrategy) {
        if (isDhisServiceBound()) {
            getDhisService().syncDashboardsAndContent(syncStrategy);
            getDhisService().syncDataMaps();
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onResponseReceived(NetworkJob.NetworkJobResult<?> result) {
        Log.d(TAG, "Received " + result.getResourceType());
        if (result.getResourceType() == ResourceType.DASHBOARDS) {
            getDhisService().syncDashboardContents();
        }
        if (result.getResourceType() == ResourceType.DASHBOARDS_CONTENT) {
            getDhisService().pullDashboardImages(mImageNetworkPolicy,getContext());
        }
        if (result.getResourceType() == ResourceType.INTERPRETATIONS) {
            getDhisService().pullInterpretationImages(mImageNetworkPolicy,getContext());
        }
        if (result.getResourceType() == ResourceType.DASHBOARD_IMAGES) {
            getDhisService().syncInterpretations(SyncStrategy.DOWNLOAD_ALL);
        }
        if (result.getResourceType() == ResourceType.INTERPRETATION_IMAGES) {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean isSyncing() {
        return mProgressBar.getVisibility() == View.VISIBLE;
    }

    private static class DashboardQuery implements Query<List<Dashboard>> {

        @Override
        public List<Dashboard> query(Context context) {
            List<Dashboard> dashboards = new Select()
                    .from(Dashboard.class)
                    .where(Condition.column(Dashboard$Table
                            .STATE).isNot(State.TO_DELETE.toString()))
                    .queryList();
            Collections.sort(dashboards, Dashboard.DISPLAY_NAME_COMPARATOR);
            return dashboards;
        }
    }
}
