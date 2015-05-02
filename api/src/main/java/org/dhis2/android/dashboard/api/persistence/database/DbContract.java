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

import android.content.ContentResolver;
import android.net.Uri;

public final class DbContract {
    public static final String AUTHORITY = "org.dhis2.android.dashboard.api.persistence.database.DbContentProvider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface AccessColumns {
        String DELETE = "deleteAccess";
        String EXTERNALIZE = "externalizeAccess";
        String MANAGE = "manageAccess";
        String READ = "readAccess";
        String UPDATE = "updateAccess";
        String WRITE = "writeAccess";
    }

    interface DashboardColumns extends AccessColumns {
        String ID = "id";
        String CREATED = "created";
        String LAST_UPDATED = "lastUpdated";
        String NAME = "name";
        String DISPLAY_NAME = "displayName";
        String ITEM_COUNT = "itemCount";
    }

    interface DashboardItemColumns extends AccessColumns {
        String ID = "id";
        String CREATED = "created";
        String LAST_UPDATED = "lastUpdated";
        String CONTENT_COUNT = "contentCount";
        String TYPE = "type";
        String SHAPE = "shape";
        String MESSAGES = "messages";
        String ELEMENT = "element";
        String DASHBOARD_ID = "dashboardId";
    }

    public static final class Dashboards implements DashboardColumns {
        public static final String TABLE_NAME = "dashboardsTable";
        public static final String DASHBOARDS_PATH = TABLE_NAME;
        public static final String DASHBOARD_ID_PATH = TABLE_NAME + "/*/";
        public static final String DASHBOARD_ID_ITEMS_PATH = DASHBOARD_ID_PATH +
                DashboardItems.TABLE_NAME;

        private static final int DASHBOARD_ID_POSITION = 1;

        public static Uri buildUriWithItems(String dashboardId) {
            return CONTENT_URI.buildUpon()
                    .appendPath(dashboardId)
                    .appendPath(DashboardItems.TABLE_NAME)
                    .build();
        }

        public static String getId(Uri uri) {
            return uri.getPathSegments().get(DASHBOARD_ID_POSITION);
        }

        public static final Uri CONTENT_URI = Uri.withAppendedPath(
                BASE_CONTENT_URI, DASHBOARDS_PATH);
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/org.hisp.dhis2.mobile.Dashboard";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/org.hisp.dhis2.mobile.Dashboard";
    }

    public static final class DashboardItems implements DashboardItemColumns {
        public static final String TABLE_NAME = "dashboardItemsTable";
        public static final String DASHBOARD_ITEMS_PATH = TABLE_NAME;
        public static final String DASHBOARD_ITEM_ID_PATH = TABLE_NAME + "/*/";

        private static final int DASHBOARD_ITEM_ID_POSITION = 1;

        public static String getId(Uri uri) {
            return uri.getPathSegments().get(DASHBOARD_ITEM_ID_POSITION);
        }

        public static final Uri CONTENT_URI = Uri.withAppendedPath(
                BASE_CONTENT_URI, DASHBOARD_ITEMS_PATH);
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/org.hisp.dhis2.mobile.DashboardItem";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/org.hisp.dhis2.mobile.DashboardItem";
    }
}