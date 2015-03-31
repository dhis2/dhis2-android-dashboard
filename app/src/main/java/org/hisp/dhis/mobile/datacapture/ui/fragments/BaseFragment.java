package org.hisp.dhis.mobile.datacapture.ui.fragments;

import android.support.v4.app.Fragment;

import org.hisp.dhis.mobile.datacapture.utils.BusProvider;

public class BaseFragment extends Fragment {

    @Override
    public void onPause() {
        super.onPause();

        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        BusProvider.getInstance().register(this);
    }
}
