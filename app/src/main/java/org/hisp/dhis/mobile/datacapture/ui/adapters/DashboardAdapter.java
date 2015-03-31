package org.hisp.dhis.mobile.datacapture.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.hisp.dhis.mobile.datacapture.api.android.models.DbRow;
import org.hisp.dhis.mobile.datacapture.api.models.Dashboard;
import org.hisp.dhis.mobile.datacapture.ui.fragments.DashboardFragment;

import java.util.List;

public class DashboardAdapter extends FragmentPagerAdapter {
    private static final String EMPTY_TITLE = "";
    private List<DbRow<Dashboard>> mDashboards;

    public DashboardAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (mDashboards != null && mDashboards.size() > 0) {
            return DashboardFragment.newInstance(mDashboards.get(position));
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
            return mDashboards.get(position).getItem().getName();
        } else {
            return EMPTY_TITLE;
        }
    }

    public DbRow<Dashboard> getDashboard(int position) {
        if (mDashboards != null && mDashboards.size() > 0) {
            return mDashboards.get(position);
        } else {
            return null;
        }
    }

    public void swapData(List<DbRow<Dashboard>> dashboards) {
        boolean hasToNotifyAdapter = mDashboards != dashboards;
        mDashboards = dashboards;

        if (hasToNotifyAdapter) {
            notifyDataSetChanged();
        }
    }
}
