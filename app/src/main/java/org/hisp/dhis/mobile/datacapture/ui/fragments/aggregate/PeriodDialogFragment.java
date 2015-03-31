package org.hisp.dhis.mobile.datacapture.ui.fragments.aggregate;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import org.hisp.dhis.mobile.datacapture.R;
import org.hisp.dhis.mobile.datacapture.api.android.date.CustomDateIterator;
import org.hisp.dhis.mobile.datacapture.api.android.date.DateIteratorFactory;
import org.hisp.dhis.mobile.datacapture.io.handlers.DataSetHandler;
import org.hisp.dhis.mobile.datacapture.api.android.models.DateHolder;
import org.hisp.dhis.mobile.datacapture.api.android.models.DbRow;
import org.hisp.dhis.mobile.datacapture.api.models.DataSet;
import org.hisp.dhis.mobile.datacapture.api.models.DataSetOptions;
import org.hisp.dhis.mobile.datacapture.io.DBContract.DataSets;
import org.hisp.dhis.mobile.datacapture.io.loaders.CursorLoaderBuilder;
import org.hisp.dhis.mobile.datacapture.io.loaders.Transformation;
import org.hisp.dhis.mobile.datacapture.ui.adapters.SimpleAdapter;
import org.hisp.dhis.mobile.datacapture.ui.fragments.ListViewDialogFragment;

import java.util.List;

public class PeriodDialogFragment extends DialogFragment
        implements LoaderCallbacks<DbRow<DataSet>>,
        View.OnClickListener, AdapterView.OnItemClickListener {
    private static final String TAG = ListViewDialogFragment.class.getName();
    private static final int LOADER_ID = 345234575;

    private ListView mListView;
    private Button mPrevious;
    private Button mNext;

    private SimpleAdapter<DateHolder> mAdapter;
    private OnPeriodSetListener mListener;

    private CustomDateIterator<List<DateHolder>> mIterator;

    public static PeriodDialogFragment newInstance(OnPeriodSetListener listener,
                                                   int dataSetDBId) {
        PeriodDialogFragment fragment = new PeriodDialogFragment();
        Bundle args = new Bundle();
        args.putInt(DataSets.DB_ID, dataSetDBId);
        fragment.setArguments(args);
        fragment.setOnItemClickListener(listener);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE,
                R.style.Theme_AppCompat_Light_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_listview_period, container, false);
        mListView = (ListView) view.findViewById(R.id.simple_listview);
        mPrevious = (Button) view.findViewById(R.id.previous);
        mNext = (Button) view.findViewById(R.id.next);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mAdapter = new SimpleAdapter<>(getActivity());
        mAdapter.setCallback(new ExtractPeriodLabel());

        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        mPrevious.setOnClickListener(this);
        mNext.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, getArguments(), this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.previous: {
                mNext.setEnabled(true);
                mAdapter.swapData(mIterator.previous());
                break;
            }
            case R.id.next: {
                if (mIterator.hasNext()) {
                    List<DateHolder> dates = mIterator.next();
                    if (!mIterator.hasNext()) {
                        mNext.setEnabled(false);
                    }
                    mAdapter.swapData(dates);
                } else {
                    mNext.setEnabled(false);
                }
                break;
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mListener != null) {
            DateHolder date = mAdapter.getItemSafely(position);
            if (date != null) {
                mListener.onPeriodSelected(date);
            }
            dismiss();
        }
    }

    public void show(FragmentManager manager) {
        show(manager, TAG);
    }

    public void setOnItemClickListener(OnPeriodSetListener listener) {
        mListener = listener;
    }

    @Override
    public Loader<DbRow<DataSet>> onCreateLoader(int id, Bundle bundle) {
        if (id == LOADER_ID && bundle != null) {
            int dataSetId = bundle.getInt(DataSets.DB_ID);
            Uri uri = ContentUris.withAppendedId(DataSets.CONTENT_URI, dataSetId);
            return CursorLoaderBuilder.forUri(uri)
                    .projection(DataSetHandler.PROJECTION)
                    .transformation(new TransformDataSet())
                    .build(getActivity());
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<DbRow<DataSet>> dbRowLoader,
                               DbRow<DataSet> dataSetDbRow) {
        if (dbRowLoader != null && dataSetDbRow != null &&
                dbRowLoader.getId() == LOADER_ID) {
            DataSetOptions options = dataSetDbRow
                    .getItem()
                    .getOptions();
            mIterator = DateIteratorFactory.getDateIterator(
                    options.getPeriodType(), options.isAllowFuturePeriods()
            );

            mAdapter.swapData(mIterator.current());
            if (mIterator != null && mIterator.hasNext()) {
                mNext.setEnabled(true);
            } else {
                mNext.setEnabled(false);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<DbRow<DataSet>> dbRowLoader) {
    }

    static class TransformDataSet implements Transformation<DbRow<DataSet>> {

        @Override
        public DbRow<DataSet> transform(Context context, Cursor cursor) {
            return DataSetHandler.querySingleItem(cursor, false);
        }
    }

    static class ExtractPeriodLabel implements SimpleAdapter.ExtractStringCallback<DateHolder> {

        @Override
        public String getString(DateHolder object) {
            return object.getLabel();
        }
    }

    public interface OnPeriodSetListener {
        public void onPeriodSelected(DateHolder date);
    }
}
