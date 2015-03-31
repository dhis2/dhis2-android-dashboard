package org.hisp.dhis.mobile.datacapture.ui.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.hisp.dhis.mobile.datacapture.R;
import org.hisp.dhis.mobile.datacapture.ui.fragments.DashboardViewPagerFragment;
import org.hisp.dhis.mobile.datacapture.ui.fragments.InterpretationFragment;
import org.hisp.dhis.mobile.datacapture.ui.fragments.ReportViewPagerFragment;
import org.hisp.dhis.mobile.datacapture.ui.fragments.StubFragment;
import org.hisp.dhis.mobile.datacapture.ui.fragments.aggregate.AggregateReportFragment;
import org.hisp.dhis.mobile.datacapture.ui.navigation.NavigationAdapter;
import org.hisp.dhis.mobile.datacapture.ui.navigation.NavigationItem;
import org.hisp.dhis.mobile.datacapture.ui.navigation.NavigationMenuItem;
import org.hisp.dhis.mobile.datacapture.ui.navigation.NavigationSection;
import org.hisp.dhis.mobile.datacapture.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends ActionBarActivity {
    private static final int ANALYTICS_SECTION = 100;
    private static final int DASHBOARD_MENU_ITEM = 101;
    private static final int INTERPRETATIONS_MENU_ITEM = 102;

    private static final int DATA_ENTRY_SECTION = 200;
    private static final int AGGREGATE_REPORT_MENU_ITEM = 201;
    private static final int SINGLE_EVENT_MENU_ITEM = 202;
    private static final int REPORTS_MENU_ITEM = 203;

    private static final int PROFILE_SECTION = 300;
    private static final int MY_ACCOUNT_MENU_ITEM = 301;
    private static final int MESSAGES_MENU_ITEM = 302;
    private static final int LOG_OUT_MENU_ITEM = 303;

    private static final int OTHER_SECTION = 400;
    private static final int ABOUT_MENU_ITEM = 401;

    private DrawerLayout mDrawerLayout;
    private ListView mNavigationListView;
    private ActionBarDrawerToggle mDrawerToggle;
    private Runnable mPendingRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationListView = (ListView) findViewById(R.id.left_drawer);

        List<NavigationItem> items = buildNavigationItems();
        mNavigationListView.setAdapter(new NavigationAdapter(items));
        mNavigationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,
                                    int position, long id) {
                selectItem(id);
            }
        });

        if (mDrawerLayout != null) {
            mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
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

            mDrawerLayout.setDrawerListener(mDrawerToggle);
            mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
            setActionBarUpEnabled(true);
        } else {
            setActionBarUpEnabled(false);
        }
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
        if (mDrawerToggle != null && mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mDrawerLayout != null && mDrawerToggle != null) {
            mDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

    private List<NavigationItem> buildNavigationItems() {
        ArrayList<NavigationItem> items = new ArrayList<NavigationItem>();
        LayoutInflater inflater = LayoutInflater.from(this);
        items.add(new NavigationSection(inflater, ANALYTICS_SECTION, R.string.analytics));
        items.add(new NavigationMenuItem(inflater, DASHBOARD_MENU_ITEM,
                R.string.dashboard, R.drawable.ic_dashboard));
        items.add(new NavigationMenuItem(inflater, INTERPRETATIONS_MENU_ITEM,
                R.string.interpretations, R.drawable.ic_interpretations));

        /*
        items.add(new NavigationSection(inflater, DATA_ENTRY_SECTION, R.string.data_entry_section));
        items.add(new NavigationMenuItem(inflater, AGGREGATE_REPORT_MENU_ITEM,
                R.string.aggregate_report, R.drawable.ic_aggregate_report));
        items.add(new NavigationMenuItem(inflater, SINGLE_EVENT_MENU_ITEM,
                R.string.single_event, R.drawable.ic_single_event));
        items.add(new NavigationMenuItem(inflater, REPORTS_MENU_ITEM,
                R.string.reports, R.drawable.ic_reports));
        */
        items.add(new NavigationSection(inflater, PROFILE_SECTION, R.string.profile_section));
        items.add(new NavigationMenuItem(inflater, MY_ACCOUNT_MENU_ITEM,
                R.string.my_account, R.drawable.ic_username));
        /*
        items.add(new NavigationMenuItem(inflater, MESSAGES_MENU_ITEM,
                R.string.messages, R.drawable.ic_messages));
        */
        items.add(new NavigationMenuItem(inflater, LOG_OUT_MENU_ITEM,
                R.string.log_out, R.drawable.ic_log_out));

        items.add(new NavigationSection(inflater, OTHER_SECTION, R.string.other));
        items.add(new NavigationMenuItem(inflater, ABOUT_MENU_ITEM,
                R.string.about, R.drawable.ic_about));

        return items;
    }

    private void selectItem(long id) {
        final Fragment fragment = findFragmentById(id);

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

    private Fragment findFragmentById(long id) {
        int number;
        if (id == DASHBOARD_MENU_ITEM) {
            number = DASHBOARD_MENU_ITEM;
        } else if (id == INTERPRETATIONS_MENU_ITEM) {
            number = INTERPRETATIONS_MENU_ITEM;
        } else if (id == AGGREGATE_REPORT_MENU_ITEM) {
            number = AGGREGATE_REPORT_MENU_ITEM;
            // } else if (id == SINGLE_EVENT_MENU_ITEM) {
            //    number = SINGLE_EVENT_MENU_ITEM;
        } else if (id == REPORTS_MENU_ITEM) {
            number = REPORTS_MENU_ITEM;
        } else if (id == MY_ACCOUNT_MENU_ITEM) {
            number = MY_ACCOUNT_MENU_ITEM;
        } else if (id == MESSAGES_MENU_ITEM) {
            number = MESSAGES_MENU_ITEM;
        } else if (id == LOG_OUT_MENU_ITEM) {
            number = LOG_OUT_MENU_ITEM;
        } else if (id == ABOUT_MENU_ITEM) {
            number = ABOUT_MENU_ITEM;
        } else {
            number = 0;
        }

        Fragment fragment;
        if (id == AGGREGATE_REPORT_MENU_ITEM) {
            fragment = new AggregateReportFragment();
        } else if (id == LOG_OUT_MENU_ITEM) {
            fragment = new StubFragment();
            PreferenceUtils.remove(this, LoginActivity.SERVER_URL);
            PreferenceUtils.remove(this, LoginActivity.USER_CREDENTIALS);
            startActivity(new Intent(this, LoginActivity.class));
        } else if (id == DASHBOARD_MENU_ITEM) {
            fragment = new DashboardViewPagerFragment();
        } else if (id == INTERPRETATIONS_MENU_ITEM) {
            fragment = new InterpretationFragment();
        } else if (id == REPORTS_MENU_ITEM) {
            fragment = new ReportViewPagerFragment();
        } else {
            fragment = new StubFragment();
        }

        Bundle args = new Bundle();
        args.putInt(StubFragment.NUMBER_EXTRA, number);
        fragment.setArguments(args);
        return fragment;
    }

    private void attachFragment(Fragment fragment) {
        if (fragment == null) {
            throw new IllegalArgumentException("Fragment must not be null");
        }

        System.out.println("Attaching fragment: " + fragment);
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

    private void setActionBarUpEnabled(boolean flag) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(flag);
        getSupportActionBar().setHomeButtonEnabled(flag);
    }
}
