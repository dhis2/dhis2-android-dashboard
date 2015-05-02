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

import org.dhis2.android.dashboard.api.DhisManager;
import org.dhis2.android.dashboard.api.network.APIException;
import org.dhis2.android.dashboard.api.persistence.DbManager;
import org.dhis2.android.dashboard.api.persistence.handlers.SessionHandler;
import org.dhis2.android.dashboard.api.persistence.models.Dashboard;
import org.dhis2.android.dashboard.api.persistence.models.DashboardItem;
import org.dhis2.android.dashboard.api.persistence.models.DashboardToItem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.dhis2.android.dashboard.api.utils.DbUtils.filter;
import static org.dhis2.android.dashboard.api.utils.DbUtils.toMap;

public final class DashboardSyncController implements IController<Object> {
    private final DhisManager mDhisManager;
    private final SessionHandler mSessionHandler;

    public DashboardSyncController(DhisManager dhisManager, SessionHandler handler) {
        mDhisManager = dhisManager;
        mSessionHandler = handler;
    }

    @Override
    public Object run() throws APIException {
        List<Dashboard> dashboards = filter(updateDashboards());
        List<DashboardItem> items = filter(updateDashboardItems(dashboards));
        List<DashboardToItem> relations = buildRelationShip(dashboards, items);

        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        ops.addAll(DbManager.with(Dashboard.class).sync(dashboards));
        ops.addAll(DbManager.with(DashboardItem.class).sync(items));
        ops.addAll(DbManager.with(DashboardToItem.class).sync(relations));

        if (!ops.isEmpty()) {
            DbManager.applyBatch(ops);
            DbManager.notifyChange(Dashboard.class);
            DbManager.notifyChange(DashboardItem.class);
        }

        return new Object();
    }

    private List<Dashboard> updateDashboards() {
        return (new GetDashboardsController(
                mDhisManager, mSessionHandler.get())
        ).run();
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
                mDhisManager, mSessionHandler.get(), new ArrayList<>(set))
        ).run();
    }

    private List<DashboardToItem> buildRelationShip(List<Dashboard> dashboards,
                                                    List<DashboardItem> dashboardItems) {
        List<DashboardToItem> dashboardToItems = new ArrayList<>();
        Map<String, DashboardItem> dashboardItemMap = toMap(dashboardItems);

        for (Dashboard dashboard : dashboards) {
            if (dashboard.getDashboardItems() == null
                    || dashboard.getDashboardItems().isEmpty()) {
                continue;
            }

            for (DashboardItem item : dashboard.getDashboardItems()) {
                if (dashboardItemMap.get(item.getId()) != null) {
                    dashboardToItems.add(new DashboardToItem(dashboard.getId(), item.getId()));
                }
            }
        }

        return dashboardToItems;
    }
}