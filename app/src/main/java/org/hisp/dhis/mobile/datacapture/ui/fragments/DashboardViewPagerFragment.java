package org.hisp.dhis.mobile.datacapture.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import org.hisp.dhis.mobile.datacapture.api.android.models.DbRow;
import org.hisp.dhis.mobile.datacapture.utils.BusProvider;
import org.hisp.dhis.mobile.datacapture.R;
import org.hisp.dhis.mobile.datacapture.api.android.events.DashboardCreateEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.DashboardSyncEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.OnDashboardCreateEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.OnDashboardsSyncedEvent;
import org.hisp.dhis.mobile.datacapture.io.handlers.DashboardHandler;
import org.hisp.dhis.mobile.datacapture.api.android.models.ResponseHolder;
import org.hisp.dhis.mobile.datacapture.api.android.models.State;
import org.hisp.dhis.mobile.datacapture.api.models.Dashboard;
import org.hisp.dhis.mobile.datacapture.io.AbsCursorLoader;
import org.hisp.dhis.mobile.datacapture.io.CursorHolder;
import org.hisp.dhis.mobile.datacapture.io.DBContract.Dashboards;
import org.hisp.dhis.mobile.datacapture.ui.activities.DashboardEditActivity;
import org.hisp.dhis.mobile.datacapture.ui.adapters.DashboardAdapter;
import org.hisp.dhis.mobile.datacapture.ui.views.FloatingActionButton;
import org.hisp.dhis.mobile.datacapture.ui.views.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class DashboardViewPagerFragment extends BaseFragment
        implements LoaderManager.LoaderCallbacks<CursorHolder<List<DbRow<Dashboard>>>>,
        ViewPager.OnPageChangeListener, View.OnClickListener, EditDialogFragment.EditDialogListener {
    private static final int LOADER_ID = 826752394;
    private static final String STATE_PROGRESS = "stateProgress";

    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    private DashboardAdapter mAdapter;
    private FloatingActionButton mEditButton;
    private SmoothProgressBar mProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_dashboard_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.refresh) {
            BusProvider.getInstance().post(new DashboardSyncEvent());
            mProgressBar.setVisibility(View.VISIBLE);
            mProgressBar.progressiveStart();
            return true;
        } else if (menuItem.getItemId() == R.id.add_dashboard) {
            EditDialogFragment fragment = EditDialogFragment.newInstance();
            fragment.setListener(this);
            fragment.show(getChildFragmentManager(),
                    EditDialogFragment.EDIT_DIALOG_FRAGMENT);
            return true;
        }
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard_view_pager, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        final int blue = getResources().getColor(R.color.navy_blue);
        final int gray = getResources().getColor(R.color.darker_grey);

        mAdapter = new DashboardAdapter(getChildFragmentManager());
        mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
        mViewPager.setAdapter(mAdapter);

        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {

            @Override
            public int getIndicatorColor(int position) {
                return blue;
            }

            @Override
            public int getDividerColor(int position) {
                return gray;
            }

        });

        mSlidingTabLayout.setViewPager(mViewPager);
        mSlidingTabLayout.setOnPageChangeListener(this);

        mEditButton = (FloatingActionButton) view.findViewById(R.id.button_edit_dashboard);
        mEditButton.setOnClickListener(this);

        mProgressBar = (SmoothProgressBar) view.findViewById(R.id.progress_bar);
        if (savedInstanceState != null && savedInstanceState.getBoolean(STATE_PROGRESS)) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public void onSaveInstanceState(Bundle out) {
        out.putBoolean(STATE_PROGRESS, mProgressBar.isShown());
        super.onSaveInstanceState(out);
    }

    @Override
    public Loader<CursorHolder<List<DbRow<Dashboard>>>> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_ID) {
            final String SELECTION = Dashboards.STATE + " != " + '"' + State.DELETING + '"';
            return new DashboardListLoader(
                    getActivity(), Dashboards.CONTENT_URI,
                    DashboardHandler.PROJECTION, SELECTION, null, null
            );
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<CursorHolder<List<DbRow<Dashboard>>>> loader,
                               CursorHolder<List<DbRow<Dashboard>>> data) {
        if (loader != null && loader.getId() == LOADER_ID && data != null) {
            mAdapter.swapData(data.getData());
            mSlidingTabLayout.setViewPager(mViewPager);
        }
    }

    @Override
    public void onLoaderReset(Loader<CursorHolder<List<DbRow<Dashboard>>>> loader) {
        // reset state of views
    }

    @Subscribe
    public void onDashboardSyncedEvent(OnDashboardsSyncedEvent event) {
        final ResponseHolder<String> sendResponseHolder = event.getSendResponse();
        final ResponseHolder<String> getResponseHolder = event.getRetrieveResponse();

        if (sendResponseHolder != null && sendResponseHolder.getException() != null) {
            sendResponseHolder.getException().printStackTrace();
        }

        if (getResponseHolder != null && getResponseHolder.getException() != null) {
            getResponseHolder.getException().printStackTrace();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        DbRow<Dashboard> dbItem = mAdapter.getDashboard(position);
        if (dbItem != null) {
            Dashboard dashboard = dbItem.getItem();
            boolean isDashboardEditable = dashboard.getAccess().isManage();
            setEditButtonVisibility(isDashboardEditable);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onClick(View view) {
        int position = mViewPager.getCurrentItem();
        DbRow<Dashboard> dbItem = mAdapter.getDashboard(position);
        Intent intent = DashboardEditActivity.prepareIntent(getActivity(), dbItem);
        startActivity(intent);
    }

    private void setEditButtonVisibility(boolean isEditable) {
        if (isEditable) {
            mEditButton.setVisibility(View.VISIBLE);
        } else {
            mEditButton.setVisibility(View.GONE);
        }
    }

    @Subscribe
    public void onDashboardCreateEvent(OnDashboardCreateEvent event) {
        if (event.getResponseHolder().getException() != null) {
            event.getResponseHolder().getException().printStackTrace();
        }
    }

    @Override
    public void onFinishEditDialog(int id, String inputText) {
        DashboardCreateEvent event = new DashboardCreateEvent();
        event.setDashboardName(inputText);
        BusProvider.getInstance().post(event);
        Toast.makeText(getActivity(), inputText, Toast.LENGTH_SHORT).show();
    }

    @Subscribe
    public void onDashboardsSyncEvent(OnDashboardsSyncedEvent event) {
        mProgressBar.progressiveStop();
        mProgressBar.setVisibility(View.GONE);
    }

    public static class DashboardListLoader extends AbsCursorLoader<List<DbRow<Dashboard>>> {
        public DashboardListLoader(Context context, Uri uri, String[] projection,
                                   String selection, String[] selectionArgs, String sortOrder) {
            super(context, uri, projection, selection, selectionArgs, sortOrder);
        }

        @Override
        protected List<DbRow<Dashboard>> readDataFromCursor(Cursor cursor) {
            List<DbRow<Dashboard>> dashboards = new ArrayList<>();
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();

                do {
                    dashboards.add(DashboardHandler.fromCursor(cursor));
                } while (cursor.moveToNext());
            }
            return dashboards;
        }
    }
}
