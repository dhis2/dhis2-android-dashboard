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

package org.hisp.dhis.android.dashboard.api.models.entities.flow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.Table;

import org.hisp.dhis.android.dashboard.api.models.entities.common.meta.State;
import org.hisp.dhis.android.dashboard.api.models.entities.dashboard.DashboardElement;
import org.hisp.dhis.android.dashboard.api.models.meta.DbDhis;

import java.util.ArrayList;
import java.util.List;

@Table(databaseName = DbDhis.NAME)
public final class DashboardElementFlow extends BaseIdentifiableObjectFlow {
    static final String DASHBOARD_ITEM_KEY = "dashboardItem";

    @Column
    @NotNull
    State state;

    @Column
    @NotNull
    @ForeignKey(
            references = {
                    @ForeignKeyReference(columnName = DASHBOARD_ITEM_KEY, columnType = long.class, foreignColumnName = "id")
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE
    )
    DashboardItemFlow dashboardItem;

    public DashboardElementFlow() {
        state = State.SYNCED;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public DashboardItemFlow getDashboardItem() {
        return dashboardItem;
    }

    public void setDashboardItem(DashboardItemFlow dashboardItem) {
        this.dashboardItem = dashboardItem;
    }

    public static DashboardElementFlow fromModel(DashboardElement dashboardElement) {
        DashboardElementFlow dashboardElementFlow = new DashboardElementFlow();
        dashboardElementFlow.setId(dashboardElement.getId());
        dashboardElementFlow.setUId(dashboardElement.getUId());
        dashboardElementFlow.setCreated(dashboardElement.getCreated());
        dashboardElementFlow.setLastUpdated(dashboardElement.getLastUpdated());
        dashboardElementFlow.setAccess(dashboardElement.getAccess());
        dashboardElementFlow.setName(dashboardElement.getName());
        dashboardElementFlow.setDisplayName(dashboardElement.getDisplayName());
        dashboardElementFlow.setDashboardItem(DashboardItemFlow
                .fromModel(dashboardElement.getDashboardItem()));
        dashboardElementFlow.setState(dashboardElement.getState());
        return dashboardElementFlow;
    }

    public static DashboardElement toModel(DashboardElementFlow dashboardElementFlow) {
        DashboardElement dashboardElement = new DashboardElement();
        dashboardElement.setId(dashboardElementFlow.getId());
        dashboardElement.setUId(dashboardElementFlow.getUId());
        dashboardElement.setCreated(dashboardElementFlow.getCreated());
        dashboardElement.setLastUpdated(dashboardElementFlow.getLastUpdated());
        dashboardElement.setAccess(dashboardElementFlow.getAccess());
        dashboardElement.setName(dashboardElementFlow.getName());
        dashboardElement.setDisplayName(dashboardElementFlow.getDisplayName());
        dashboardElement.setDashboardItem(DashboardItemFlow
                .toModel(dashboardElementFlow.getDashboardItem()));
        dashboardElement.setState(dashboardElementFlow.getState());
        return dashboardElement;
    }

    public static List<DashboardElement> toModels(List<DashboardElementFlow> elementFlows) {
        List<DashboardElement> dashboardElements = new ArrayList<>();

        if (elementFlows != null && !elementFlows.isEmpty()) {
            for (DashboardElementFlow dashboardElementFlow : elementFlows) {
                dashboardElements.add(toModel(dashboardElementFlow));
            }
        }

        return dashboardElements;
    }
}