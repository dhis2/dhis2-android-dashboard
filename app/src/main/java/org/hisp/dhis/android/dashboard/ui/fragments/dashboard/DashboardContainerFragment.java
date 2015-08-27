/*
 * Copyright (c) 2015, dhis2
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.dashboard.ui.fragments.dashboard;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.hisp.dhis.android.dashboard.R;
import org.hisp.dhis.android.dashboard.api.models.Models;
import org.hisp.dhis.android.dashboard.api.models.common.meta.DbAction;
import org.hisp.dhis.android.dashboard.api.models.common.meta.State;
import org.hisp.dhis.android.dashboard.api.models.dashboard.Dashboard;
import org.hisp.dhis.android.dashboard.api.persistence.loaders.DbLoader;
import org.hisp.dhis.android.dashboard.api.persistence.loaders.Query;
import org.hisp.dhis.android.dashboard.api.persistence.loaders.TrackedTable;
import org.hisp.dhis.android.dashboard.ui.fragments.BaseFragment;

import java.util.Arrays;
import java.util.List;

/**
 * @author Araz Abishov <araz.abishov.gsoc@gmail.com>.
 *         <p/>
 *         This fragment is used to make decision, whether to show fragment with
 *         dashboards or fragment with message.
 */
public class DashboardContainerFragment extends BaseFragment
        implements LoaderManager.LoaderCallbacks<Boolean> {
    private static final int LOADER_ID = 3465345;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_empty, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, getArguments(), this);
    }

    @Override
    public Loader<Boolean> onCreateLoader(int id, Bundle args) {
        List<DbAction> actionsToTrack = Arrays.asList(
                DbAction.INSERT, DbAction.DELETE);
        List<TrackedTable> trackedTables = Arrays.asList(
                new TrackedTable(Dashboard.class, actionsToTrack));
        return new DbLoader<>(getActivity().getApplicationContext(),
                trackedTables, new DashboardsQuery());
    }

    @Override
    public void onLoadFinished(Loader<Boolean> loader, Boolean hasData) {
        if (loader != null && loader.getId() == LOADER_ID) {
            if (hasData) {
                // we don't want to attach the same fragment
                if (!isFragmentAttached(DashboardViewPagerFragment.TAG)) {
                    attachFragment(new DashboardViewPagerFragment(),
                            DashboardViewPagerFragment.TAG);
                }
            } else {
                if (!isFragmentAttached(DashboardEmptyFragment.TAG)) {
                    attachFragment(new DashboardEmptyFragment(),
                            DashboardEmptyFragment.TAG);
                }
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Boolean> loader) {
        // stub implementation
    }

    private void attachFragment(Fragment fragment, String tag) {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.fragment_content_frame, fragment, tag)
                .commitAllowingStateLoss();
    }

    private boolean isFragmentAttached(String tag) {
        return getChildFragmentManager().findFragmentByTag(tag) != null;
    }

    private static class DashboardsQuery implements Query<Boolean> {

        @Override
        public Boolean query(Context context) {
            List<Dashboard> dashboards = Models.dashboards().filter(State.TO_DELETE);
            System.out.println("*** DASHBOARDS *** " + dashboards);
            return dashboards != null && dashboards.size() > 0;
        }
    }
}
