package org.hisp.dhis.mobile.datacapture.ui.activities;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.hisp.dhis.mobile.datacapture.R;
import org.hisp.dhis.mobile.datacapture.api.android.events.DashboardDeleteEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.DashboardItemDeleteEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.DashboardUpdateEvent;
import org.hisp.dhis.mobile.datacapture.io.handlers.DashboardHandler;
import org.hisp.dhis.mobile.datacapture.io.handlers.DashboardItemHandler;
import org.hisp.dhis.mobile.datacapture.api.android.models.DbRow;
import org.hisp.dhis.mobile.datacapture.api.android.models.State;
import org.hisp.dhis.mobile.datacapture.api.models.Dashboard;
import org.hisp.dhis.mobile.datacapture.api.models.DashboardItem;
import org.hisp.dhis.mobile.datacapture.io.AbsCursorLoader;
import org.hisp.dhis.mobile.datacapture.io.CursorHolder;
import org.hisp.dhis.mobile.datacapture.io.DBContract.DashboardItems;
import org.hisp.dhis.mobile.datacapture.io.DBContract.Dashboards;
import org.hisp.dhis.mobile.datacapture.ui.adapters.DashboardEditAdapter;
import org.hisp.dhis.mobile.datacapture.utils.BusProvider;

import java.util.ArrayList;
import java.util.List;

