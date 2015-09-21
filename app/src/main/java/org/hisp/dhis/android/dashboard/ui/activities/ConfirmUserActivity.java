/*
 * Copyright (c) 2015, University of Oslo
 * All rights reserved.
 *
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

package org.hisp.dhis.android.dashboard.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;

import com.squareup.otto.Subscribe;

import org.hisp.dhis.android.dashboard.DhisApplication;
import org.hisp.dhis.android.dashboard.DhisService;
import org.hisp.dhis.android.dashboard.R;
import org.hisp.dhis.android.dashboard.job.NetworkJob;
import org.hisp.dhis.android.dashboard.ui.events.UiEvent;
import org.hisp.dhis.android.sdk.core.persistence.models.common.meta.Credentials;
import org.hisp.dhis.android.sdk.core.persistence.preferences.ResourceType;
import org.hisp.dhis.android.sdk.models.user.UserAccount;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

import static org.hisp.dhis.android.dashboard.utils.TextUtils.isEmpty;

public class ConfirmUserActivity extends AppCompatActivity {
    private static final String IS_LOADING = "state:isLoading";

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.progress_bar_circular_navy)
    CircularProgressBar mProgressBar;

    @Bind(R.id.re_log_in_views_container)
    View mViewsContainer;

    @Bind(R.id.username)
    EditText mUsername;

    @Bind(R.id.password)
    EditText mPassword;

    @Bind(R.id.re_log_in_button)
    Button mReLogIn;

    @Bind(R.id.delete_and_log_out_button)
    Button mLogOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_user);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        setTitle(R.string.app_name);

        hideProgress(false);
        checkEditTextFields();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(IS_LOADING, mProgressBar.isShown());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null &&
                savedInstanceState.getBoolean(IS_LOADING)) {
            showProgress(false);
        } else {
            hideProgress(false);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @OnTextChanged(value = {R.id.username, R.id.password})
    public void checkEditTextFields() {
        mReLogIn.setEnabled(
                !isEmpty(mUsername.getText()) &&
                        !isEmpty(mPassword.getText()));
    }

    @OnClick(R.id.re_log_in_button)
    @SuppressWarnings("unused")
    public void onReLogIn() {
        showProgress(true);

        String username = mUsername.getText().toString();
        String password = mPassword.getText().toString();

        DhisService.getInstance().confirmUser(
                new Credentials(username, password)
        );
    }

    @OnClick(R.id.delete_and_log_out_button)
    @SuppressWarnings("unused")
    public void deleteAndLogOut() {
        showProgress(true);
        DhisService.getInstance().logOutUser();
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onUiEventReceived(UiEvent event) {
        if (event.getEventType() == UiEvent.UiEventType.USER_LOG_OUT) {
            startActivity(new Intent(this, LauncherActivity.class));
            finish();
        }
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onNetworkResultReceived(NetworkJob.NetworkJobResult<UserAccount> result) {
        if (result.getResourceType() == ResourceType.USERS) {
            if (result.getResponseHolder().getApiException() == null) {
                startActivity(new Intent(this, LauncherActivity.class));
                finish();
            } else {
                hideProgress(true);
                ((DhisApplication) getApplication()).showApiExceptionMessage(
                        result.getResponseHolder().getApiException());
            }
        }
    }

    protected void showProgress(boolean withAnimation) {
        if (withAnimation) {
            Animation anim = AnimationUtils.loadAnimation(this, R.anim.out_up);
            mViewsContainer.startAnimation(anim);
        }
        mViewsContainer.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    protected void hideProgress(boolean withAnimation) {
        if (withAnimation) {
            Animation anim = AnimationUtils.loadAnimation(this, R.anim.in_down);
            mViewsContainer.startAnimation(anim);
        }

        mViewsContainer.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }
}

