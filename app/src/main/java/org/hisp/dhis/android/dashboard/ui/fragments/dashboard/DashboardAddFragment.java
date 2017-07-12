/*
 * Copyright (c) 2015, University of Oslo
 *
 * All rights reserved.
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

package org.hisp.dhis.android.dashboard.ui.fragments.dashboard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.hisp.dhis.android.dashboard.R;
import org.hisp.dhis.android.dashboard.api.models.Dashboard;
import org.hisp.dhis.android.dashboard.api.utils.EventBusProvider;
import org.hisp.dhis.android.dashboard.ui.events.UiEvent;
import org.hisp.dhis.android.dashboard.ui.fragments.BaseDialogFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.hisp.dhis.android.dashboard.utils.TextUtils.isEmpty;

/**
 * Fragment responsible for creation of new dashboards.
 */
public final class DashboardAddFragment extends BaseDialogFragment {
    private static final String TAG = DashboardAddFragment.class.getSimpleName();

    @Bind(R.id.dialog_label)
    TextView mDialogLabel;

    @Bind(R.id.dashboard_name)
    EditText mDashboardName;

    @Bind(R.id.text_input_dashboard_name)
    TextInputLayout mTextInputLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE,
                R.style.Theme_AppCompat_Light_Dialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard_add, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        mDialogLabel.setText(getString(R.string.add_dashboard));
    }

    @OnClick({R.id.close_dialog_button, R.id.cancel_dashboard_add, R.id.save_dashboard})
    @SuppressWarnings("unused")
    public void onButtonClicked(View view) {
        if (view.getId() == R.id.save_dashboard) {
            boolean isEmptyName = isEmpty(mDashboardName.getText().toString().trim());
            String message = isEmptyName ? getString(R.string.enter_valid_name) : "";
            mTextInputLayout.setError(message);

            if (!isEmptyName) {
                Dashboard newDashboard = Dashboard
                        .createDashboard(mDashboardName.getText().toString());
                newDashboard.save();
                if (isDhisServiceBound()) {
                    getDhisService().syncDashboards();
                    EventBusProvider.post(new UiEvent(UiEvent.UiEventType.SYNC_DASHBOARDS));
                }
                dismiss();
            }
        } else {
            dismiss();
        }
    }

    public void show(FragmentManager manager) {
        super.show(manager, TAG);
    }
}
