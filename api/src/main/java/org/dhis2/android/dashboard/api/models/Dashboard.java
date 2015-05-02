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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Comparator;
import java.util.List;

public class Dashboard extends BaseIdentifiableModel {
    public static final NameComparator NAME_COMPARATOR = new NameComparator();

    @JsonProperty("displayName") private String displayName;
    @JsonProperty("itemCount") private long itemCount;
    @JsonProperty("access") private Access access;
    @JsonProperty("dashboardItems") List<DashboardItem> dashboardItems;

    @JsonIgnore public String getDisplayName() {
        return displayName;
    }

    @JsonIgnore public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @JsonIgnore public long getItemCount() {
        return itemCount;
    }

    @JsonIgnore public void setItemCount(long itemCount) {
        this.itemCount = itemCount;
    }

    @JsonIgnore public Access getAccess() {
        return access;
    }

    @JsonIgnore public void setAccess(Access access) {
        this.access = access;
    }

    @JsonIgnore public List<DashboardItem> getDashboardItems() {
        return dashboardItems;
    }

    @JsonIgnore public void setDashboardItems(List<DashboardItem> dashboardItems) {
        this.dashboardItems = dashboardItems;
    }

    @JsonIgnore @Override
    public boolean isItemComplete() {
        return super.isItemComplete() && access != null;
    }

    @JsonIgnore @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        String baseString = super.toString();
        builder.append(baseString);

        builder.append(" displayName: ");
        builder.append(displayName);

        builder.append(" itemCount: " );
        builder.append(itemCount);

        builder.append(" access: ");
        builder.append(access == null ? "null" : access.toString());

        return builder.toString();
    }

    public static class NameComparator implements Comparator<Dashboard> {

        @Override public int compare(Dashboard first, Dashboard second) {
            if (first == null || second == null) {
                return 0;
            }
            return String.CASE_INSENSITIVE_ORDER.compare(first.getName(), second.getName());
        }
    }
}