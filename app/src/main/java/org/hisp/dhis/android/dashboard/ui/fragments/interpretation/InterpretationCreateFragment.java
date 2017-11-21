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

import static org.hisp.dhis.android.dashboard.api.models.Interpretation.createInterpretation;

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

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.dashboard.R;
import org.hisp.dhis.android.dashboard.api.models.DashboardElement;
import org.hisp.dhis.android.dashboard.api.models.DashboardElement$Table;
import org.hisp.dhis.android.dashboard.api.models.DashboardItem;
import org.hisp.dhis.android.dashboard.api.models.DashboardItem$Table;
import org.hisp.dhis.android.dashboard.api.models.Interpretation;
import org.hisp.dhis.android.dashboard.api.models.InterpretationElement;
import org.hisp.dhis.android.dashboard.api.models.User;
import org.hisp.dhis.android.dashboard.api.models.User$Table;
import org.hisp.dhis.android.dashboard.api.models.UserAccount;
import org.hisp.dhis.android.dashboard.api.models.meta.State;
import org.hisp.dhis.android.dashboard.api.utils.EventBusProvider;
import org.hisp.dhis.android.dashboard.api.utils.SyncStrategy;
import org.hisp.dhis.android.dashboard.ui.events.UiEvent;
import org.hisp.dhis.android.dashboard.ui.fragments.BaseDialogFragment;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Fragment responsible for creation of new dashboards.
 */
public final class InterpretationCreateFragment extends BaseDialogFragment {
    private static final String TAG = InterpretationCreateFragment.class.getSimpleName();

    @Bind(R.id.dialog_label)
    TextView mDialogLabel;

    @Bind(R.id.interpretation_text)
    EditText mInterpretationText;

    DashboardItem mDashboardItem;

    public static InterpretationCreateFragment newInstance(long itemId) {
        Bundle args = new Bundle();
        args.putLong(DashboardItem$Table.ID, itemId);

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

        long dashboardItemId = getArguments().getLong(DashboardItem$Table.ID);
        mDashboardItem = new Select()
                .from(DashboardItem.class)
                .where(Condition.column(DashboardItem$Table
                        .ID).is(dashboardItemId))
                .querySingle();

        List<DashboardElement> elements = new Select()
                .from(DashboardElement.class)
                .where(Condition.column(DashboardElement$Table
                        .DASHBOARDITEM_DASHBOARDITEM).is(dashboardItemId))
                .and(Condition.column(DashboardElement$Table
                        .STATE).isNot(State.TO_DELETE.toString()))
                .queryList();

        mDashboardItem.setDashboardElements(elements);
        mDialogLabel.setText(getString(R.string.create_interpretation));
    }

    @OnClick({R.id.close_dialog_button, R.id.cancel_interpretation_create, R.id.create_interpretation})
    @SuppressWarnings("unused")
    public void onButtonClicked(View view) {
        if (view.getId() == R.id.create_interpretation) {
            // read user
            UserAccount userAccount = UserAccount
                    .getCurrentUserAccountFromDb();
            User user = new Select()
                    .from(User.class)
                    .where(Condition.column(User$Table
                            .UID).is(userAccount.getUId()))
                    .querySingle();

            // create interpretation
            Interpretation interpretation = createInterpretation(mDashboardItem,
                    user, mInterpretationText.getText().toString());
            List<InterpretationElement> elements = interpretation
                    .getInterpretationElements();

            // save interpretation
            interpretation.save();
            if (elements != null && !elements.isEmpty()) {
                for (InterpretationElement element : elements) {
                    // save corresponding interpretation elements
                    element.save();
                }
            }

            if (isDhisServiceBound()) {
                getDhisService().syncInterpretations(SyncStrategy.DOWNLOAD_ONLY_NEW);
                EventBusProvider.post(new UiEvent(UiEvent.UiEventType.SYNC_INTERPRETATIONS));
            }

            Toast.makeText(getActivity(),
                    getString(R.string.successfully_created_interpretation), Toast.LENGTH_SHORT).show();
        }
        dismiss();
    }

    public void show(FragmentManager manager) {
        super.show(manager, TAG);
    }
}
