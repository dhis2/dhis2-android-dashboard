package org.hisp.dhis.mobile.datacapture.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.hisp.dhis.mobile.datacapture.R;
import org.hisp.dhis.mobile.datacapture.api.models.Comment;
import org.hisp.dhis.mobile.datacapture.ui.adapters.CommentsAdapter;

import java.util.List;

// TODO implement 'close' button
public class CommentsDialogFragment extends DialogFragment {
    public static final String COMMENTS_DIALOG = CommentsDialogFragment.class.getName();
    private ListView mListView;

    private CommentsAdapter mAdapter;
    private List<Comment> mData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Theme_AppCompat_Light_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_listview, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mAdapter = new CommentsAdapter(getActivity());
        mListView = (ListView) view.findViewById(R.id.simple_listview);
        mListView.setAdapter(mAdapter);
        mAdapter.swapData(mData);
    }

    public void setData(List<Comment> comments) {
        mData = comments;
    }
}
