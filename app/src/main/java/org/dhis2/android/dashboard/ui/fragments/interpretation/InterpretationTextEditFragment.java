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

import org.dhis2.android.dashboard.R;
import org.dhis2.android.dashboard.api.models.Interpretation;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;

import static org.dhis2.android.dashboard.api.utils.Preconditions.isNull;

/**
 * Handles editing (changing text) of given interpretation.
 */
public final class InterpretationTextEditFragment extends DialogFragment {
    private static final String TAG = InterpretationTextEditFragment.class.getSimpleName();

    @Bind(R.id.fragment_bar)
    View mFragmentBar;

    @Bind(R.id.fragment_bar_mode_editing)
    View mFragmentBarEditingMode;

    @Bind(R.id.dialog_label)
    TextView mDialogLabel;

    @Bind(R.id.action_name)
    TextView mActionName;

    @Bind(R.id.interpretation_text)
    EditText mInterpretationText;

    Interpretation mInterpretation;

    public static InterpretationTextEditFragment newInstance(Interpretation interpretation) {
        isNull(interpretation, "Interpretation object must not be null");

        InterpretationTextEditFragment fragment = new InterpretationTextEditFragment();
        fragment.mInterpretation = interpretation;
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

        mDialogLabel.setText(getString(R.string.interpretation_text));
        mActionName.setText(getString(R.string.edit_text));

        mInterpretationText.setText(mInterpretation.getText());
        setFragmentBarActionMode(false);
    }


    @OnClick({R.id.close_dialog_button, R.id.cancel_action, R.id.accept_action})
    @SuppressWarnings("unused")
    public void onButtonClick(View view) {
        switch (view.getId()) {
            case R.id.cancel_action: {
                mInterpretationText.setText(
                        mInterpretation.getText());
                mInterpretationText.clearFocus();
                break;
            }
            case R.id.accept_action: {
                mInterpretation.updateInterpretation(
                        mInterpretationText.getText().toString());
                mInterpretationText.clearFocus();
                break;
            }
            case R.id.close_dialog_button: {
                dismiss();
            }
        }
    }

    @OnFocusChange(R.id.interpretation_text)
    @SuppressWarnings("unused")
    public void onFocusChange(boolean hasFocus) {
        setFragmentBarActionMode(hasFocus);
    }

    /* set fragment bar in editing mode, by hiding standard
    layout and showing layout with actions*/
    void setFragmentBarActionMode(boolean enabled) {
        mFragmentBarEditingMode.setVisibility(enabled ? View.VISIBLE : View.GONE);
        mFragmentBar.setVisibility(enabled ? View.GONE : View.VISIBLE);
    }

    public void show(FragmentManager manager) {
        super.show(manager, TAG);
    }
}
