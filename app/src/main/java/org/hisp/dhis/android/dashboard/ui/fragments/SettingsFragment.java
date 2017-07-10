package org.hisp.dhis.android.dashboard.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.squareup.otto.Subscribe;

import org.hisp.dhis.android.dashboard.R;
import org.hisp.dhis.android.dashboard.api.persistence.preferences.DateTimeManager;
import org.hisp.dhis.android.dashboard.api.persistence.preferences.SettingsManager;
import org.hisp.dhis.android.dashboard.ui.activities.LauncherActivity;
import org.hisp.dhis.android.dashboard.ui.events.UiEvent;
import org.hisp.dhis.android.dashboard.ui.views.FontEditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by arazabishov on 7/27/15.
 */
public final class SettingsFragment extends BaseFragment {
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    FontEditText widthEditText;
    FontEditText heightEditText;
    public static final String MINIMUM_WIDTH = "480";
    public static final String MINIMUM_HEIGHT = "320";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        widthEditText = (FontEditText) view.findViewById(R.id.update_width_edit);
        heightEditText =(FontEditText) view.findViewById(R.id.update_height_edit);
        widthEditText.addTextChangedListener(new CustomTextWatcher(SettingsManager.CHART_WIDTH, SettingsManager.DEFAULT_WIDTH, widthEditText));
        heightEditText.addTextChangedListener(new CustomTextWatcher(SettingsManager.CHART_HEIGHT, SettingsManager.DEFAULT_HEIGHT, heightEditText));
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        mToolbar.setNavigationIcon(R.mipmap.ic_menu);
        mToolbar.setTitle(R.string.settings);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleNavigationDrawer();
            }
        });
        String width = SettingsManager.getInstance(getContext()).getPreference((SettingsManager.CHART_WIDTH), SettingsManager.DEFAULT_WIDTH);
        String height = SettingsManager.getInstance(getContext()).getPreference((SettingsManager.CHART_HEIGHT), SettingsManager.DEFAULT_HEIGHT);
        widthEditText.setText(width);
        heightEditText.setText(height);

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

    private class CustomTextWatcher implements TextWatcher{

        final String preference;
        final int minimumValue;
        final EditText mEditText;
        public CustomTextWatcher(String preference, String minimumValue, EditText editText){
            this.preference = preference;
            this.minimumValue = Integer.parseInt(minimumValue);
            this.mEditText = editText;
        }
        @Override
        public void afterTextChanged(Editable s) {}

        @Override
        public void beforeTextChanged(CharSequence s, int start,
        int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start,
        int before, int count) {
            if(s.toString().length()>0) {
                if (Integer.parseInt(s.toString())>= minimumValue) {
                    mEditText.setError(null);
                    SettingsManager.getInstance(getContext()).setPreference(preference,
                            s.toString());
                }else{
                    mEditText.setError(getContext().getString(R.string.invalid_value)+" "+minimumValue);
                }
            }
        }
    }

}
