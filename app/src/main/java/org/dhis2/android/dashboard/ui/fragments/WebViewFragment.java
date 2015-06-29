/*
 * Copyright (c) 2015, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.dhis2.android.dashboard.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import org.dhis2.android.dashboard.R;
import org.dhis2.android.dashboard.api.DhisManager;
import org.dhis2.android.dashboard.api.job.Job;
import org.dhis2.android.dashboard.api.job.JobExecutor;
import org.dhis2.android.dashboard.api.network.DhisApi;
import org.dhis2.android.dashboard.api.network.RepoManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;

import retrofit.mime.TypedInput;

import static android.text.TextUtils.isEmpty;

public class WebViewFragment extends BaseFragment {
    private static final String DASHBOARD_ELEMENT_ID = "arg:dashboardElementId";

    WebView mWebView;

    public static WebViewFragment newInstance(String id) {
        Bundle args = new Bundle();
        args.putString(DASHBOARD_ELEMENT_ID, id);

        WebViewFragment fragment = new WebViewFragment();
        fragment.setArguments(args);

        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_web_view, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mWebView = (WebView) view;
        if (getArguments() != null && !isEmpty(getArguments()
                .getString(DASHBOARD_ELEMENT_ID))) {
            JobExecutor.enqueueJob(new GetReportTableJob(this, getArguments()
                    .getString(DASHBOARD_ELEMENT_ID)));
        }
    }

    public void onDataDownloaded(String data) {
        mWebView.loadData(data, "text/html", "UTF-8");
    }

    static class GetReportTableJob extends Job<String> {
        static final int JOB_ID = 4573452;

        final WeakReference<WebViewFragment> mFragmentRef;
        final String mDashboardElementId;

        public GetReportTableJob(WebViewFragment fragment, String dashboardElementId) {
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
        public String inBackground() {
            DhisApi dhisApi = RepoManager.createService(DhisManager.getInstance().getServerUrl(),
                    DhisManager.getInstance().getUserCredentials());
            return readInputStream(dhisApi.getReportTableData(mDashboardElementId).getBody());
        }

        @Override
        public void onFinish(String result) {
            if (mFragmentRef.get() != null) {
                mFragmentRef.get().onDataDownloaded(result);
            }
        }
    }
}