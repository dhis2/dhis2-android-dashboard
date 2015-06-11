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

package org.dhis2.android.dashboard.api.models;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.annotation.UniqueGroup;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.dhis2.android.dashboard.api.persistence.DbDhis;

@Table(databaseName = DbDhis.NAME, uniqueColumnGroups = {
        @UniqueGroup(groupNumber = ElementToItemRelation.UNIQUE_ITEM_TO_ELEMENT_GROUP, uniqueConflict = ConflictAction.FAIL)
})
public final class ElementToItemRelation extends BaseModel implements RelationModel {
    static final int UNIQUE_ITEM_TO_ELEMENT_GROUP = 1;
    static final String DASHBOARD_ITEM_KEY = "dashboardItem";
    static final String DASHBOARD_ELEMENT_KEY = "dashboardElement";

    @Column @PrimaryKey(autoincrement = true) long localId;
    @Column @NotNull State state;

    @Column @NotNull @Unique(unique = false, uniqueGroups = {UNIQUE_ITEM_TO_ELEMENT_GROUP})
    @ForeignKey(
            references = {
                    @ForeignKeyReference(columnName = DASHBOARD_ITEM_KEY, columnType = long.class, foreignColumnName = "localId")
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE
    ) DashboardItem dashboardItem;

    @Column @NotNull @Unique(unique = false, uniqueGroups = {UNIQUE_ITEM_TO_ELEMENT_GROUP})
    @ForeignKey(
            references = {
                    @ForeignKeyReference(columnName = DASHBOARD_ELEMENT_KEY, columnType = String.class, foreignColumnName = "id")
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE
    ) DashboardElement dashboardElement;

    public long getLocalId() {
        return localId;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public DashboardItem getDashboardItem() {
        return dashboardItem;
    }

    public void setDashboardItem(DashboardItem dashboardItem) {
        this.dashboardItem = dashboardItem;
    }

    public DashboardElement getDashboardElement() {
        return dashboardElement;
    }

    public void setDashboardElement(DashboardElement dashboardElement) {
        this.dashboardElement = dashboardElement;
    }

    @Override public String getFirstKey() {
        return dashboardItem.getId() + "";
    }

    @Override public String getSecondKey() {
        return dashboardElement.getId();
    }
}
