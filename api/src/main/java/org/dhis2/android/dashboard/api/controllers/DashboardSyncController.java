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

package org.dhis2.android.dashboard.api.controllers;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.RemoteException;

import org.dhis2.android.dashboard.api.DhisManager;
import org.dhis2.android.dashboard.api.network.APIException;
import org.dhis2.android.dashboard.api.persistence.database.DbContract;
import org.dhis2.android.dashboard.api.persistence.database.DbContract.Dashboards;
import org.dhis2.android.dashboard.api.persistence.handlers.DashboardHandler;
import org.dhis2.android.dashboard.api.persistence.handlers.DashboardItemHandler;
import org.dhis2.android.dashboard.api.persistence.handlers.DashboardsToItemsHandler;
import org.dhis2.android.dashboard.api.persistence.handlers.SessionHandler;
import org.dhis2.android.dashboard.api.persistence.models.Dashboard;
import org.dhis2.android.dashboard.api.persistence.models.DashboardItem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class DashboardSyncController implements IController<Object> {
    private final Context mContext;
    private final DhisManager mDhisManager;
    private final SessionHandler mSessionHandler;
    private final DashboardHandler mDashboardHandler;
    private final DashboardItemHandler mDashboardItemHandler;
    private final DashboardsToItemsHandler mDashboardToItemsHandler;

    public DashboardSyncController(Context context, DhisManager dhisManager, SessionHandler handler,
                                   DashboardHandler dashboardHandler, DashboardItemHandler itemHandler,
                                   DashboardsToItemsHandler dashboardsToItemsHandler) {
        mContext = context;
        mDhisManager = dhisManager;
        mSessionHandler = handler;
        mDashboardHandler = dashboardHandler;
        mDashboardItemHandler = itemHandler;
        mDashboardToItemsHandler = dashboardsToItemsHandler;
    }

    @Override
    public Object run() throws APIException {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        System.out.println("Here 0");
        List<Dashboard> dashboards = updateDashboards();
        System.out.println("Here 1");
        List<DashboardItem> items = updateDashboardItems(dashboards);
        System.out.println("Here 2");

        ops.addAll(mDashboardHandler.sync(dashboards));
        ops.addAll(mDashboardItemHandler.sync(items));
        ops.addAll(mDashboardToItemsHandler.sync(dashboards));

        try {
            mContext.getContentResolver()
                    .applyBatch(DbContract.AUTHORITY, ops);
            mContext.getContentResolver()
                    .notifyChange(Dashboards.CONTENT_URI, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
        /* new SaveModelTransaction<>(ProcessModelInfo
                .withModels(dashboards)).onExecute();
        new SaveModelTransaction<>(ProcessModelInfo
                .withModels(dashboardItems)).onExecute(); */

        return new Object();
    }

    private List<Dashboard> updateDashboards() {
        return (new GetDashboardsController(
                mDhisManager, mSessionHandler.get(),
                mDashboardHandler, mDashboardToItemsHandler)).run();
    }

    private List<DashboardItem> updateDashboardItems(List<Dashboard> dashboards) {
        if (dashboards == null || dashboards.isEmpty()) {
            return new ArrayList<>();
        }

        Set<String> set = new HashSet<>();
        for (Dashboard dashboard : dashboards) {
            if (dashboard.getDashboardItems() == null ||
                    dashboard.getDashboardItems().isEmpty()) {
                continue;
            }

            for (DashboardItem dashboardItem : dashboard.getDashboardItems()) {
                set.add(dashboardItem.getId());
            }
        }
        return (new GetDashboardItemsController(
                mDhisManager, mSessionHandler.get(),
                mDashboardItemHandler, new ArrayList<>(set))).run();
    }
}