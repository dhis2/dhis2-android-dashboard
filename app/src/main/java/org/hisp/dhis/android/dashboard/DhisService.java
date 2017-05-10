/*
 * Copyright (c) 2015, dhis2
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.dashboard;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.squareup.okhttp.HttpUrl;

import org.hisp.dhis.android.dashboard.api.controllers.DhisController;
import org.hisp.dhis.android.dashboard.api.job.Job;
import org.hisp.dhis.android.dashboard.api.job.JobExecutor;
import org.hisp.dhis.android.dashboard.api.job.NetworkJob;
import org.hisp.dhis.android.dashboard.api.models.UserAccount;
import org.hisp.dhis.android.dashboard.api.models.meta.Credentials;
import org.hisp.dhis.android.dashboard.api.network.APIException;
import org.hisp.dhis.android.dashboard.api.persistence.preferences.ResourceType;
import org.hisp.dhis.android.dashboard.api.utils.EventBusProvider;
import org.hisp.dhis.android.dashboard.ui.events.UiEvent;

/**
 * @author Araz Abishov <araz.abishov.gsoc@gmail.com>.
 */
public final class DhisService extends Service {
    public static final int LOG_IN = 1;
    public static final int CONFIRM_USER = 2;
    public static final int LOG_OUT = 3;
    public static final int SYNC_DASHBOARDS = 5;
    public static final int SYNC_DASHBOARD_CONTENT = 6;
    public static final int SYNC_INTERPRETATIONS = 7;
    public static final int PULL_INTERPRETATION_IMAGES = 8;
    public static final int PULL_DASHBOARD_IMAGES = 9;

    private final IBinder mBinder = new ServiceBinder();
    private DhisController mDhisController;

    @Override
    public void onCreate() {
        super.onCreate();
        mDhisController = DhisController.getInstance();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class ServiceBinder extends Binder {
        public DhisService getService() {
            return DhisService.this;
        }
    }

    public void logInUser(final HttpUrl serverUrl, final Credentials credentials) {
        JobExecutor.enqueueJob(new NetworkJob<UserAccount>(LOG_IN,
                ResourceType.USERS) {

            @Override
            public UserAccount execute() throws APIException {
                return mDhisController.logInUser(serverUrl, credentials);
            }
        });
    }

    public void logOutUser() {
        JobExecutor.enqueueJob(new Job<UiEvent>(LOG_OUT) {
            @Override
            public UiEvent inBackground() {
                mDhisController.logOutUser();
                return new UiEvent(UiEvent.UiEventType.USER_LOG_OUT);
            }

            @Override
            public void onFinish(UiEvent result) {
                EventBusProvider.post(result);
            }
        });
    }

    public void confirmUser(final Credentials credentials) {
        JobExecutor.enqueueJob(new NetworkJob<UserAccount>(CONFIRM_USER,
                ResourceType.USERS) {

            @Override
            public UserAccount execute() throws APIException {
                return mDhisController.confirmUser(credentials);
            }
        });
    }

    public void syncDashboardContents() {
        JobExecutor.enqueueJob(new NetworkJob<Object>(SYNC_DASHBOARD_CONTENT,
                ResourceType.DASHBOARDS_CONTENT) {

            @Override
            public Object execute() throws APIException {
                mDhisController.syncDashboardContent();
                return new Object();
            }
        });
    }

    public void syncDashboardsAndContent() {
        JobExecutor.enqueueJob(new NetworkJob<Object>(SYNC_DASHBOARDS,
                ResourceType.DASHBOARDS) {

            @Override
            public Object execute() throws APIException {
                mDhisController.syncDashboardContent();
                mDhisController.syncDashboards();
                return new Object();
            }
        });
    }

    public void syncDashboards() {
        JobExecutor.enqueueJob(new NetworkJob<Object>(SYNC_DASHBOARDS,
                ResourceType.DASHBOARDS) {

            @Override
            public Object execute() throws APIException {
                mDhisController.syncDashboards();
                return new Object();
            }
        });
    }

    public void syncInterpretations() {
        JobExecutor.enqueueJob(new NetworkJob<Object>(SYNC_INTERPRETATIONS,
                ResourceType.INTERPRETATIONS) {
            @Override
            public Object execute() throws APIException {
                mDhisController.syncInterpretations();
                return new Object();
            }
        });
    }

    public void pullInterpretationImages(final Context context) {
        JobExecutor.enqueueJob(new NetworkJob<Object>(PULL_INTERPRETATION_IMAGES,
                ResourceType.INTERPRETATION_IMAGES) {
            @Override
            public Object execute() throws APIException {
                mDhisController.pullInterpretationImages(context);
                return new Object();
            }
        });
    }

    public void pullDashboardImages(final Context context) {
        JobExecutor.enqueueJob(new NetworkJob<Object>(PULL_DASHBOARD_IMAGES,
                ResourceType.DASHBOARD_IMAGES) {
            @Override
            public Object execute() throws APIException {
                mDhisController.pullDashboardImages(context);
                return new Object();
            }
        });
    }

    public boolean isJobRunning(int jobId) {
        return JobExecutor.isJobRunning(jobId);
    }
}
