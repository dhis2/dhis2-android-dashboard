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

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.hisp.dhis.android.dashboard.R;
import org.hisp.dhis.android.dashboard.api.models.UserAccount;
import org.hisp.dhis.android.dashboard.api.persistence.loaders.DbLoader;
import org.hisp.dhis.android.dashboard.api.persistence.loaders.Query;
import org.hisp.dhis.android.dashboard.ui.fragments.AccountFragment;
import org.hisp.dhis.android.dashboard.ui.fragments.SettingsFragment;
import org.hisp.dhis.android.dashboard.ui.fragments.dashboard.DashboardContainerFragment;
import org.hisp.dhis.android.dashboard.ui.fragments.interpretation.InterpretationContainerFragment;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.hisp.dhis.android.dashboard.api.utils.Preconditions.isNull;

public class MenuActivity extends BaseActivity
        implements OnNavigationItemSelectedListener, DrawerListener,
        INavigationCallback, LoaderManager.LoaderCallbacks<UserAccount> {

    private static final int LOADER_ID = 589352;

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @Bind(R.id.navigation_view)
    NavigationView mNavigationView;

    @Bind(R.id.drawer_user_name)
    TextView mUsername;

    @Bind(R.id.drawer_user_info)
    TextView mUserInfo;

    Runnable mPendingRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ButterKnife.bind(this);

        mDrawerLayout.setDrawerListener(this);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (savedInstanceState == null) {
            attachFragment(new DashboardContainerFragment());
        }

        getSupportLoaderManager().initLoader(LOADER_ID, savedInstanceState, this);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.menu_dashboard_item: {
                attachFragmentDelayed(new DashboardContainerFragment());
                break;
            }
            case R.id.menu_interpretations_item: {
                attachFragmentDelayed(new InterpretationContainerFragment());
                break;
            }
            case R.id.menu_account_item: {
                attachFragmentDelayed(new AccountFragment());
                break;
            }
            case R.id.menu_settings_item: {
                attachFragmentDelayed(new SettingsFragment());
                break;
            }
            /* case R.id.menu_about_item: {
                getDhisController().invalidateSession();
                startActivity(new Intent(this, LauncherActivity.class));
                finish();
                break;
            } */
        }
        menuItem.setChecked(true);
        mDrawerLayout.closeDrawers();
        return true;
    }

    @Override
    public void onDrawerOpened(View drawerView) {
        mPendingRunnable = null;
    }

    @Override
    public void onDrawerClosed(View drawerView) {
        if (mPendingRunnable != null) {
            new Handler().post(mPendingRunnable);
        }
        mPendingRunnable = null;
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
        // stub implementation
    }

    @Override
    public void onDrawerStateChanged(int newState) {
        // stub implementation
    }

    @Override
    public void toggleNavigationDrawer() {
        if (!mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerLayout.openDrawer(Gravity.LEFT);
        } else {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }
    }

    @OnClick(R.id.drawer_user_avatar)
    @SuppressWarnings("unused")
    public void onClick() {
        MenuItem item = mNavigationView.getMenu()
                .findItem(R.id.menu_account_item);
        onNavigationItemSelected(item);
    }


    private void attachFragmentDelayed(final Fragment fragment) {
        isNull(fragment, "Fragment must not be null");

        mPendingRunnable = new Runnable() {

            @Override
            public void run() {
                attachFragment(fragment);
            }
        };
    }

    private void attachFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }

    @Override
    public Loader<UserAccount> onCreateLoader(int id, Bundle args) {
        List<DbLoader.TrackedTable> trackedTables = Arrays.asList(
                new DbLoader.TrackedTable(UserAccount.class));
        return new DbLoader<>(getApplicationContext(),
                trackedTables, new UserAccountQuery());
    }

    @Override
    public void onLoadFinished(Loader<UserAccount> loader, UserAccount user) {
        if (loader != null && loader.getId() == LOADER_ID && user != null) {
            mUsername.setText(user.getDisplayName());
            mUserInfo.setText(user.getEmail());
        }
    }

    @Override
    public void onLoaderReset(Loader<UserAccount> loader) {
        // stub implementation
    }

    private static class UserAccountQuery implements Query<UserAccount> {

        @Override
        public UserAccount query(Context context) {
            return UserAccount.getCurrentUserAccountFromDb();
        }
    }
}