package org.hisp.dhis.mobile.datacapture.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.squareup.otto.Subscribe;

import org.hisp.dhis.mobile.datacapture.io.DBContract.DashboardItems;
import org.hisp.dhis.mobile.datacapture.utils.BusProvider;
import org.hisp.dhis.mobile.datacapture.R;
import org.hisp.dhis.mobile.datacapture.api.android.events.GetReportTableEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.OnGotReportTableEvent;

public class WebViewFragment extends BaseFragment {
    private WebView mWebView;

    public static WebViewFragment newInstance(String id) {
        WebViewFragment fragment = new WebViewFragment();
        Bundle args = new Bundle();

        args.putString(DashboardItems.ID, id);
        fragment.setArguments(args);

        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mWebView = (WebView) inflater.inflate(R.layout.fragment_web_view, container, false);
        return mWebView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (getArguments() != null && getArguments().getString(DashboardItems.ID) != null) {
            GetReportTableEvent event = new GetReportTableEvent();
            event.setId(getArguments().getString(DashboardItems.ID));
            BusProvider.getInstance().post(event);
        }
    }

    @Subscribe
    public void onGotReportTable(OnGotReportTableEvent event) {
        if (event.getResponseHolder().getException() == null) {
            mWebView.loadData(event.getResponseHolder().getItem(), "text/html", "UTF-8");
        }
    }
}
