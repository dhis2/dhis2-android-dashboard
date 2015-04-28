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

import android.net.Uri;

public final class DbContract {
    public static final String AUTHORITY = "org.dhis2.android.dashboard.api.persistence.database.DbContentProvider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    static interface AccessColumns {
        public static final String DELETE = "delete";
        public static final String EXTERNALIZE = "externalize";
        public static final String MANAGE = "manage";
        public static final String READ = "read";
        public static final String UPDATE = "update";
        public static final String WRITE = "write";
    }

    static interface DashboardColumns extends AccessColumns {
        public static final String TABLE_NAME = "organizationUnitsTable";
        public static final String ID = "id";
        public static final String CREATED = "created";
        public static final String LAST_UPDATED = "lastUpdated";
        public static final String NAME = "name";
        public static final String DISPLAY_NAME = "displayName";
        public static final String ITEM_COUNT = "itemCount";
    }

    static interface DashboardItemColumns extends AccessColumns {
        public static final String TABLE_NAME = "dashboardsTable";
        public static final String ID = "id";
        public static final String CREATED = "created";
        public static final String LAST_UPDATED = "lastUpdated";
        public static final String CONTENT_COUNT = "contentCount";
        public static final String TYPE = "type";
        public static final String SHAPE = "shape";
    }

    public static final class Dashboards implements DashboardColumns {

    }
}
