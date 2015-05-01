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

package org.dhis2.android.dashboard.api.persistence;

import android.app.Application;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.RemoteException;

import org.dhis2.android.dashboard.api.persistence.database.DbContract;
import org.dhis2.android.dashboard.api.persistence.handlers.DashboardHandler;
import org.dhis2.android.dashboard.api.persistence.handlers.DashboardItemHandler;
import org.dhis2.android.dashboard.api.persistence.handlers.DashboardsToItemsHandler;
import org.dhis2.android.dashboard.api.persistence.handlers.IModelHandler;
import org.dhis2.android.dashboard.api.persistence.models.Dashboard;
import org.dhis2.android.dashboard.api.persistence.models.DashboardItem;
import org.dhis2.android.dashboard.api.persistence.models.DashboardToItem;

import java.util.ArrayList;

import static org.dhis2.android.dashboard.api.utils.Preconditions.isNull;

/**
 * Created by araz on 29.04.2015.
 */
public final class DbManager {
    private static DbManager mManager;
    private final Context mContext;

    private DbManager(Application application) {
        mContext = isNull(application, "Application object must not be null");
    }

    public static void init(Application application) {
        if (mManager == null) {
            mManager = new DbManager(application);
        }
    }

    private static DbManager getInstance() {
        isNull(mManager, "You have to call init() " +
                "on DbManager before using it");
        return mManager;
    }

    private Context getContext() {
        return mContext;
    }

    public static <T> IModelHandler<T> with(Class<T> clazz) {
        isNull(clazz, "Class object must not be null");

        if (clazz == Dashboard.class) {
            return (IModelHandler<T>) new DashboardHandler(getInstance().getContext());
        } else if (clazz == DashboardItem.class) {
            return (IModelHandler<T>) new DashboardItemHandler(getInstance().getContext());
        } else if (clazz == DashboardToItem.class) {
            return (IModelHandler<T>) new DashboardsToItemsHandler(getInstance().getContext());
        } else {
            throw new IllegalArgumentException("Unsupported type");
        }
    }

    public static void applyBatch(ArrayList<ContentProviderOperation> ops) {
        try {
            getInstance().getContext().getContentResolver()
                    .applyBatch(DbContract.AUTHORITY, ops);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        } catch (OperationApplicationException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> void notifyChange(Class<T> clazz) {
        isNull(clazz, "Class object must not be null");

        ContentResolver resolver = getInstance().getContext()
                .getContentResolver();
        if (clazz == Dashboard.class) {
            resolver.notifyChange(DbContract.Dashboards.CONTENT_URI, null);
        } else if (clazz == DashboardItem.class) {
            resolver.notifyChange(DbContract.DashboardItems.CONTENT_URI, null);
        } else if (clazz == DashboardToItem.class) {
            resolver.notifyChange(DbContract.DashboardsToItems.CONTENT_URI, null);
        } else {
            throw new IllegalArgumentException("Unsupported type");
        }
    }
}
