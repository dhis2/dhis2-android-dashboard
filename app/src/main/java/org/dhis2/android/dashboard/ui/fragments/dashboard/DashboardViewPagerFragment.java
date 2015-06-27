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
import android.support.design.widget.TabLayout;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.Model;

import org.dhis2.android.dashboard.R;
import org.dhis2.android.dashboard.api.models.Dashboard;
import org.dhis2.android.dashboard.api.models.Dashboard$Table;
import org.dhis2.android.dashboard.api.models.DashboardItemContent;
import org.dhis2.android.dashboard.api.models.DashboardItemContent$Table;
import org.dhis2.android.dashboard.api.models.meta.State;
import org.dhis2.android.dashboard.api.persistence.loaders.DbLoader;
import org.dhis2.android.dashboard.api.persistence.loaders.Query;
import org.dhis2.android.dashboard.ui.adapters.DashboardAdapter;
import org.dhis2.android.dashboard.ui.fragments.BaseFragment;
import org.dhis2.android.dashboard.ui.fragments.dashboard.DashboardItemAddFragment.OnOptionSelectedListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DashboardViewPagerFragment extends BaseFragment
        implements LoaderCallbacks<List<Dashboard>>, View.OnClickListener,
        Toolbar.OnMenuItemClickListener, ViewPager.OnPageChangeListener, OnOptionSelectedListener {
    private static final int LOADER_ID = 1233432;

    @InjectView(R.id.dashboard_tabs)
    TabLayout mTabs;

    @InjectView(R.id.dashboard_view_pager)
    ViewPager mViewPager;

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    DashboardAdapter mDashboardAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboards, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.inject(this, view);

        mToolbar.setNavigationIcon(R.mipmap.ic_menu);
        mToolbar.setNavigationOnClickListener(this);
        mToolbar.setTitle(R.string.dashboard);
        mToolbar.inflateMenu(R.menu.menu_dashboard_fragment);
        mToolbar.setOnMenuItemClickListener(this);

        mDashboardAdapter = new DashboardAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mDashboardAdapter);
        mViewPager.addOnPageChangeListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, savedInstanceState, this);
    }

    @Override
    public Loader<List<Dashboard>> onCreateLoader(int id, Bundle state) {
        if (id == LOADER_ID && isAdded()) {
            List<Class<? extends Model>> tablesToTrack = new ArrayList<>();
            tablesToTrack.add(Dashboard.class);
            return new DbLoader<>(getActivity().getApplicationContext(),
                    tablesToTrack, new DashboardQuery());
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
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // stub implementation
    }

    @Override
    public void onPageSelected(int position) {
        Dashboard dashboard = mDashboardAdapter.getDashboard(position);
        if (dashboard != null) {
            boolean isDashboardEditable = dashboard.getAccess().isManage();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    private void setDashboards(List<Dashboard> dashboards) {
        mDashboardAdapter.swapData(dashboards);
        mTabs.removeAllTabs();
        mTabs.setupWithViewPager(mViewPager);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_dashboard_item: {
                DashboardItemAddFragment.newInstance(this)
                        .show(getChildFragmentManager());
                return true;
            }
            case R.id.refresh: {
                getService().syncDashboards();
                return true;
            }
            case R.id.add_dashboard: {
                DashboardAddFragment fragment
                        = new DashboardAddFragment();
                fragment.show(getChildFragmentManager(), "someFragment");
                return true;
            }
            case R.id.manage_dashboard: {
                Dashboard dashboard = mDashboardAdapter
                        .getDashboard(mViewPager.getCurrentItem());
                DashboardManageFragment fragment
                        = DashboardManageFragment.newInstance(dashboard);
                fragment.show(getChildFragmentManager(), "someFragment2");
                return true;
            }
        }
        return false;
    }

    @Override
    public void onOptionSelected(int dialogId, int position, String id, String name) {
        if (dialogId == DashboardItemAddFragment.DIALOG_ID) {
            DashboardItemContent resource = new Select().from(DashboardItemContent.class)
                    .where(Condition.column(DashboardItemContent$Table.UID).is(id))
                    .querySingle();
            Dashboard dashboard = mDashboardAdapter
                    .getDashboard(mViewPager.getCurrentItem());
            dashboard.addItemContent(resource);
        }
    }

    private static class DashboardQuery implements Query<List<Dashboard>> {

        @Override
        public List<Dashboard> query(Context context) {
            List<Dashboard> dashboards = new Select()
                    .from(Dashboard.class)
                    .where(Condition.column(Dashboard$Table.STATE).isNot(State.TO_DELETE.toString()))
                    .queryList();
            Collections.sort(dashboards, Dashboard.DISPLAY_NAME_COMPARATOR);
            return dashboards;
        }
    }
}
