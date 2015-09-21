package org.hisp.dhis.android.dashboard.ui.fragments;

import android.app.Activity;
import android.support.v4.app.DialogFragment;

import org.hisp.dhis.android.dashboard.BackgroundService;
import org.hisp.dhis.android.dashboard.ui.activities.INavigationCallback;
import org.hisp.dhis.android.dashboard.utils.EventBusProvider;

/**
 * Created by arazabishov on 7/28/15.
 */
public class BaseDialogFragment extends DialogFragment {
    INavigationCallback mNavCallback;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof INavigationCallback) {
            mNavCallback = (INavigationCallback) activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mNavCallback = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBusProvider.unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBusProvider.register(this);
    }

    public void toggleNavigationDrawer() {
        if (mNavCallback != null) {
            mNavCallback.toggleNavigationDrawer();
        } else {
            throw new UnsupportedOperationException("The fragment must be attached to Activity " +
                    "which implements INavigationCallback interface");
        }
    }

    public void onBackPressed() {
        if (isAdded()) {
            getActivity().onBackPressed();
        }
    }
}
