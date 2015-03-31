package org.hisp.dhis.mobile.datacapture.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.hisp.dhis.mobile.datacapture.R;
import org.hisp.dhis.mobile.datacapture.ui.adapters.SimpleAdapter;

import java.util.List;

public class ListViewDialogFragment extends DialogFragment implements AdapterView.OnItemClickListener {
    private static final String TAG = ListViewDialogFragment.class.getName();

    private ListView mListView;
    private SimpleAdapter mAdapter;
    private List<String> mListValues;
    private OnDialogItemClickListener mListener;

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
        mAdapter = new SimpleAdapter(getActivity());
        mListView = (ListView) view.findViewById(R.id.simple_listview);

        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        mAdapter.swapData(mListValues);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view,
                            int position, long id) {
        if (mListener != null) {
            mListener.onItemClickListener(position);
            dismiss();
        }
    }

    public void show(FragmentManager manager) {
        show(manager, TAG);
    }

    public void swapData(List<String> listValues) {
        mListValues = listValues;
        if (mAdapter != null) {
            mAdapter.swapData(listValues);
        }
    }

    public void setOnItemClickListener(OnDialogItemClickListener listener) {
        mListener = listener;
    }

    public interface OnDialogItemClickListener {
        public void onItemClickListener(int position);
    }
}
