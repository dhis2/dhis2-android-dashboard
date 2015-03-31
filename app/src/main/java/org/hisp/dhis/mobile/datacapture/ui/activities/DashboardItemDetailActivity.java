package org.hisp.dhis.mobile.datacapture.ui.activities;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.hisp.dhis.mobile.datacapture.R;
import org.hisp.dhis.mobile.datacapture.io.handlers.DashboardItemHandler;
import org.hisp.dhis.mobile.datacapture.api.android.models.DbRow;
import org.hisp.dhis.mobile.datacapture.api.managers.DHISManager;
import org.hisp.dhis.mobile.datacapture.api.models.DashboardItem;
import org.hisp.dhis.mobile.datacapture.api.models.DashboardItemElement;
import org.hisp.dhis.mobile.datacapture.api.models.User;
import org.hisp.dhis.mobile.datacapture.io.AbsCursorLoader;
import org.hisp.dhis.mobile.datacapture.io.CursorHolder;
import org.hisp.dhis.mobile.datacapture.io.DBContract.DashboardItems;
import org.hisp.dhis.mobile.datacapture.ui.fragments.ImageViewFragment;
import org.hisp.dhis.mobile.datacapture.ui.fragments.ListViewFragment;
import org.hisp.dhis.mobile.datacapture.ui.fragments.WebViewFragment;

import java.util.List;

public class DashboardItemDetailActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<CursorHolder<DashboardItem>> {
    private static final String TAG = DashboardItemDetailActivity.class.getSimpleName();
    private static final int LOADER_ID = 12834514;

    private DashboardItem mDashboardItem;
    private boolean mCanShareInterpretation;

    public static Intent prepareIntent(FragmentActivity activity, int dashboardId) {
        Intent intent = new Intent(activity, DashboardItemDetailActivity.class);
        intent.putExtra(DashboardItems.DB_ID, dashboardId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_item_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mCanShareInterpretation = false;
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getSupportLoaderManager().initLoader(LOADER_ID, getIntent().getExtras(), this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dashboard_item_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<CursorHolder<DashboardItem>> onCreateLoader(int loaderId, Bundle args) {
        if (LOADER_ID == loaderId) {
            int id = args.getInt(DashboardItems.DB_ID);
            Uri uri = ContentUris.withAppendedId(DashboardItems.CONTENT_URI, id);
            return new ItemLoader(getBaseContext(), uri, DashboardItemHandler.PROJECTION, null, null, null);
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<CursorHolder<DashboardItem>> loader,
                               CursorHolder<DashboardItem> data) {
        if (loader != null && loader.getId() == LOADER_ID &&
                data != null && data.getData() != null) {
            mDashboardItem = data.getData();
            Log.d(TAG, "DashboardItem Type: " + mDashboardItem.getType());

            Fragment fragment = null;
            String serverUrl = DHISManager.getInstance().getServerUrl();

            if (DashboardItem.TYPE_MAP.equals(mDashboardItem.getType()) && mDashboardItem.getMap() != null) {
                setTitle(mDashboardItem.getMap().getName());
                String request = serverUrl + "/api/maps/" + mDashboardItem.getMap().getId() + "/data.png";
                fragment = ImageViewFragment.newInstance(request);
                mCanShareInterpretation = true;
            } else if (DashboardItem.TYPE_CHART.equals(mDashboardItem.getType()) && mDashboardItem.getChart() != null) {
                setTitle(mDashboardItem.getChart().getName());
                String request = serverUrl + "/api/charts/" + mDashboardItem.getChart().getId() + "/data.png";
                fragment = ImageViewFragment.newInstance(request);
                mCanShareInterpretation = true;
            } else if (DashboardItem.TYPE_EVENT_CHART.equals(mDashboardItem.getType()) && mDashboardItem.getEventChart() != null) {
                setTitle(mDashboardItem.getEventChart().getName());
                String request = serverUrl + "/api/eventCharts/" + mDashboardItem.getEventChart().getId() + "/data.png";
                fragment = ImageViewFragment.newInstance(request);
                mCanShareInterpretation = false;
            } else if (DashboardItem.TYPE_REPORT_TABLE.equals(mDashboardItem.getType()) && mDashboardItem.getReportTable() != null) {
                setTitle(mDashboardItem.getReportTable().getName());
                fragment = WebViewFragment.newInstance(mDashboardItem.getReportTable().getId());
                mCanShareInterpretation = true;
            } else if (DashboardItem.TYPE_USERS.equals(mDashboardItem.getType()) && mDashboardItem.getUsers() != null) {
                setTitle(getString(R.string.users));
                List<User> users = mDashboardItem.getUsers();
                String[] userNames = new String[users.size()];
                for (int i = 0; i < users.size(); i++) {
                    userNames[i] = users.get(i).getName();
                }
                fragment = ListViewFragment.newInstance(userNames);
                mCanShareInterpretation = false;
            } else if (DashboardItem.TYPE_RESOURCES.equals(mDashboardItem.getType()) && mDashboardItem.getResources() != null) {
                setTitle(getString(R.string.resources));
                List<DashboardItemElement> resources = mDashboardItem.getResources();
                String[] resourcesLabels = new String[resources.size()];
                for (int i = 0; i < resources.size(); i++) {
                    resourcesLabels[i] = resources.get(i).getName();
                }
                fragment = ListViewFragment.newInstance(resourcesLabels);
                mCanShareInterpretation = false;
            } else if (DashboardItem.TYPE_REPORTS.equals(mDashboardItem.getType()) && mDashboardItem.getReports() != null) {
                setTitle(getString(R.string.reports));
                List<DashboardItemElement> reports = mDashboardItem.getReports();
                String[] reportLabels = new String[reports.size()];
                for (int i = 0; i < reports.size(); i++) {
                    reportLabels[i] = reports.get(i).getName();
                }
                fragment = ListViewFragment.newInstance(reportLabels);
                mCanShareInterpretation = false;
            }

            supportInvalidateOptionsMenu();

            if (fragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.dashboard_item_detail_frame, fragment)
                        .commitAllowingStateLoss();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<CursorHolder<DashboardItem>> loader) {

    }

    private static class ItemLoader extends AbsCursorLoader<DashboardItem> {

        public ItemLoader(Context context, Uri uri, String[] projection,
                          String selection, String[] selectionArgs, String sortOrder) {
            super(context, uri, projection, selection, selectionArgs, sortOrder);
        }

        @Override
        protected DashboardItem readDataFromCursor(Cursor cursor) {
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                DbRow<DashboardItem> dbItem = DashboardItemHandler.fromCursor(cursor);
                return dbItem.getItem();
            } else {
                return null;
            }
        }
    }
}
