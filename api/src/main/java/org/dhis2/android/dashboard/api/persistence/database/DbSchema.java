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

package org.dhis2.android.dashboard.api.persistence.database;

import org.dhis2.android.dashboard.api.persistence.database.DbContract.DashboardItems;
import org.dhis2.android.dashboard.api.persistence.database.DbContract.Dashboards;

public final class DbSchema {

    public static final String CREATE_DASHBOARD_TABLE = "CREATE TABLE " + Dashboards.TABLE_NAME + "(" +
            Dashboards.ID + " TEXT PRIMARY KEY," +
            Dashboards.CREATED + " TEXT NOT NULL," +
            Dashboards.LAST_UPDATED + " TEXT NOT NULL," +
            Dashboards.NAME + " TEXT," +
            Dashboards.DISPLAY_NAME + " TEXT," +
            Dashboards.ITEM_COUNT + " INTEGER," +
            Dashboards.DELETE + " INTEGER NOT NULL," +
            Dashboards.EXTERNALIZE + " INTEGER NOT NULL," +
            Dashboards.MANAGE + " INTEGER NOT NULL," +
            Dashboards.READ + " INTEGER NOT NULL," +
            Dashboards.UPDATE + " INTEGER NOT NULL," +
            Dashboards.WRITE + " INTEGER NOT NULL" + ")";

    public static final String DROP_DASHBOARD_TABLE = "DROP TABLE IF EXISTS " + Dashboards.TABLE_NAME;

    public static final String CREATE_DASHBOARD_ITEMS_TABLE = "CREATE TABLE " + DashboardItems.TABLE_NAME + "(" +
            DashboardItems.ID + " TEXT PRIMARY KEY," +
            DashboardItems.CREATED + " TEXT NOT NULL," +
            DashboardItems.LAST_UPDATED + " TEXT NOT NULL," +
            DashboardItems.CONTENT_COUNT + " INTEGER," +
            DashboardItems.TYPE + " TEXT NOT NULL," +
            DashboardItems.SHAPE + " TEXT NOT NULL," +
            DashboardItems.DELETE + " INTEGER NOT NULL," +
            DashboardItems.EXTERNALIZE + " INTEGER NOT NULL," +
            DashboardItems.MANAGE + " INTEGER NOT NULL," +
            DashboardItems.READ + " INTEGER NOT NULL," +
            DashboardItems.UPDATE + " INTEGER NOT NULL," +
            DashboardItems.WRITE + " INTEGER NOT NULL," +

            DashboardItems.CHART + " TEXT," +
            DashboardItems.EVENT_CHART + " TEXT," +
            DashboardItems.MAP + " TEXT," +

            DashboardItems.REPORT_TABLE + " TEXT," +
            DashboardItems.EVENT_REPORT + " TEXT," +
            DashboardItems.USERS + " TEXT," +

            DashboardItems.REPORTS + " TEXT," +
            DashboardItems.RESOURCES + " TEXT," +

            DashboardItems.DASHBOARD_ID + " TEXT NOT NULL," +
            " FOREIGN KEY " + "(" + DashboardItems.DASHBOARD_ID + ")" +
            " REFERENCES " + Dashboards.TABLE_NAME + "(" + Dashboards.ID + ")" +
            " ON DELETE CASCADE " + ")";

    public static final String DROP_DASHBOARD_ITEMS_TABLE = "DROP TABLE IF EXISTS " + DashboardItems.TABLE_NAME;

    public static final String UNIT_JOIN_DASHBOARD_ITEMS_TABLE =
            Dashboards.TABLE_NAME + " LEFT OUTER JOIN " + DashboardItems.TABLE_NAME +
                    " ON " + Dashboards.TABLE_NAME + "." + Dashboards.ID +
                    " = " + DashboardItems.TABLE_NAME + "." + DashboardItems.DASHBOARD_ID;
}