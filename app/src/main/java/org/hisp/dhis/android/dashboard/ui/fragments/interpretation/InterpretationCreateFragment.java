/*
 * Copyright (c) 2015, dhis2
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.dashboard.ui.fragments.interpretation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.hisp.dhis.android.dashboard.DhisService;
import org.hisp.dhis.android.dashboard.R;
import org.hisp.dhis.android.dashboard.ui.events.UiEvent;
import org.hisp.dhis.android.dashboard.ui.fragments.BaseDialogFragment;
import org.hisp.dhis.android.dashboard.utils.EventBusProvider;
import org.hisp.dhis.android.sdk.core.api.Dhis2;
import org.hisp.dhis.android.sdk.core.api.Models;
import org.hisp.dhis.android.sdk.models.common.meta.State;
import org.hisp.dhis.android.sdk.models.dashboard.DashboardElement;
import org.hisp.dhis.android.sdk.models.dashboard.DashboardItem;
import org.hisp.dhis.android.sdk.models.interpretation.Interpretation;
import org.hisp.dhis.android.sdk.models.interpretation.InterpretationElement;
import org.hisp.dhis.android.sdk.models.user.User;
import org.hisp.dhis.android.sdk.models.user.UserAccount;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Fragment responsible for creation of new dashboards.
 */
public final class InterpretationCreateFragment extends BaseDialogFragment {
    private static final String TAG = InterpretationCreateFragment.class.getSimpleName();
    private static final String DASHBOARD_ITEM_ID = "arg:dashboardItemId";

    @Bind(R.id.dialog_label)
    TextView mDialogLabel;

    @Bind(R.id.interpretation_text)
    EditText mInterpretationText;

    DashboardItem mDashboardItem;

    public static InterpretationCreateFragment newInstance(long itemId) {
        Bundle args = new Bundle();
        args.putLong(DASHBOARD_ITEM_ID, itemId);

        InterpretationCreateFragment fragment = new InterpretationCreateFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE,
                R.style.Theme_AppCompat_Light_Dialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_interpretation_create, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        long dashboardItemId = getArguments().getLong(DASHBOARD_ITEM_ID);
        mDashboardItem = Models.dashboardItems().query(dashboardItemId);

        List<DashboardElement> elements = Models.dashboardElements()
                .filter(mDashboardItem, State.TO_DELETE);
        mDashboardItem.setDashboardElements(elements);

        mDialogLabel.setText(getString(R.string.create_interpretation));
    }

    @OnClick({R.id.close_dialog_button, R.id.cancel_interpretation_create, R.id.create_interpretation})
    @SuppressWarnings("unused")
    public void onButtonClicked(View view) {
        if (view.getId() == R.id.create_interpretation) {
            // read user
            UserAccount userAccount = Dhis2.getCurrentUserAccount();
            User user = Models.users().query(userAccount.getUId());

            System.out.println("*** User: ***" + user);

            // create interpretation
            Interpretation interpretation = Dhis2.interpretations()
                    .createInterpretation(mDashboardItem, user, mInterpretationText.getText().toString());
            List<InterpretationElement> elements = Dhis2.interpretations()
                    .getInterpretationElements(interpretation);

            // save interpretation
            Models.interpretations().save(interpretation);
            if (elements != null && !elements.isEmpty()) {
                for (InterpretationElement element : elements) {
                    // save corresponding interpretation elements
                    Models.interpretationElements().save(element);
                }
            }

            DhisService.getInstance().syncInterpretations();
            EventBusProvider.post(new UiEvent(UiEvent.UiEventType.SYNC_INTERPRETATIONS));

            Toast.makeText(getActivity(),
                    getString(R.string.successfully_created_interpretation), Toast.LENGTH_SHORT).show();
        }
        dismiss();
    }

    public void show(FragmentManager manager) {
        super.show(manager, TAG);
    }
}
