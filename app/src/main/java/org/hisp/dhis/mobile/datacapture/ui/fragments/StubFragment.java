package org.hisp.dhis.mobile.datacapture.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.hisp.dhis.mobile.datacapture.R;
import org.hisp.dhis.mobile.datacapture.ui.views.AutoCompleteValueEntryView;

import java.util.ArrayList;
import java.util.List;

public class StubFragment extends Fragment {
    public static final String NUMBER_EXTRA = "numberExtra";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle bundle) {
        int number = 0;

        if (getArguments() != null) {
            number = getArguments().getInt(NUMBER_EXTRA);
        }

        //TextView stubTextView = (TextView) view.findViewById(R.id.stub_text_view);
        //stubTextView.setText("StubFragment: " + number);

        View view = inflater.inflate(R.layout.fragment_stub, group, false);
        AutoCompleteValueEntryView autoComplete = (AutoCompleteValueEntryView)
                view.findViewById(R.id.stub_text_view);
        List<String> values = new ArrayList<>();
        values.add("1");
        values.add("2");
        values.add("3");
        values.add("4");
        values.add("5");
        values.add("Hello");
        autoComplete.swapData(values);

        return view;
    }
}
