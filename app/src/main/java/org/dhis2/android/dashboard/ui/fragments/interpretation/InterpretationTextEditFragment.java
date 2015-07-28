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

package org.dhis2.android.dashboard.ui.fragments.interpretation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.dhis2.android.dashboard.R;
import org.dhis2.android.dashboard.api.models.Interpretation;
import org.dhis2.android.dashboard.api.models.Interpretation$Table;
import org.dhis2.android.dashboard.api.utils.EventBusProvider;
import org.dhis2.android.dashboard.ui.events.UiEvent;
import org.dhis2.android.dashboard.ui.fragments.BaseDialogFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Handles editing (changing text) of given interpretation.
 */
public final class InterpretationTextEditFragment extends BaseDialogFragment {
    private static final String TAG = InterpretationTextEditFragment.class.getSimpleName();

    @Bind(R.id.dialog_label)
    TextView mDialogLabel;

    @Bind(R.id.interpretation_text)
    EditText mInterpretationText;

    Interpretation mInterpretation;

    public static InterpretationTextEditFragment newInstance(long interpretationId) {
        Bundle args = new Bundle();
        args.putLong(Interpretation$Table.ID, interpretationId);

        InterpretationTextEditFragment fragment = new InterpretationTextEditFragment();
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
        return inflater.inflate(R.layout.fragment_dialog_interpretation_text_edit, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        mInterpretation = new Select()
                .from(Interpretation.class)
                .where(Condition.column(Interpretation$Table
                        .ID).is(getArguments().getLong(Interpretation$Table.ID)))
                .querySingle();

        mDialogLabel.setText(getString(R.string.interpretation_text));
        mInterpretationText.setText(mInterpretation.getText());
    }


    @OnClick({
            R.id.close_dialog_button,
            R.id.cancel_interpretation_text_edit,
            R.id.update_interpretation_text
    })
    @SuppressWarnings("unused")
    public void onButtonClick(View view) {
        switch (view.getId()) {
            case R.id.update_interpretation_text: {
                mInterpretation.updateInterpretation(
                        mInterpretationText.getText().toString());

                if (isDhisServiceBound()) {
                    getDhisService().syncInterpretations();
                    EventBusProvider.post(new UiEvent(UiEvent
                            .UiEventType.SYNC_INTERPRETATIONS));
                }
                break;
            }
        }

        dismiss();
    }

    public void show(FragmentManager manager) {
        super.show(manager, TAG);
    }
}
