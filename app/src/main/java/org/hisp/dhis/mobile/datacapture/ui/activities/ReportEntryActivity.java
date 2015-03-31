package org.hisp.dhis.mobile.datacapture.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.squareup.otto.Subscribe;

import org.hisp.dhis.mobile.datacapture.R;
import org.hisp.dhis.mobile.datacapture.api.android.events.OnReportCreateEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.OnReportSaveEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.ReportCreateEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.ReportDeleteEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.ReportPostEvent;
import org.hisp.dhis.mobile.datacapture.api.android.models.DbRow;
import org.hisp.dhis.mobile.datacapture.api.models.Group;
import org.hisp.dhis.mobile.datacapture.api.models.Report;
import org.hisp.dhis.mobile.datacapture.io.DBContract.Reports;
import org.hisp.dhis.mobile.datacapture.io.handlers.ReportGroupHandler;
import org.hisp.dhis.mobile.datacapture.io.handlers.ReportHandler;
import org.hisp.dhis.mobile.datacapture.io.loaders.CursorLoaderBuilder;
import org.hisp.dhis.mobile.datacapture.io.loaders.Transformation;
import org.hisp.dhis.mobile.datacapture.ui.adapters.ReportGroupAdapter;
import org.hisp.dhis.mobile.datacapture.ui.views.SlidingTabLayout;
import org.hisp.dhis.mobile.datacapture.utils.BusProvider;

import java.util.List;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class ReportEntryActivity extends BaseActivity
        implements LoaderCallbacks<List<DbRow<Group>>>, View.OnClickListener {
    private static final int LOADER_ID = 89254134;
    private static final String IS_PROGRESS_BAR_VISIBLE = "extra:isProgressBarVisible";

    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    private ReportGroupAdapter mAdapter;

    private Button mDeleteButton;
    private Button mSendButton;

    private SmoothProgressBar mProgressBar;

    public static Intent newIntent(FragmentActivity activity,
                                   String orgUnitId, String orgUnitLabel,
                                   String dataSetId, String dataSetLabel,
                                   String period, String periodLabel) {
        Intent intent = new Intent(activity, ReportEntryActivity.class);
        intent.putExtra(Reports.ORG_UNIT_ID, orgUnitId);
        intent.putExtra(Reports.ORG_UNIT_LABEL, orgUnitLabel);
        intent.putExtra(Reports.DATASET_ID, dataSetId);
        intent.putExtra(Reports.DATASET_LABEL, dataSetLabel);
        intent.putExtra(Reports.PERIOD, period);
        intent.putExtra(Reports.PERIOD_LABEL, periodLabel);
        return intent;
    }

    private static Report getReportFromBundle(Bundle extras) {
        Report report = new Report();

        if (extras != null) {
            String orgUnitId = extras.getString(Reports.ORG_UNIT_ID);
            String orgUnitLabel = extras.getString(Reports.ORG_UNIT_LABEL);
            String dataSetId = extras.getString(Reports.DATASET_ID);
            String dataSetLabel = extras.getString(Reports.DATASET_LABEL);
            String periodExtra = extras.getString(Reports.PERIOD);
            String periodLabel = extras.getString(Reports.PERIOD_LABEL);

            report.setOrgUnit(orgUnitId);
            report.setOrgUnitLabel(orgUnitLabel);
            report.setDataSet(dataSetId);
            report.setDataSetLabel(dataSetLabel);
            report.setPeriod(periodExtra);
            report.setPeriodLabel(periodLabel);
        }

        return report;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_entry);

        Toolbar actionBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(actionBar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mProgressBar = (SmoothProgressBar) findViewById(R.id.progress_bar);
        mDeleteButton = (Button) findViewById(R.id.delete_report);
        mSendButton = (Button) findViewById(R.id.send_report);

        mDeleteButton.setOnClickListener(this);
        mSendButton.setOnClickListener(this);

        mAdapter = new ReportGroupAdapter(getSupportFragmentManager());

        final int blue = getResources().getColor(R.color.navy_blue);
        final int gray = getResources().getColor(R.color.darker_grey);

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

        mViewPager.setAdapter(mAdapter);
        mSlidingTabLayout.setViewPager(mViewPager);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getSupportLoaderManager().initLoader(LOADER_ID, getIntent().getExtras(), this);
        if (savedInstanceState == null) {
            ReportCreateEvent event = new ReportCreateEvent();
            event.setReport(getReportFromBundle(getIntent().getExtras()));
            BusProvider.getInstance().post(event);
            showProgressBar();
        } else {
            boolean isProgressBarVisible = savedInstanceState
                    .getBoolean(IS_PROGRESS_BAR_VISIBLE);
            if (isProgressBarVisible) {
                showProgressBar();
            } else {
                hideProgressBar();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(IS_PROGRESS_BAR_VISIBLE,
                mProgressBar.getVisibility() == View.VISIBLE);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        BusProvider.getInstance().post(new OnReportSaveEvent());
        super.onBackPressed();
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

    @Override
    public Loader<List<DbRow<Group>>> onCreateLoader(int id, Bundle args) {
        if (LOADER_ID == id) {
            Report report = getReportFromBundle(args);
            return CursorLoaderBuilder.forUri(Reports.buildUriWithGroups())
                    .projection(ReportGroupHandler.PROJECTION)
                    .selection(ReportHandler.REPORT_ID_SELECTION)
                    .selectionArgs(new String[]{report.getOrgUnit(),
                            report.getDataSet(), report.getPeriod()})
                    .transformation(new Transformer())
                    .build(getBaseContext());
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<List<DbRow<Group>>> loader,
                               List<DbRow<Group>> data) {
        if (loader != null && LOADER_ID == loader.getId() && data != null) {
            mAdapter.swapData(data);

            if (data.size() > 1) {
                mSlidingTabLayout.setVisibility(View.VISIBLE);
                mSlidingTabLayout.setViewPager(mViewPager);
            } else {
                mSlidingTabLayout.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<List<DbRow<Group>>> loader) {
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.delete_report: {
                ReportDeleteEvent event = new ReportDeleteEvent();
                event.setReport(getReportFromBundle(getIntent().getExtras()));
                BusProvider.getInstance().post(event);
                finish();
                break;
            }
            case R.id.send_report: {
                ReportPostEvent event = new ReportPostEvent();
                event.setReport(getReportFromBundle(getIntent().getExtras()));
                BusProvider.getInstance().post(event);
                finish();
                break;
            }
        }
    }

    @Subscribe
    public void onReportCreateEvent(OnReportCreateEvent event) {
        hideProgressBar();
    }

    private void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
        mDeleteButton.setVisibility(View.GONE);
        mSendButton.setVisibility(View.GONE);
    }

    private void hideProgressBar() {
        mProgressBar.setVisibility(View.GONE);
        mDeleteButton.setVisibility(View.VISIBLE);
        mSendButton.setVisibility(View.VISIBLE);
    }

    private static class Transformer implements Transformation<List<DbRow<Group>>> {

        @Override
        public List<DbRow<Group>> transform(Context context, Cursor cursor) {
            return ReportGroupHandler.map(cursor, false);
        }
    }
}
