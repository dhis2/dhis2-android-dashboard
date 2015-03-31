package org.hisp.dhis.mobile.datacapture.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.hisp.dhis.mobile.datacapture.api.android.models.DbRow;
import org.hisp.dhis.mobile.datacapture.api.models.Group;
import org.hisp.dhis.mobile.datacapture.ui.fragments.ReportGroupFragment;

import java.util.List;

public class ReportGroupAdapter extends FragmentPagerAdapter {
    private static final String EMPTY_TITLE = "";
    private List<DbRow<Group>> mGroups;

    public ReportGroupAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (mGroups != null && mGroups.size() > 0) {
            int groupId = mGroups.get(position).getId();
            return ReportGroupFragment.newInstance(groupId);
        } else {
            return null;
        }
    }

    @Override
    public int getCount() {
        if (mGroups != null) {
            return mGroups.size();
        } else {
            return 0;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (mGroups != null && mGroups.size() > 0) {
            return mGroups.get(position).getItem().getLabel();
        } else {
            return EMPTY_TITLE;
        }
    }

    public void swapData(List<DbRow<Group>> groups) {
        boolean hasToNotifyAdapter = mGroups != groups;
        mGroups = groups;

        if (hasToNotifyAdapter) {
            notifyDataSetChanged();
        }
    }
}
