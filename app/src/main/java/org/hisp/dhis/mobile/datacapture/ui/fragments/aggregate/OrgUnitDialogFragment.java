package org.hisp.dhis.mobile.datacapture.ui.fragments.aggregate;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.hisp.dhis.mobile.datacapture.R;
import org.hisp.dhis.mobile.datacapture.io.handlers.OrganizationUnitHandler;
import org.hisp.dhis.mobile.datacapture.api.android.models.DbRow;
import org.hisp.dhis.mobile.datacapture.api.models.OrganisationUnit;
import org.hisp.dhis.mobile.datacapture.io.DBContract.OrganizationUnits;
import org.hisp.dhis.mobile.datacapture.io.loaders.CursorLoaderBuilder;
import org.hisp.dhis.mobile.datacapture.io.loaders.Transformation;
import org.hisp.dhis.mobile.datacapture.ui.adapters.SimpleAdapter;

import java.util.List;

public class OrgUnitDialogFragment extends DialogFragment
        implements AdapterView.OnItemClickListener, LoaderCallbacks<List<DbRow<OrganisationUnit>>> {
    private static final String TAG = OrgUnitDialogFragment.class.getName();
    private static final int LOADER_ID = 243756345;

    private ListView mListView;
    private SimpleAdapter<DbRow<OrganisationUnit>> mAdapter;
    private OnOrgUnitSetListener mListener;

    public static OrgUnitDialogFragment newInstance(OnOrgUnitSetListener listener) {
        OrgUnitDialogFragment fragment = new OrgUnitDialogFragment();
        fragment.setOnClickListener(listener);
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
            DbRow<OrganisationUnit> row = mAdapter.getItemSafely(position);
            if (row != null) {
                mListener.onUnitSelected(
                        row.getId(), row.getItem().getId(),
                        row.getItem().getLabel()
                );
            }
        }
        dismiss();
    }

    public void setOnClickListener(OnOrgUnitSetListener listener) {
        mListener = listener;
    }

    public void show(FragmentManager manager) {
        show(manager, TAG);
    }

    @Override
    public Loader<List<DbRow<OrganisationUnit>>> onCreateLoader(int id, Bundle args) {
        if (LOADER_ID == id) {
            return CursorLoaderBuilder.forUri(OrganizationUnits.CONTENT_URI)
                    .projection(OrganizationUnitHandler.PROJECTION)
                    .transformation(new OrgUnitTransform())
                    .build(getActivity());
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<List<DbRow<OrganisationUnit>>> loader,
                               List<DbRow<OrganisationUnit>> data) {
        mAdapter.swapData(data);
    }

    @Override
    public void onLoaderReset(Loader<List<DbRow<OrganisationUnit>>> loader) {
    }

    static class OrgUnitTransform implements Transformation<List<DbRow<OrganisationUnit>>> {

        @Override
        public List<DbRow<OrganisationUnit>> transform(Context context, Cursor cursor) {
            return OrganizationUnitHandler.query(cursor, false);
        }
    }

    static class StringExtractor implements SimpleAdapter.ExtractStringCallback<DbRow<OrganisationUnit>> {

        @Override
        public String getString(DbRow<OrganisationUnit> object) {
            return object.getItem().getLabel();
        }
    }

    public interface OnOrgUnitSetListener {
        public void onUnitSelected(int dbId, String orgUnitId,
                                   String orgUnitLabel);
    }
}