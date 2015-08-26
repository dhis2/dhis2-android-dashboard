/*
 * Copyright (c) 2014, Araz Abishov
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.dashboard.api.persistence.loaders;

import android.util.Log;

import com.raizlabs.android.dbflow.runtime.FlowContentObserver;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.Model;

import org.hisp.dhis.android.dashboard.api.models.entities.common.meta.DbAction;
import org.hisp.dhis.android.dashboard.api.models.entities.dashboard.Dashboard;
import org.hisp.dhis.android.dashboard.api.models.entities.dashboard.DashboardElement;
import org.hisp.dhis.android.dashboard.api.models.entities.dashboard.DashboardItem;
import org.hisp.dhis.android.dashboard.api.models.entities.flow.Dashboard$Flow;
import org.hisp.dhis.android.dashboard.api.models.entities.flow.DashboardElement$Flow;
import org.hisp.dhis.android.dashboard.api.models.entities.flow.DashboardItem$Flow;

import static org.hisp.dhis.android.dashboard.api.utils.Preconditions.isNull;

public class ModelChangeObserver implements FlowContentObserver.OnModelStateChangedListener {
    private static final String TAG = ModelChangeObserver.class.getSimpleName();

    private final TrackedTable mTrackedTable;
    private final DbLoader<?> mLoader;
    private final FlowContentObserver mObserver;

    public ModelChangeObserver(TrackedTable trackedTable, DbLoader<?> loader) {
        mTrackedTable = isNull(trackedTable, "TrackedTable object must not be null");
        mLoader = isNull(loader, "DbLoader must not be null");
        mObserver = new FlowContentObserver();
    }

    public void registerObserver() {
        Class<? extends BaseModel> trackedModel;

        if (Dashboard.class.equals(mTrackedTable.getTrackedModel())) {
            trackedModel = Dashboard$Flow.class;
        } else if (DashboardItem.class.equals(mTrackedTable.getTrackedModel())) {
            trackedModel = DashboardItem$Flow.class;
        } else if (DashboardElement.class.equals(mTrackedTable.getTrackedModel())) {
            trackedModel = DashboardElement$Flow.class;
        } else {
            throw new IllegalArgumentException("Unsupported model type for tracking: "
                    + mTrackedTable.getTrackedModel());
        }

        mObserver.registerForContentChanges(
                mLoader.getContext(), trackedModel);
        mObserver.addModelChangeListener(this);
    }

    public void unregisterObserver() {
        mObserver.unregisterForContentChanges(mLoader.getContext());
        mObserver.removeModelChangeListener(this);
    }

    @Override
    public void onModelStateChanged(Class<? extends Model> aClass, BaseModel.Action action) {
        Log.d(TAG, "onModelStateChanged() " + aClass.getSimpleName() + ": " + action);

        System.out.println("NOTIFY_LOADER: " + notifyLoader(action));
        if (notifyLoader(action)) {
            mLoader.onContentChanged();
        }
    }

    private boolean notifyLoader(BaseModel.Action action) {
        if (mTrackedTable.getActions().isEmpty()) {
            return true;
        }

        DbAction dbAction = toAction(action);
        for (DbAction modelAction : mTrackedTable.getActions()) {
            if (dbAction.equals(modelAction)) {
                return true;
            }
        }

        return false;
    }

    private static DbAction toAction(BaseModel.Action flowAction) {
        switch (flowAction) {
            case INSERT:
                return DbAction.INSERT;
            case UPDATE:
                return DbAction.UPDATE;
            case SAVE:
                return DbAction.SAVE;
            case DELETE:
                return DbAction.DELETE;
            default: {
                return DbAction.ON_CHANGE;
            }
        }
    }
}
