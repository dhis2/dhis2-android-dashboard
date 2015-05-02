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

import org.dhis2.android.dashboard.api.DhisManager;
import org.dhis2.android.dashboard.api.network.APIException;
import org.dhis2.android.dashboard.api.network.models.Session;
import org.dhis2.android.dashboard.api.network.tasks.GetDashboardItemsTask;
import org.dhis2.android.dashboard.api.persistence.DbManager;
import org.dhis2.android.dashboard.api.models.DashboardItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.dhis2.android.dashboard.api.utils.DbUtils.toMap;

public final class GetDashboardItemsController implements IController<List<DashboardItem>> {
    private final DhisManager mDhisManager;
    private final Session mSession;
    private final List<String> mIds;

    public GetDashboardItemsController(DhisManager dhisManager,
                                       Session session, List<String> ids) {
        mDhisManager = dhisManager;
        mSession = session;
        mIds = ids;
    }

    @Override
    public List<DashboardItem> run() throws APIException {
        Map<String, DashboardItem> newBaseItems = getNewBaseDashboardItems();
        Map<String, DashboardItem> oldItems = getOldFullDashboardItems();

        List<String> toDownload = new ArrayList<>();
        for (String itemId : newBaseItems.keySet()) {
            DashboardItem newItem = newBaseItems.get(itemId);
            DashboardItem oldItem = oldItems.get(itemId);

            if (oldItem == null) {
                toDownload.add(itemId);
                continue;
            }

            if (newItem.getLastUpdated().isAfter(oldItem.getLastUpdated())) {
                toDownload.add(itemId);
            }
        }

        Map<String, DashboardItem> newItems = getNewFullDashboardItems(toDownload);
        List<DashboardItem> combinedItems = new ArrayList<>();
        for (String itemId : newBaseItems.keySet()) {
            DashboardItem newItem = newItems.get(itemId);
            DashboardItem oldItem = oldItems.get(itemId);

            if (newItem != null) {
                combinedItems.add(newItem);
                continue;
            }

            if (oldItem != null) {
                combinedItems.add(oldItem);
            }
        }
        return combinedItems;
    }

    private Map<String, DashboardItem> getNewBaseDashboardItems() throws APIException {
        return toMap(
                (new GetDashboardItemsTask(mDhisManager,
                        mSession.getServerUri(), mSession.getCredentials(), mIds, true).run()));
    }

    private Map<String, DashboardItem> getNewFullDashboardItems(List<String> ids) throws APIException {
        if (ids != null && !ids.isEmpty()) {
            return toMap(
                    (new GetDashboardItemsTask(mDhisManager,
                            mSession.getServerUri(), mSession.getCredentials(), ids, false).run()));
        } else {
            return new HashMap<>();
        }
    }

    private Map<String, DashboardItem> getOldFullDashboardItems() {
        return toMap(DbManager.with(DashboardItem.class).query());
    }
}