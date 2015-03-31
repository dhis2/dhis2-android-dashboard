package org.hisp.dhis.mobile.datacapture.ui.fragments.aggregate;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.hisp.dhis.mobile.datacapture.R;
import org.hisp.dhis.mobile.datacapture.io.handlers.DataSetHandler;
import org.hisp.dhis.mobile.datacapture.api.android.models.DbRow;
import org.hisp.dhis.mobile.datacapture.api.models.DataSet;
import org.hisp.dhis.mobile.datacapture.io.DBContract.DataSets;
import org.hisp.dhis.mobile.datacapture.io.loaders.CursorLoaderBuilder;
import org.hisp.dhis.mobile.datacapture.io.loaders.Transformation;
import org.hisp.dhis.mobile.datacapture.ui.adapters.SimpleAdapter;

import java.util.List;

public class DataSetDialogFragment extends DialogFragment
        implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<List<DbRow<DataSet>>> {
    private static String TAG = DataSetDialogFragment.class.getName();
    private static final int LOADER_ID = 340962123;

    private ListView mListView;
    private SimpleAdapter<DbRow<DataSet>> mAdapter;
    private OnDatasetSetListener mListener;

    public static DataSetDialogFragment newInstance(OnDatasetSetListener listener,
                                                    int orgUnitDBId) {
        DataSetDialogFragment fragment = new DataSetDialogFragment();
        Bundle args = new Bundle();
        args.putInt(DataSets.ORGANIZATION_UNIT_DB_ID, orgUnitDBId);
        fragment.setOnClickListener(listener);
        fragment.setArguments(args);
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
        return inflater.inflate(R.layout.dialog_fragment_listview, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mAdapter = new SimpleAdapter<>(getActivity());
        mAdapter.setCallback(new StringExtractor());
        mListView = (ListView) view.findViewById(R.id.simple_listview);

        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, getArguments(), this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view,
                            int position, long id) {
        if (mListener != null) {
            DbRow<DataSet> row = mAdapter.getItemSafely(position);
            if (row != null) {
                mListener.onDataSetSelected(
                        row.getId(), row.getItem().getId(),
                        row.getItem().getLabel()
                );
            }
        }
        dismiss();
    }

    @Override
    public Loader<List<DbRow<DataSet>>> onCreateLoader(int id, Bundle bundle) {
        if (LOADER_ID == id && bundle != null) {
            int orgUnitId = bundle.getInt(DataSets.ORGANIZATION_UNIT_DB_ID);
            return CursorLoaderBuilder.forUri(DataSets.CONTENT_URI)
                    .projection(DataSetHandler.PROJECTION)
                    .selection(DataSetHandler.SELECTION_BY_ORG_UNIT)
                    .selectionArgs(new String[] { String.valueOf(orgUnitId) })
                    .transformation(new DataSetTransform())
                    .build(getActivity());
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<List<DbRow<DataSet>>> listLoader,
                               List<DbRow<DataSet>> dbRows) {
        mAdapter.swapData(dbRows);
    }

    @Override
    public void onLoaderReset(Loader<List<DbRow<DataSet>>> listLoader) { }

    public void setOnClickListener(OnDatasetSetListener listener) {
        mListener = listener;
    }

    public void show(FragmentManager manager) {
        show(manager, TAG);
    }

    static class DataSetTransform implements Transformation<List<DbRow<DataSet>>> {

        @Override
        public List<DbRow<DataSet>> transform(Context context, Cursor cursor) {
            return DataSetHandler.query(cursor, false);
        }
    }

    static class StringExtractor implements SimpleAdapter.ExtractStringCallback<DbRow<DataSet>> {

        @Override
        public String getString(DbRow<DataSet> object) {
            return object.getItem().getLabel();
        }
    }

    public interface OnDatasetSetListener {
        public void onDataSetSelected(int dbId, String orgUnitId,
                                      String orgUnitLabel);
    }
}