public class DashboardEditActivity extends ActionBarActivity implements View.OnClickListener,
        DialogInterface.OnClickListener, DashboardEditAdapter.OnDeleteClickListener {
    private static final int DASHBOARD_LOADER_ID = 892675923;
    private static final int ITEM_LOADER_ID = 894023524;

    private DashboardListener mDashboardListener = new DashboardListener();
    private ItemListener mItemsListener = new ItemListener();

    private ListView mListView;
    private DashboardEditAdapter mAdapter;

    private Button mChangeNameButton;
    private Button mDeleteButton;

    private AlertDialog mDialog;
    private EditText mEditDashboardName;

    public static Intent prepareIntent(FragmentActivity activity,
                                       DbRow<Dashboard> dbItem) {
        Intent intent = new Intent(activity, DashboardEditActivity.class);
        intent.putExtra(Dashboards.DB_ID, dbItem.getId());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_edit);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mListView = (ListView) findViewById(R.id.list);
        mAdapter = new DashboardEditAdapter(this);

        mChangeNameButton = (Button) getLayoutInflater().inflate(R.layout.button_change_dashboard_name, null);
        mDeleteButton = (Button) getLayoutInflater().inflate(R.layout.button_delete_dashboard, null);

        mAdapter.setOnClickListener(this);
        mChangeNameButton.setOnClickListener(this);
        mDeleteButton.setOnClickListener(this);

        mListView.addHeaderView(mChangeNameButton);
        mListView.addFooterView(mDeleteButton);
        mListView.setAdapter(mAdapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        mEditDashboardName = new EditText(this);
        mEditDashboardName.setHint(R.string.enter_dashboard_name);

        builder.setTitle(R.string.enter_dashboard_name);
        builder.setView(mEditDashboardName);

        builder.setPositiveButton(android.R.string.ok, this);
        builder.setNegativeButton(android.R.string.cancel, this);

        mDialog = builder.create();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        getSupportLoaderManager().initLoader(DASHBOARD_LOADER_ID,
                getIntent().getExtras(), mDashboardListener);
        getSupportLoaderManager().initLoader(ITEM_LOADER_ID,
                getIntent().getExtras(), mItemsListener);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dashboard_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    private AlertDialog buildDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText editText = new EditText(this);

        editText.setHint(R.string.enter_dashboard_name);
        editText.setText(mChangeNameButton.getText());

        builder.setTitle(R.string.enter_dashboard_name);
        builder.setView(editText);

        builder.setPositiveButton(android.R.string.ok, this);
        builder.setNegativeButton(android.R.string.cancel, this);
        return builder.create();
    }
    */

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.button_change_dashboard_name) {
            mEditDashboardName.setText(mChangeNameButton.getText());
            mDialog.show();
        } else if (view.getId() == R.id.delete_dashboard_button) {
            deleteDashboard();
        }
    }

    // this OnClickListener is responsible for
    // handling events from dialog
    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (DialogInterface.BUTTON_POSITIVE == which) {
            DashboardUpdateEvent event = new DashboardUpdateEvent();
            int dashboardId = getIntent().getExtras().getInt(Dashboards.DB_ID);
            String name = mEditDashboardName.getText().toString();

            event.setDataBaseId(dashboardId);
            event.setName(name);

            BusProvider.getInstance().post(event);
        }
    }

    private void deleteDashboard() {
        int dashboardId = getIntent().getExtras().getInt(Dashboards.DB_ID);
        DashboardDeleteEvent event = new DashboardDeleteEvent();
        event.setDashboardDbId(dashboardId);
        BusProvider.getInstance().post(event);
        finish();
    }

    @Override
    public void onDeleteButtonClicked(DbRow<DashboardItem> item) {
        final int dashboardId = getIntent().getExtras().getInt(Dashboards.DB_ID);
        final int dashboardItemId = item.getId();
        final DashboardItemDeleteEvent event = new DashboardItemDeleteEvent();

        event.setDashboardDbId(dashboardId);
        event.setDashboardItemDbId(dashboardItemId);

        BusProvider.getInstance().post(event);
    }

    private static class DashboardLoader extends AbsCursorLoader<DbRow<Dashboard>> {

        public DashboardLoader(Context context, Uri uri, String[] projection,
                               String selection, String[] selectionArgs, String sortOrder) {
            super(context, uri, projection, selection, selectionArgs, sortOrder);
        }

        @Override
        protected DbRow<Dashboard> readDataFromCursor(Cursor dashboardCursor) {
            if (dashboardCursor != null && dashboardCursor.getCount() > 0) {
                dashboardCursor.moveToFirst();
                return DashboardHandler.fromCursor(dashboardCursor);
            } else {
                return null;
            }
        }
    }

    private static class ItemLoader extends AbsCursorLoader<List<DbRow<DashboardItem>>> {

        public ItemLoader(Context context, Uri uri, String[] projection,
                          String selection, String[] selectionArgs, String sortOrder) {
            super(context, uri, projection, selection, selectionArgs, sortOrder);
        }

        @Override
        protected List<DbRow<DashboardItem>> readDataFromCursor(Cursor cursor) {
            List<DbRow<DashboardItem>> items = new ArrayList<>();
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();

                do {
                    items.add(DashboardItemHandler.fromCursor(cursor));
                } while (cursor.moveToNext());
            }
            return items;
        }
    }

    ;

    private class DashboardListener implements LoaderCallbacks<CursorHolder<DbRow<Dashboard>>> {

        @Override
        public Loader<CursorHolder<DbRow<Dashboard>>> onCreateLoader(int id, Bundle args) {
            if (DASHBOARD_LOADER_ID == id) {
                int dashboardId = args.getInt(Dashboards.DB_ID);
                Uri uri = ContentUris.withAppendedId(Dashboards.CONTENT_URI, dashboardId);
                return new DashboardLoader(getBaseContext(), uri,
                        DashboardHandler.PROJECTION, null, null, null);
            } else {
                return null;
            }
        }

        @Override
        public void onLoadFinished(Loader<CursorHolder<DbRow<Dashboard>>> loader,
                                   CursorHolder<DbRow<Dashboard>> data) {
            if (loader != null && loader.getId() == DASHBOARD_LOADER_ID &&
                    data != null && data.getData() != null) {
                DbRow<Dashboard> dashboard = data.getData();
                mChangeNameButton.setText(dashboard.getItem().getName());
            }
        }

        @Override
        public void onLoaderReset(Loader<CursorHolder<DbRow<Dashboard>>> loader) {
        }
    }

    private class ItemListener implements LoaderCallbacks<CursorHolder<List<DbRow<DashboardItem>>>> {

        @Override
        public Loader<CursorHolder<List<DbRow<DashboardItem>>>> onCreateLoader(int id, Bundle args) {
            if (ITEM_LOADER_ID == id) {
                int dashboardId = args.getInt(Dashboards.DB_ID);
                final String SELECTION = DashboardItems.DASHBOARD_DB_ID + " = " + dashboardId + " AND " +
                        DashboardItems.STATE + " != " + "'" + State.DELETING.toString() + "'" + " AND " +
                        DashboardItems.TYPE + " != " + "'" + DashboardItems.REPORT_TABLES + "'" + " AND " +
                        DashboardItems.TYPE + " != " + "'" + DashboardItems.MESSAGES + "'";
                return new ItemLoader(getBaseContext(), DashboardItems.CONTENT_URI,
                        DashboardItemHandler.PROJECTION, SELECTION, null, null);
            } else {
                return null;
            }
        }

        @Override
        public void onLoadFinished(Loader<CursorHolder<List<DbRow<DashboardItem>>>> loader,
                                   CursorHolder<List<DbRow<DashboardItem>>> data) {
            if (loader != null && ITEM_LOADER_ID == loader.getId() &&
                    data != null && data.getData() != null) {
                mAdapter.swapData(data.getData());
            }
        }

        @Override
        public void onLoaderReset(Loader<CursorHolder<List<DbRow<DashboardItem>>>> loader) { }
    };
}
