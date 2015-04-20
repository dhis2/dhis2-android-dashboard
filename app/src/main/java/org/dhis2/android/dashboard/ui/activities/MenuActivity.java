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

package org.dhis2.android.dashboard.ui.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.dhis2.android.dashboard.R;
import org.dhis2.android.dashboard.ui.fragments.dashboard.DashboardFragment;
import org.dhis2.android.dashboard.ui.navigation.NavigationAdapter;
import org.dhis2.android.dashboard.ui.navigation.NavigationItem;
import org.dhis2.android.dashboard.ui.navigation.NavigationMenuItem;
import org.dhis2.android.dashboard.ui.navigation.NavigationSection;

import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import butterknife.Optional;

import static org.dhis2.android.dashboard.api.utils.Preconditions.isNull;

public class MenuActivity extends BaseActivity {
    private static final int ANALYTICS = 200;
    private static final int DASHBOARD = 201;
    private static final int INTERPRETATIONS = 202;

    private static final int PROFILE_SECTION = 300;
    private static final int MY_ACCOUNT_MENU_ITEM = 301;
    private static final int LOG_OUT_MENU_ITEM = 302;

    private static final int OTHER_SECTION = 400;
    private static final int SETTINGS_MENU_ITEM = 401;
    private static final int ABOUT_MENU_ITEM = 402;

    @InjectView(R.id.toolbar) Toolbar mToolbar;
    @InjectView(R.id.drawer_layout) @Optional DrawerLayout mDrawerLayout;
    @InjectView(R.id.left_drawer) ListView mNavigationListView;

    ActionBarDrawerToggle mDrawerToggle;
    Runnable mPendingRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ButterKnife.inject(this);

        setSupportActionBar(mToolbar);
        setTitle(R.string.toolbar_title);

        List<NavigationItem> items = buildNavigationItems();
        mNavigationListView.setAdapter(new NavigationAdapter(items));
        if (mDrawerLayout != null) {
            mDrawerToggle = createToggle();
            mDrawerLayout.setDrawerListener(mDrawerToggle);
            mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(mDrawerLayout != null);
        getSupportActionBar().setHomeButtonEnabled(mDrawerLayout != null);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mDrawerLayout != null && mDrawerToggle != null) {
            mDrawerToggle.syncState();
        }

        if (savedInstanceState == null) {
            setDefaultMenuItemSelected();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return (mDrawerToggle != null && mDrawerToggle.onOptionsItemSelected(item)) ||
                super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mDrawerLayout != null && mDrawerToggle != null) {
            mDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

    @OnItemClick(R.id.left_drawer)
    public void onMenuItemClick(long id) {
        Toast.makeText(getBaseContext(), "" + id, Toast.LENGTH_SHORT).show();
        final Fragment fragment = findFragmentById(id);

        if (id == LOG_OUT_MENU_ITEM) {
            getDhisService().logOutUser();
            startActivity(new Intent(this, LauncherActivity.class));
            finish();
        }

        if (id == ABOUT_MENU_ITEM) {
            getDhisManager().invalidateMetaData();
            startActivity(new Intent(this, LauncherActivity.class));
            finish();
        }

        if (mDrawerLayout != null) {
            mPendingRunnable = new Runnable() {

                @Override
                public void run() {
                    attachFragment(fragment);
                }
            };
            mDrawerLayout.closeDrawer(mNavigationListView);
        } else {
            attachFragment(fragment);
            supportInvalidateOptionsMenu();
        }
    }

    private void attachFragment(Fragment fragment) {
        isNull(fragment, "Fragment must not be null");
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }

    private void setDefaultMenuItemSelected() {
        if (mNavigationListView != null && mNavigationListView.getAdapter() != null) {
            ListAdapter adapter = mNavigationListView.getAdapter();
            for (int position = 0; position < adapter.getCount(); position++) {
                if (adapter.isEnabled(position)) {
                    mNavigationListView.setItemChecked(position, true);
                    Fragment fragment = findFragmentById(adapter.getItemId(position));
                    attachFragment(fragment);
                    break;
                }
            }
        }
    }

    private Fragment findFragmentById(long id) {
        if (id == DASHBOARD) {
            return new DashboardFragment();
        }
        return new Fragment();
    }

    private List<NavigationItem> buildNavigationItems() {
        LayoutInflater inflater = LayoutInflater.from(this);
        return Arrays.asList(
                new NavigationSection(inflater, ANALYTICS,
                        R.string.analytics),
                new NavigationMenuItem(inflater, DASHBOARD,
                        R.string.dashboard, R.mipmap.ic_dashboard),
                new NavigationMenuItem(inflater, INTERPRETATIONS,
                        R.string.interpretations, R.mipmap.ic_interpretations),

                new NavigationSection(inflater, PROFILE_SECTION,
                        R.string.profile_section),
                new NavigationMenuItem(inflater, MY_ACCOUNT_MENU_ITEM,
                        R.string.my_account, R.mipmap.ic_username),
                new NavigationMenuItem(inflater, LOG_OUT_MENU_ITEM,
                        R.string.log_out, R.mipmap.ic_log_out),

                new NavigationSection(inflater, OTHER_SECTION,
                        R.string.other),
                new NavigationMenuItem(inflater, SETTINGS_MENU_ITEM,
                        R.string.settings, R.mipmap.ic_settings),
                new NavigationMenuItem(inflater, ABOUT_MENU_ITEM,
                        R.string.about, R.mipmap.ic_about)
        );
    }

    private ActionBarDrawerToggle createToggle() {
        return new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.drawer_open, R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                if (mPendingRunnable != null) {
                    new Handler().post(mPendingRunnable);
                }
                mPendingRunnable = null;
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                mPendingRunnable = null;
            }
        };
    }
}