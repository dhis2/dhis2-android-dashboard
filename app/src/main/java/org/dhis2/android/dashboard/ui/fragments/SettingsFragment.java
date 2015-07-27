package org.dhis2.android.dashboard.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Subscribe;

import org.dhis2.android.dashboard.R;
import org.dhis2.android.dashboard.ui.activities.LauncherActivity;
import org.dhis2.android.dashboard.ui.events.UiEvent;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by arazabishov on 7/27/15.
 */
public final class SettingsFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
    }

    @OnClick(R.id.delete_and_log_out_button)
    @SuppressWarnings("unused")
    public void onClick() {
        if (isDhisServiceBound()) {
            getDhisService().logOutUser();
        }
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onLogOut(UiEvent event) {
        if (isAdded() && getActivity() != null) {
            startActivity(new Intent(getActivity(), LauncherActivity.class));
            getActivity().finish();
        }
    }
}
