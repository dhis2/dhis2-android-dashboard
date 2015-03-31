package org.hisp.dhis.mobile.datacapture.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.hisp.dhis.mobile.datacapture.R;

public class ListViewFragment extends Fragment {
    private static final String STRING_ARRAY_EXTRA = "stringArrayExtra";

    private ListView mListView;
    private ArrayAdapter<String> mAdapter;

    public static ListViewFragment newInstance(String[] entries) {
        ListViewFragment fragment = new ListViewFragment();
        Bundle args = new Bundle();

        args.putStringArray(ListViewFragment.STRING_ARRAY_EXTRA, entries);
        fragment.setArguments(args);

        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mListView = (ListView) inflater.inflate(R.layout.listview, container, false);
        return mListView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (getArguments() != null &&
                getArguments().getStringArray(STRING_ARRAY_EXTRA) != null &&
                getArguments().getStringArray(STRING_ARRAY_EXTRA).length > 0) {
            mAdapter = new ArrayAdapter<>(getActivity(),
                    R.layout.listview_row_simple_item_layout, R.id.text_label,
                    getArguments().getStringArray(STRING_ARRAY_EXTRA));
            mListView.setAdapter(mAdapter);
        }
    }
}