package org.hisp.dhis.android.dashboard.ui.fragments;

import static android.text.TextUtils.isEmpty;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import org.hisp.dhis.android.dashboard.DhisApplication;
import org.hisp.dhis.android.dashboard.R;
import org.hisp.dhis.android.dashboard.api.controllers.DhisController;
import org.hisp.dhis.android.dashboard.api.job.Job;
import org.hisp.dhis.android.dashboard.api.job.JobExecutor;
import org.hisp.dhis.android.dashboard.api.models.DataElementDimension;
import org.hisp.dhis.android.dashboard.api.models.EventReport;
import org.hisp.dhis.android.dashboard.api.models.meta.ResponseHolder;
import org.hisp.dhis.android.dashboard.api.network.APIException;
import org.hisp.dhis.android.dashboard.api.network.DhisApi;
import org.hisp.dhis.android.dashboard.api.network.RepoManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.mime.TypedInput;

public class WebViewEventReportFragment extends BaseFragment {
    private static final String DASHBOARD_ELEMENT_ID = "arg:dashboardElementId";

    @Bind(R.id.web_view_content)
    WebView mWebView;

    @Bind(R.id.container_layout_progress_bar)
    View mProgressBarContainer;

    public static WebViewEventReportFragment newInstance(String id) {
        Bundle args = new Bundle();
        args.putString(DASHBOARD_ELEMENT_ID, id);

        WebViewEventReportFragment fragment = new WebViewEventReportFragment();
        fragment.setArguments(args);

        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_web_view, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        mWebView.getSettings().setBuiltInZoomControls(true);
        if (getArguments() != null && !isEmpty(getArguments()
                .getString(DASHBOARD_ELEMENT_ID))) {
            JobExecutor.enqueueJob(new GetEventReportTableJob(this, getArguments()
                    .getString(DASHBOARD_ELEMENT_ID)));
        }
    }

    public void onDataDownloaded(ResponseHolder<String> data) {
        mProgressBarContainer.setVisibility(View.GONE);

        if (data.getApiException() == null) {
            mWebView.loadData(data.getItem(), "text/html", "UTF-8");
        } else {
            if (isAdded()) {
                ((DhisApplication) (getActivity().getApplication()))
                        .showApiExceptionMessage(data.getApiException());
            }
        }
    }

    static class GetEventReportTableJob extends Job<ResponseHolder<String>> {
        static final int JOB_ID = 1546489;

        final WeakReference<WebViewEventReportFragment> mFragmentRef;
        final String mDashboardElementId;

        public GetEventReportTableJob(WebViewEventReportFragment fragment,
                String dashboardElementId) {
            super(JOB_ID);

            mFragmentRef = new WeakReference<>(fragment);
            mDashboardElementId = dashboardElementId;
        }


        static String readInputStream(TypedInput in) {
            StringBuilder builder = new StringBuilder();
            try {
                BufferedReader bufferedStream
                        = new BufferedReader(new InputStreamReader(in.in()));
                try {
                    String line;
                    while ((line = bufferedStream.readLine()) != null) {
                        builder.append(line);
                        builder.append('\n');
                    }
                    return builder.toString();
                } finally {
                    bufferedStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return builder.toString();
        }

        @Override
        public ResponseHolder<String> inBackground() {
            ResponseHolder<String> responseHolder = new ResponseHolder<>();
            EventReport eventReport;

            try {
                DhisApi dhisApi = RepoManager.createService(
                        DhisController.getInstance().getServerUrl(),
                        DhisController.getInstance().getUserCredentials());
                eventReport = dhisApi.getEventReport(mDashboardElementId);
                responseHolder.setItem(readInputStream(
                        dhisApi.getEventReportTableData(eventReport.getProgram().getuId(),
                                eventReport.getProgramStage().getuId(),
                                getDimensions(eventReport)).getBody()));
            } catch (APIException exception) {
                responseHolder.setApiException(exception);
            }

            return responseHolder;
        }

        private List<String> getDimensions(EventReport eventReport) {
            List<String> dimensions = new ArrayList<>();
            dimensions.add("pe:" + eventReport.getRelativePeriods().getRelativePeriodString());
            dimensions.add("ou:" + eventReport.getOrganisationUnits().get(0).getuId());
            for (DataElementDimension dimension : eventReport.getDataElementDimensions()) {
                String dimensionUID = "";
                dimensionUID += dimension.getDataElement().getuId();
                if (dimension.getFilter() != null && !dimension.getFilter().isEmpty()) {
                    dimensionUID += ":" + dimension.getFilter();
                }
                dimensions.add(dimensionUID);
            }
            return dimensions;
        }

        @Override
        public void onFinish(ResponseHolder<String> result) {
            if (mFragmentRef.get() != null) {
                mFragmentRef.get().onDataDownloaded(result);
            }
        }
    }
}
