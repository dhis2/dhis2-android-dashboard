package org.hisp.dhis.mobile.datacapture.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import org.hisp.dhis.mobile.datacapture.R;
import org.hisp.dhis.mobile.datacapture.api.android.models.DbRow;
import org.hisp.dhis.mobile.datacapture.api.android.models.ReportState;
import org.hisp.dhis.mobile.datacapture.api.models.Report;
import org.hisp.dhis.mobile.datacapture.io.DBContract.Reports;
import org.hisp.dhis.mobile.datacapture.io.handlers.ReportHandler;
import org.hisp.dhis.mobile.datacapture.io.loaders.CursorLoaderBuilder;
import org.hisp.dhis.mobile.datacapture.io.loaders.Transformation;
import org.hisp.dhis.mobile.datacapture.ui.activities.ReportEntryActivity;
import org.hisp.dhis.mobile.datacapture.ui.adapters.ReportListAdapter;

import java.util.List;

public class ReportsFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<List<DbRow<Report>>>,
        ReportListAdapter.OnItemClickListener {
    private static final int LOADER_ID = 678432561;
    private static final String REPORT_STATE = "reportState";

    private ListView mListview;
    private ReportListAdapter mAdapter;

    public static ReportsFragment newInstance(ReportState state) {
        ReportsFragment fragment = new ReportsFragment();
        Bundle args = new Bundle();
        args.putString(REPORT_STATE, state.toString());
        fragment.setArguments(args);
        return fragment;
    }

    public static ReportState getStateFromArgs(Bundle args) {
        String state = args.getString(REPORT_STATE);
        return ReportState.valueOf(state);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ReportState.PENDING.equals(
                getStateFromArgs(getArguments()))) {
            setHasOptionsMenu(true);
        } else {
            setHasOptionsMenu(false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reports, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_refresh, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.refresh) {
            Toast.makeText(getActivity(), "Schedule sync", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mAdapter = new ReportListAdapter(getActivity().getBaseContext());
        mAdapter.setOnItemClickListener(this);

        mListview = (ListView) view.findViewById(R.id.list);
        mListview.setAdapter(mAdapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, getArguments(), this);
    }

    @Override
    public Loader<List<DbRow<Report>>> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_ID) {
            String[] selectionArgs = new String[]{getStateFromArgs(args).toString()};
            return CursorLoaderBuilder.forUri(Reports.CONTENT_URI)
                    .projection(ReportHandler.PROJECTION)
                    .selection(ReportHandler.REPORT_STATE_SELECTION)
                    .selectionArgs(selectionArgs)
                    .transformation(new Transformer())
                    .build(getActivity().getBaseContext());
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<List<DbRow<Report>>> loader,
                               List<DbRow<Report>> data) {
        if (loader != null && loader.getId() == LOADER_ID) {
            mAdapter.swapData(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<DbRow<Report>>> loader) {
    }

    @Override
    public void onItemClick(DbRow<Report> row) {
        if (row != null && row.getItem() != null) {
            Report report = row.getItem();
            Intent intent = ReportEntryActivity.newIntent(getActivity(),
                    report.getOrgUnit(), report.getOrgUnitLabel(),
                    report.getDataSet(), report.getDataSetLabel(),
                    report.getPeriod(), report.getDataSetLabel());
            startActivity(intent);
        }
    }

    private static class Transformer implements Transformation<List<DbRow<Report>>> {

        @Override
        public List<DbRow<Report>> transform(Context context, Cursor cursor) {
            return ReportHandler.map(cursor, false);
        }
    }
}
