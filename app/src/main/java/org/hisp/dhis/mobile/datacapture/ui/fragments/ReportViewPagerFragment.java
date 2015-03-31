package org.hisp.dhis.mobile.datacapture.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.hisp.dhis.mobile.datacapture.R;
import org.hisp.dhis.mobile.datacapture.ui.adapters.ReportAdapter;
import org.hisp.dhis.mobile.datacapture.ui.views.SlidingTabLayout;

public class ReportViewPagerFragment extends Fragment {
    private ViewPager mViewPager;
    private SlidingTabLayout mSlidingTabs;
    private ReportAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_report_view_pager, viewGroup, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mAdapter = new ReportAdapter(getChildFragmentManager(),
                getActivity().getBaseContext());
        mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
        mViewPager.setAdapter(mAdapter);

        final int red = getResources().getColor(R.color.red);
        final int blue = getResources().getColor(R.color.navy_blue);
        final int gray = getResources().getColor(R.color.darker_grey);

        mSlidingTabs = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabs.setDistributedEvenly(true);
        mSlidingTabs.setViewPager(mViewPager);
        mSlidingTabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {

            @Override
            public int getIndicatorColor(int position) {
                if (position == ReportAdapter.PENDING_REPORTS) {
                    return red;
                }
                return blue;
            }

            @Override
            public int getDividerColor(int position) {
                return gray;
            }
        });
    }
}
