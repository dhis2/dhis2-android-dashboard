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

package org.hisp.dhis.android.dashboard.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;

import org.hisp.dhis.android.dashboard.api.models.Access;
import org.hisp.dhis.android.dashboard.api.models.Dashboard;
import org.hisp.dhis.android.dashboard.ui.fragments.SyncingController;
import org.hisp.dhis.android.dashboard.ui.fragments.dashboard.DashboardFragment;

import java.util.List;

public class DashboardAdapter extends FragmentPagerAdapter {
    private static final String EMPTY_TITLE = "";
    private List<DashboardFragment> mDashboardFragments;
    private List<Dashboard> mDashboards;
    private SyncingController syncingController;
    private FragmentManager fragmentManager;

    public DashboardAdapter(FragmentManager fm, SyncingController syncingController,
            List<DashboardFragment> dashboardFragments,
            List<Dashboard> dashboards) {
        super(fm);
        fragmentManager = fm;
        this.syncingController = syncingController;
        mDashboards = dashboards;
        mDashboardFragments = dashboardFragments;
    }

    @Override
    public Fragment getItem(int position) {
        if (mDashboardFragments != null && mDashboardFragments.size() > 0) {

            return mDashboardFragments.get(position);
        } else {
            return null;
        }
    }

    @Override
    public int getCount() {
        if (mDashboardFragments != null) {
            return mDashboardFragments.size();
        } else {
            return 0;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (mDashboards != null && mDashboards.size() > 0) {
            return mDashboards.get(position).getName();
        } else {
            return EMPTY_TITLE;
        }
    }

    public long getDashboardID(int position) {
        if (mDashboards != null && mDashboards.size() > 0) {
            return mDashboards.get(position).getId();
        } else {
            return 0;
        }
    }

    public Access getDashboardAccess(int position) {
        if (mDashboards != null && mDashboards.size() > 0) {
            return mDashboards.get(position).getAccess();
        } else {
            return null;
        }
    }


    @Override
    public boolean isViewFromObject(View view, Object fragment) {
        return ((Fragment) fragment).getView() == view;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        assert (0 <= position && position < mDashboardFragments.size());
        Fragment fragment = getItem(position);
        FragmentTransaction trans = fragmentManager.beginTransaction();
        if (fragment == null) {
            fragment = fragmentManager.findFragmentByTag("fragment:" + position);
            if (fragment == null) {
                fragment = DashboardFragment
                        .newInstance(mDashboards.get(position));
                ((DashboardFragment) fragment).setSyncingController(syncingController);
            }
        }
        trans.remove(fragment);
        trans.commit();
        mDashboardFragments.set(position, null);
    }

    @Override
    public Fragment instantiateItem(ViewGroup container, int position) {
        Fragment fragment = getItem(position);
        FragmentTransaction trans = fragmentManager.beginTransaction();
        if (fragment == null) {
            fragment = fragmentManager.findFragmentByTag("fragment:" + position);
            if (fragment == null) {
                fragment = DashboardFragment
                        .newInstance(mDashboards.get(position));
                ((DashboardFragment) fragment).setSyncingController(syncingController);
            }
        }
        if (!fragment.isAdded()) {
            trans.add(container.getId(), fragment, "fragment:" + position);
            trans.commit();
        }
        return fragment;
    }

}
