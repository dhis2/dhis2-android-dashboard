package org.hisp.dhis.mobile.datacapture.ui.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.hisp.dhis.mobile.datacapture.R;
import org.hisp.dhis.mobile.datacapture.api.android.models.ReportState;
import org.hisp.dhis.mobile.datacapture.ui.fragments.ReportsFragment;

public class ReportAdapter extends FragmentPagerAdapter {
    public static final int DRAFT_REPORTS = 0;
    public static final int PENDING_REPORTS = 1;
    private static final int TAB_COUNT = 2;

    private final String draftsString;
    private final String pendingString;

    public ReportAdapter(FragmentManager fm, Context context) {
        super(fm);
        draftsString = context.getString(R.string.drafts);
        pendingString = context.getString(R.string.pending);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case DRAFT_REPORTS:
                return ReportsFragment.newInstance(ReportState.OFFLINE);
            case PENDING_REPORTS:
                return ReportsFragment.newInstance(ReportState.PENDING);
            default:
                throw new IllegalArgumentException("No such ReportFragment");
        }
    }

    @Override
    public int getCount() {
        return TAB_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case DRAFT_REPORTS:
                return draftsString;
            case PENDING_REPORTS:
                return pendingString;
            default:
                throw new IllegalArgumentException("No such ReportFragment");
        }
    }
}
