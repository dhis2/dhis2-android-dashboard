package org.dhis2.android.dashboard.ui.fragments;

import android.app.Activity;
import android.support.v4.app.DialogFragment;

import org.dhis2.android.dashboard.DhisService;
import org.dhis2.android.dashboard.api.utils.EventBusProvider;
import org.dhis2.android.dashboard.ui.activities.BaseActivity;
import org.dhis2.android.dashboard.ui.activities.INavigationCallback;

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

    public DhisService getDhisService() {
        if (isAdded() && getActivity() instanceof BaseActivity) {
            return ((BaseActivity) getActivity()).getDhisService();
        } else {
            throw new UnsupportedOperationException("The fragment must be attached to Activity " +
                    "which extends BaseActivity");
        }
    }

    public boolean isDhisServiceBound() {
        if (isAdded() && getActivity() instanceof BaseActivity) {
            return ((BaseActivity) getActivity()).isDhisServiceBound();
        } else {
            throw new UnsupportedOperationException("The fragment must be attached to Activity " +
                    "which extends BaseActivity");
        }
    }
}
