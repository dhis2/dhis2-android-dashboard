/*
 * Copyright (c) 2015, University of Oslo
 * All rights reserved.
 *
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

package org.dhis2.android.dashboard.api;

import com.squareup.okhttp.HttpUrl;

import org.dhis2.android.dashboard.api.job.Job;
import org.dhis2.android.dashboard.api.job.JobExecutor;
import org.dhis2.android.dashboard.api.job.NetworkJob;
import org.dhis2.android.dashboard.api.models.UserAccount;
import org.dhis2.android.dashboard.api.models.meta.Credentials;
import org.dhis2.android.dashboard.api.network.APIException;

import static org.dhis2.android.dashboard.api.utils.Preconditions.isNull;

public final class DhisService {
    public static final int LOG_IN_JOB_ID = 1;
    public static final int CONFIRM_USER_JOB_ID = 2;
    public static final int LOG_OUT_JOB_ID = 3;

    public static final int SYNC_META_DATA = 4;
    public static final int SYNC_DASHBOARDS = 5;

    private DhisManager mDhisManager;

    public DhisService(DhisManager dhisManager) {
        mDhisManager = isNull(dhisManager, "DhisManager must not be null");
    }

    public void logInUser(final HttpUrl serverUrl, final Credentials credentials) {
        JobExecutor.enqueueJob(new NetworkJob<UserAccount>(LOG_IN_JOB_ID) {

            @Override
            public UserAccount execute() throws APIException {
                return mDhisManager.logInUser(serverUrl, credentials);
            }
        });
    }

    public void confirmUser(final Credentials credentials) {
        JobExecutor.enqueueJob(new NetworkJob<UserAccount>(CONFIRM_USER_JOB_ID) {

            @Override
            public UserAccount execute() throws APIException {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return mDhisManager.confirmUser(credentials);
            }
        });
    }

    // TODO we need to cancel all pending Jobs first
    public void logOutUser() {
        JobExecutor.enqueueJob(new Job<Object>(LOG_OUT_JOB_ID) {
            @Override
            public Object inBackground() {
                mDhisManager.logOutUser();
                return new Object();
            }
        });
    }

    public void syncMetaData() {
        JobExecutor.enqueueJob(new NetworkJob<Object>(SYNC_META_DATA) {

            @Override
            public Object execute() throws APIException {
                mDhisManager.syncMetaData();
                return new Object();
            }
        });
    }

    public void syncDashboardContent() {
        JobExecutor.enqueueJob(new NetworkJob<Object>(SYNC_DASHBOARDS) {

            @Override
            public Object execute() throws APIException {
                mDhisManager.syncDashboardContent();
                return new Object();
            }
        });
    }

    public void syncDashboards() {
        JobExecutor.enqueueJob(new NetworkJob<Object>(SYNC_DASHBOARDS) {

            @Override
            public Object execute() throws APIException {
                mDhisManager.syncDashboards();
                return new Object();
            }
        });
    }

    public boolean isJobRunning(int jobId) {
        return JobExecutor.isJobRunning(jobId);
    }
}
