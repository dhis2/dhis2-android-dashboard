package org.hisp.dhis.mobile.datacapture.ui.fragments.aggregate;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Subscribe;

import org.hisp.dhis.mobile.datacapture.R;
import org.hisp.dhis.mobile.datacapture.api.android.events.DatasetSyncEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.OnDatasetSyncEvent;
import org.hisp.dhis.mobile.datacapture.api.android.models.DateHolder;
import org.hisp.dhis.mobile.datacapture.io.DBContract.OrganizationUnits;
import org.hisp.dhis.mobile.datacapture.io.handlers.OrganizationUnitHandler;
import org.hisp.dhis.mobile.datacapture.io.loaders.CursorLoaderBuilder;
import org.hisp.dhis.mobile.datacapture.io.loaders.Transformation;
import org.hisp.dhis.mobile.datacapture.ui.activities.ReportEntryActivity;
import org.hisp.dhis.mobile.datacapture.ui.fragments.BaseFragment;
import org.hisp.dhis.mobile.datacapture.ui.fragments.aggregate.DataSetDialogFragment.OnDatasetSetListener;
import org.hisp.dhis.mobile.datacapture.ui.fragments.aggregate.OrgUnitDialogFragment.OnOrgUnitSetListener;
import org.hisp.dhis.mobile.datacapture.ui.fragments.aggregate.PeriodDialogFragment.OnPeriodSetListener;
import org.hisp.dhis.mobile.datacapture.ui.views.CardDetailedButton;
import org.hisp.dhis.mobile.datacapture.ui.views.CardTextViewButton;
import org.hisp.dhis.mobile.datacapture.utils.BusProvider;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class AggregateReportFragment extends BaseFragment
        implements View.OnClickListener, LoaderCallbacks<Boolean>,
        OnOrgUnitSetListener, OnDatasetSetListener, OnPeriodSetListener {
    private static final String STATE = "state:AggregateReportFragment";
    private static final int LOADER_ID = 345784834;

    private SmoothProgressBar mProgressBar;

    private CardTextViewButton mOrgUnitButton;
    private CardTextViewButton mDataSetButton;
    private CardTextViewButton mPeriodButton;
    private CardDetailedButton mButton;

    private AggregateReportFragmentState mState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_aggregate_report_layout, container, false);

        mProgressBar = (SmoothProgressBar) view.findViewById(R.id.progress_bar);

        mOrgUnitButton = (CardTextViewButton) view.findViewById(R.id.org_unit_button);
        mDataSetButton = (CardTextViewButton) view.findViewById(R.id.dataset_button);
        mPeriodButton = (CardTextViewButton) view.findViewById(R.id.period_button);
        mButton = (CardDetailedButton) view.findViewById(R.id.data_entry_button);

        mOrgUnitButton.setOnClickListener(this);
        mDataSetButton.setOnClickListener(this);
        mPeriodButton.setOnClickListener(this);
        mButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mOrgUnitButton.setEnabled(false);
        mDataSetButton.setEnabled(false);
        mPeriodButton.setEnabled(false);
        mButton.hide(false);

        if (savedInstanceState != null &&
                savedInstanceState.getParcelable(STATE) != null) {
            mState = savedInstanceState.getParcelable(STATE);
        }

        if (mState == null) {
            mState = new AggregateReportFragmentState();
        }

        mProgressBar.setVisibility(mState.isSyncInProcess() ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_refresh, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.refresh) {
            mProgressBar.setVisibility(View.VISIBLE);
            BusProvider.getInstance().post(new DatasetSyncEvent());
            return true;
        }

        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.org_unit_button: {
                OrgUnitDialogFragment fragment = OrgUnitDialogFragment
                        .newInstance(this);
                fragment.show(getChildFragmentManager());
                break;
            }
            case R.id.dataset_button: {
                DataSetDialogFragment fragment = DataSetDialogFragment
                        .newInstance(this, mState.getOrgUnitDBId());
                fragment.show(getChildFragmentManager());
                break;
            }
            case R.id.period_button: {
                PeriodDialogFragment fragment = PeriodDialogFragment
                        .newInstance(this, mState.getDataSetDBId());
                fragment.show(getChildFragmentManager());
                break;
            }
            case R.id.data_entry_button: {
                startReportEntryActivity();
                break;
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle out) {
        out.putParcelable(STATE, mState);
        super.onSaveInstanceState(out);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, getArguments(), this);
    }

    @Subscribe
    public void onDataSetSyncEvent(OnDatasetSyncEvent event) {
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onUnitSelected(int dbId, String orgUnitId,
                               String orgUnitLabel) {
        mOrgUnitButton.setText(orgUnitLabel);
        mDataSetButton.setEnabled(true);

        mState.setOrgUnit(dbId, orgUnitId, orgUnitLabel);
        mState.resetDataSet();
        mState.resetPeriod();
        handleViews(0);
    }

    @Override
    public void onDataSetSelected(int dbId, String dataSetId,
                                  String dataSetLabel) {
        mDataSetButton.setText(dataSetLabel);
        mPeriodButton.setEnabled(true);

        mState.setDataSet(dbId, dataSetId, dataSetLabel);
        mState.resetPeriod();
        handleViews(1);
    }

    @Override
    public void onPeriodSelected(DateHolder dateHolder) {
        mPeriodButton.setText(dateHolder.getLabel());
        mState.setPeriod(dateHolder);
        handleButton();
        handleViews(2);
    }

    private void handleButton() {
        String orgUnit = getString(R.string.organization_unit) +
                ": " + mState.getOrgUnitLabel();
        String dataSet = getString(R.string.dataset) +
                ": " + mState.getDataSetLabel();
        String period = getString(R.string.period) +
                ": " + mState.getPeriod().getLabel();
        mButton.setFirstLineText(orgUnit);
        mButton.setSecondLineText(dataSet);
        mButton.setThirdLineText(period);
    }

    private void handleViews(int level) {
        switch (level) {
            case 0:
                mPeriodButton.setEnabled(false);
            case 1:
                mButton.hide(true);
                break;
            case 2:
                mButton.show(true);
        }
    }

    private void startReportEntryActivity() {
        String orgUnitId = mState.getOrgUnitId();
        String orgUnitLabel = mState.getOrgUnitLabel();

        String dataSetId = mState.getDataSetId();
        String dataSetLabel = mState.getDataSetLabel();

        String period = mState.getPeriod().getDate();
        String periodLabel = mState.getPeriod().getLabel();

        Intent intent = ReportEntryActivity.newIntent(
                getActivity(),
                orgUnitId, orgUnitLabel,
                dataSetId, dataSetLabel,
                period, periodLabel
        );
        startActivity(intent);
    }

    @Override
    public Loader<Boolean> onCreateLoader(int id, Bundle bundle) {
        if (id == LOADER_ID) {
            return CursorLoaderBuilder.forUri(OrganizationUnits.CONTENT_URI)
                    .projection(OrganizationUnitHandler.PROJECTION)
                    .transformation(new OrgUnitTransformation())
                    .build(getActivity());
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Boolean> booleanLoader,
                               Boolean hasUnits) {
        if (booleanLoader != null &&
                booleanLoader.getId() == LOADER_ID) {
            mOrgUnitButton.setEnabled(hasUnits);
            if (!hasUnits) {
                return;
            }

            AggregateReportFragmentState backedUpState = new AggregateReportFragmentState(mState);
            if (!backedUpState.isOrgUnitEmpty()) {
                onUnitSelected(
                        backedUpState.getOrgUnitDBId(),
                        backedUpState.getOrgUnitId(),
                        backedUpState.getOrgUnitLabel()
                );

                if (!backedUpState.isDataSetEmpty()) {
                    onDataSetSelected(
                            backedUpState.getDataSetDBId(),
                            backedUpState.getDataSetId(),
                            backedUpState.getDataSetLabel()
                    );

                    if (!backedUpState.isPeriodEmpty()) {
                        onPeriodSelected(backedUpState.getPeriod());
                    }
                }
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Boolean> booleanLoader) {
    }

    static class OrgUnitTransformation implements Transformation<Boolean> {

        @Override
        public Boolean transform(Context context, Cursor cursor) {
            return (cursor != null && cursor.getCount() > 0);
        }
    }
}