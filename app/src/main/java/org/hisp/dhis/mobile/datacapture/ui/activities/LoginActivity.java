package org.hisp.dhis.mobile.datacapture.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.squareup.otto.Subscribe;

import org.hisp.dhis.mobile.datacapture.utils.BusProvider;
import org.hisp.dhis.mobile.datacapture.R;
import org.hisp.dhis.mobile.datacapture.api.android.events.LoginUserEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.OnUserLoginEvent;

public class LoginActivity extends BaseActivity {
    public static final String SERVER_URL = "serverUrl";
    public static final String USER_CREDENTIALS = "credentials";
    private static final String IS_PROGRESS_BAR_SHOWN = "isProgressBarShown";

    private View mViewsContainer;
    private EditText mServerUrl;
    private EditText mUsername;
    private EditText mPassword;
    private Button mLoginButton;
    private ProgressBar mProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setUpViews();

        mServerUrl.setText("https://apps.dhis2.org/demo/");
        mUsername.setText("admin");
        mPassword.setText("district");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(IS_PROGRESS_BAR_SHOWN, mProgressBar.isShown());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null &&
                savedInstanceState.getBoolean(IS_PROGRESS_BAR_SHOWN, false)) {
            mViewsContainer.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mViewsContainer.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        }

        super.onRestoreInstanceState(savedInstanceState);
    }

    private void setUpViews() {
        mViewsContainer = findViewById(R.id.login_views_container);
        mServerUrl = (EditText) findViewById(R.id.server_url);
        mUsername = (EditText) findViewById(R.id.username);
        mPassword = (EditText) findViewById(R.id.password);
        mLoginButton = (Button) findViewById(R.id.login_button);

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.GONE);

        TextWatcher textWatcher = new TextWatcher() {

            @Override
            public void afterTextChanged(Editable edit) {
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1,
                                      int arg2, int arg3) {
                checkEditTextFields();
            }
        };

        mServerUrl.addTextChangedListener(textWatcher);
        mUsername.addTextChangedListener(textWatcher);
        mPassword.addTextChangedListener(textWatcher);

        checkEditTextFields();

        mLoginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }

    private void checkEditTextFields() {
        String tempUrl = mServerUrl.getText().toString();
        String tempUsername = mUsername.getText().toString();
        String tempPassword = mPassword.getText().toString();

        if (tempUrl.equals("") || tempUsername.equals("") || tempPassword.equals("")) {
            mLoginButton.setEnabled(false);
        } else {
            mLoginButton.setEnabled(true);
        }
    }

    private void loginUser() {
        showProgress();

        LoginUserEvent event = new LoginUserEvent();
        event.setServerUrl(mServerUrl.getText().toString());
        event.setUsername(mUsername.getText().toString());
        event.setPassword(mPassword.getText().toString());

        BusProvider.getInstance().post(event);
    }

    @Subscribe
    public void onUserLogin(OnUserLoginEvent event) {
        if (event.getResponseHolder().getItem() != null) {
            startActivity(new Intent(this, MenuActivity.class));
        } else {
            hideProgress();
            String message = "Failure";
            event.getResponseHolder().getException().printStackTrace();
        }
        // Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void showProgress() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.out_up);
        mViewsContainer.startAnimation(anim);
        mViewsContainer.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.in_down);
        mViewsContainer.startAnimation(anim);
        mViewsContainer.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }
}
