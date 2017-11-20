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

import static org.hisp.dhis.android.dashboard.ui.fragments.dashboard.DashboardFragment.newInstance;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.hisp.dhis.android.dashboard.api.models.Dashboard;
import org.hisp.dhis.android.dashboard.ui.fragments.SyncingController;
import org.hisp.dhis.android.dashboard.ui.fragments.dashboard.DashboardFragment;

import java.util.List;

public class DashboardAdapter extends FragmentPagerAdapter {
    private static final String EMPTY_TITLE = "";
    private List<Dashboard> mDashboards;
    private SyncingController syncingController;

    public DashboardAdapter(FragmentManager fm,SyncingController syncingController) {
        super(fm);
        this.syncingController = syncingController;
    }

    @Override
    public Fragment getItem(int position) {
        if (mDashboards != null && mDashboards.size() > 0) {
            DashboardFragment dashboardFragment = DashboardFragment
                    .newInstance(getDashboard(position));

            dashboardFragment.setSyncingController(syncingController);

            return dashboardFragment;
        } else {
            return null;
        }
    }

    @Override
    public int getCount() {
        if (mDashboards != null) {
            return mDashboards.size();
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

    public Dashboard getDashboard(int position) {
        if (mDashboards != null && mDashboards.size() > 0) {
            return mDashboards.get(position);
        } else {
            return null;
        }
    }

    public Integer getDashboardPosition(Dashboard dashboard) {
        int position=-1;
        if (mDashboards != null && mDashboards.size() > 0) {
            for(Dashboard dashboardOnList:mDashboards){
                position++;
                if(dashboardOnList.equals(dashboard)) {
                    return position;
                }
            }
        }
        return null;
    }

    public void swapData(List<Dashboard> dashboards) {
        boolean hasToNotifyAdapter = mDashboards != dashboards;
        mDashboards = dashboards;

        if (hasToNotifyAdapter) {
            notifyDataSetChanged();
        }
    }
}
