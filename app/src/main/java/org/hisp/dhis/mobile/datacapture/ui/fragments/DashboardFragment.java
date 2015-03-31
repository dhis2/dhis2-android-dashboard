package org.hisp.dhis.mobile.datacapture.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import org.hisp.dhis.mobile.datacapture.utils.BusProvider;
import org.hisp.dhis.mobile.datacapture.R;
import org.hisp.dhis.mobile.datacapture.api.android.events.DashboardItemDeleteEvent;
import org.hisp.dhis.mobile.datacapture.io.handlers.DashboardItemHandler;
import org.hisp.dhis.mobile.datacapture.api.android.models.DbRow;
import org.hisp.dhis.mobile.datacapture.api.android.models.State;
import org.hisp.dhis.mobile.datacapture.api.models.Access;
import org.hisp.dhis.mobile.datacapture.api.models.Dashboard;
import org.hisp.dhis.mobile.datacapture.api.models.DashboardItem;
import org.hisp.dhis.mobile.datacapture.io.AbsCursorLoader;
import org.hisp.dhis.mobile.datacapture.io.CursorHolder;
import org.hisp.dhis.mobile.datacapture.io.DBContract.DashboardItems;
import org.hisp.dhis.mobile.datacapture.ui.activities.DashboardItemDetailActivity;
import org.hisp.dhis.mobile.datacapture.ui.adapters.DashboardItemAdapter;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends BaseFragment implements LoaderCallbacks<CursorHolder<List<DbRow<DashboardItem>>>>,
        DashboardItemAdapter.OnItemClickListener {
    private static final int LOADER_ID = 74734523;

    private static final String DB_ID_EXTRA = "dashboardId";
    private static final String ACCESS_DELETE_EXTRA = "accessDeleteExtra";
    private static final String ACCESS_UPDATE_EXTRA = "accessUpdateExtra";
    private static final String ACCESS_READ_EXTRA = "accessReadExtra";
    private static final String ACCESS_WRITE_EXTRA = "accessWriteExtra";
    private static final String ACCESS_MANAGE_EXTRA = "accessManagerExtra";
    private static final String ACCESS_EXTERNALIZE_EXTRA = "accessExternalizeExtra";

    private GridView mGridView;
    private DashboardItemAdapter mAdapter;

    public static DashboardFragment newInstance(DbRow<Dashboard> dashboard) {
        DashboardFragment fragment = new DashboardFragment();

        Bundle args = new Bundle();
        args.putInt(DB_ID_EXTRA, dashboard.getId());
        putAccessInBundle(args, dashboard.getItem().getAccess());

        fragment.setArguments(args);
        return fragment;
    }

    private static void putAccessInBundle(Bundle args, Access access) {
        args.putBoolean(ACCESS_DELETE_EXTRA, access.isDelete());
        args.putBoolean(ACCESS_UPDATE_EXTRA, access.isUpdate());
        args.putBoolean(ACCESS_READ_EXTRA, access.isRead());
        args.putBoolean(ACCESS_WRITE_EXTRA, access.isWrite());
        args.putBoolean(ACCESS_MANAGE_EXTRA, access.isManage());
        args.putBoolean(ACCESS_EXTERNALIZE_EXTRA, access.isExternalize());
    }

    private static Access getAccessFromBundle(Bundle args) {
        Access access = new Access();

        access.setDelete(args.getBoolean(ACCESS_DELETE_EXTRA));
        access.setUpdate(args.getBoolean(ACCESS_UPDATE_EXTRA));
        access.setRead(args.getBoolean(ACCESS_READ_EXTRA));
        access.setWrite(args.getBoolean(ACCESS_WRITE_EXTRA));
        access.setManage(args.getBoolean(ACCESS_MANAGE_EXTRA));
        access.setExternalize(args.getBoolean(ACCESS_EXTERNALIZE_EXTRA));

        return access;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle bundle) {
        return inflater.inflate(R.layout.fragment_dashboard, group, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Access access = getAccessFromBundle(getArguments());
        mAdapter = new DashboardItemAdapter(getActivity());
        mAdapter.setOnItemClickListener(this);
        mAdapter.setDashboardAccess(access);

        mGridView = (GridView) view.findViewById(R.id.grid);
        mGridView.setAdapter(mAdapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isAdded()) {
            getLoaderManager().initLoader(LOADER_ID, getArguments(), DashboardFragment.this);
        }
    }

    @Override
    public Loader<CursorHolder<List<DbRow<DashboardItem>>>> onCreateLoader(int id, Bundle args) {
        if (LOADER_ID == id) {
            int dashboardId = getArguments().getInt(DB_ID_EXTRA);
            String SELECTION = DashboardItems.DASHBOARD_DB_ID + " = " + dashboardId + " AND " +
                    DashboardItems.STATE + " != " + "'" + State.DELETING.toString() + "'" + " AND " +
                    DashboardItems.TYPE + " != " + "'" + DashboardItem.TYPE_REPORT_TABLES + "'" + " AND " +
                    DashboardItems.TYPE + " != " + "'" + DashboardItem.TYPE_MESSAGES + "'";
            return new ItemsLoader(getActivity(), DashboardItems.CONTENT_URI,
                    DashboardItemHandler.PROJECTION, SELECTION, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<CursorHolder<List<DbRow<DashboardItem>>>> loader,
                               CursorHolder<List<DbRow<DashboardItem>>> data) {
        if (loader != null && loader.getId() == LOADER_ID && mAdapter != null) {
            mAdapter.swapData(data.getData());
        }
    }

    @Override
    public void onLoaderReset(Loader<CursorHolder<List<DbRow<DashboardItem>>>> loader) {
        // reset the state of screen
    }

    @Override
    public void onItemClick(DbRow<DashboardItem> dbItem) {
        if (dbItem != null) {
            Intent intent = DashboardItemDetailActivity.prepareIntent(getActivity(),
                    dbItem.getId());
            startActivity(intent);
        }
    }

    @Override
    public void onItemShareInterpretation(DbRow<DashboardItem> dbItem) {
        Toast.makeText(getActivity(), "Item: " + dbItem.getId(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemDelete(DbRow<DashboardItem> dbItem) {
        final int dashboardId = getArguments().getInt(DB_ID_EXTRA);
        final int dashboardItemId = dbItem.getId();
        final DashboardItemDeleteEvent event = new DashboardItemDeleteEvent();

        event.setDashboardDbId(dashboardId);
        event.setDashboardItemDbId(dashboardItemId);

        BusProvider.getInstance().post(event);
    }

    public static class ItemsLoader extends AbsCursorLoader<List<DbRow<DashboardItem>>> {

        public ItemsLoader(Context context, Uri uri, String[] projection, String selection,
                           String[] selectionArgs, String sortOrder) {
            super(context, uri, projection, selection, selectionArgs, sortOrder);
        }

        @Override
        protected List<DbRow<DashboardItem>> readDataFromCursor(Cursor cursor) {
            List<DbRow<DashboardItem>> items = new ArrayList<>();

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();

                do {
                    DbRow<DashboardItem> item = DashboardItemHandler.fromCursor(cursor);
                    items.add(item);
                } while (cursor.moveToNext());
            }

            return items;
        }
    }
}
