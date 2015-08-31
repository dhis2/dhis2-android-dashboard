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

package org.hisp.dhis.android.dashboard.ui.fragments.dashboard;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.hisp.dhis.android.dashboard.R;
import org.hisp.dhis.android.dashboard.api.api.Dhis2;
import org.hisp.dhis.android.dashboard.api.api.Models;
import org.hisp.dhis.android.dashboard.api.models.dashboard.Dashboard;
import org.hisp.dhis.android.dashboard.api.models.dashboard.DashboardItemContent;
import org.hisp.dhis.android.dashboard.api.persistence.loaders.DbLoader;
import org.hisp.dhis.android.dashboard.api.persistence.loaders.Query;
import org.hisp.dhis.android.dashboard.api.persistence.loaders.TrackedTable;
import org.hisp.dhis.android.dashboard.api.utils.EventBusProvider;
import org.hisp.dhis.android.dashboard.ui.adapters.DashboardItemSearchDialogAdapter;
import org.hisp.dhis.android.dashboard.ui.adapters.DashboardItemSearchDialogAdapter.OptionAdapterValue;
import org.hisp.dhis.android.dashboard.ui.events.UiEvent;
import org.hisp.dhis.android.dashboard.ui.fragments.BaseDialogFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnTextChanged;

public class DashboardItemAddFragment extends BaseDialogFragment
        implements PopupMenu.OnMenuItemClickListener, LoaderCallbacks<List<OptionAdapterValue>> {
    private static final String DASHBOARD_ID = "arg:dashboardId";
    private static final String TAG = DashboardItemAddFragment.class.getSimpleName();
    private static final int LOADER_ID = 3451234;

    @Bind(R.id.filter_options)
    EditText mFilter;

    @Bind(R.id.dialog_label)
    TextView mDialogLabel;

    @Bind(R.id.simple_listview)
    ListView mListView;

    @Bind(R.id.filter_resources)
    ImageView mFilterResources;

    PopupMenu mResourcesMenu;
    DashboardItemSearchDialogAdapter mAdapter;

    Dashboard mDashboard;

    public static DashboardItemAddFragment newInstance(long dashboardId) {
        Bundle args = new Bundle();
        args.putLong(DASHBOARD_ID, dashboardId);

        DashboardItemAddFragment fragment = new DashboardItemAddFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE,
                R.style.Theme_AppCompat_Light_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_dashboard_item_add, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        long dashboardId = getArguments().getLong(DASHBOARD_ID);
        mDashboard = Models.dashboards().query(dashboardId);

        ButterKnife.bind(this, view);

        InputMethodManager imm = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mFilter.getWindowToken(), 0);

        mAdapter = new DashboardItemSearchDialogAdapter(
                LayoutInflater.from(getActivity()));
        mListView.setAdapter(mAdapter);
        mDialogLabel.setText(getString(R.string.add_dashboard_item));

        mResourcesMenu = new PopupMenu(getActivity(), mFilterResources);
        mResourcesMenu.inflate(R.menu.menu_filter_resources);
        mResourcesMenu.setOnMenuItemClickListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        queryApiResources();
    }

    @OnTextChanged(value = R.id.filter_options,
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    @SuppressWarnings("unused")
    public void afterTextChanged(Editable s) {
        mAdapter.getFilter().filter(s.toString());
    }

    @OnClick({R.id.close_dialog_button, R.id.filter_resources})
    @SuppressWarnings("unused")
    public void onButtonClick(View v) {
        if (R.id.close_dialog_button == v.getId()) {
            dismiss();
        } else if (R.id.filter_resources == v.getId()) {
            mResourcesMenu.show();
        }
    }

    @SuppressWarnings("unused")
    @OnItemClick(R.id.simple_listview)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        OptionAdapterValue adapterValue = mAdapter.getItem(position);
        DashboardItemContent resource = Models.dashboardItemContent().query(adapterValue.id);
        Dhis2.dashboards().addDashboardContent(mDashboard, resource);

        if (isDhisServiceBound()) {
            getDhisService().syncDashboards();
            EventBusProvider.post(new UiEvent(UiEvent.UiEventType.SYNC_DASHBOARDS));
        }

        dismiss();
    }

    public void show(FragmentManager fragmentManager) {
        show(fragmentManager, TAG);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        item.setChecked(!item.isChecked());
        queryApiResources();
        return false;
    }

    @Override
    public Loader<List<OptionAdapterValue>> onCreateLoader(int id, Bundle args) {
        List<TrackedTable> trackedTables = Arrays.asList(
                new TrackedTable(DashboardItemContent.class));
        return new DbLoader<>(getActivity().getApplicationContext(),
                trackedTables, new DbQuery(getTypesToInclude()));
    }

    @Override
    public void onLoadFinished(Loader<List<OptionAdapterValue>> loader,
                               List<OptionAdapterValue> data) {
        if (loader != null && loader.getId() == LOADER_ID) {
            mAdapter.swapData(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<OptionAdapterValue>> loader) {
        if (loader != null && loader.getId() == LOADER_ID) {
            mAdapter.swapData(null);
        }
    }

    private void queryApiResources() {
        getLoaderManager().restartLoader(LOADER_ID, getArguments(), this);
    }

    private List<String> getTypesToInclude() {
        List<String> typesToInclude = new ArrayList<>();
        if (isItemChecked(R.id.type_charts)) {
            typesToInclude.add(DashboardItemContent.TYPE_CHART);
        }
        if (isItemChecked(R.id.type_event_charts)) {
            typesToInclude.add(DashboardItemContent.TYPE_EVENT_CHART);
        }
        if (isItemChecked(R.id.type_maps)) {
            typesToInclude.add(DashboardItemContent.TYPE_MAP);
        }
        if (isItemChecked(R.id.type_report_tables)) {
            typesToInclude.add(DashboardItemContent.TYPE_REPORT_TABLES);
        }
        if (isItemChecked(R.id.type_event_reports)) {
            typesToInclude.add(DashboardItemContent.TYPE_EVENT_REPORT);
        }
        if (isItemChecked(R.id.type_users)) {
            typesToInclude.add(DashboardItemContent.TYPE_USERS);
        }
        if (isItemChecked(R.id.type_reports)) {
            typesToInclude.add(DashboardItemContent.TYPE_REPORTS);
        }
        if (isItemChecked(R.id.type_resources)) {
            typesToInclude.add(DashboardItemContent.TYPE_RESOURCES);
        }

        return typesToInclude;
    }

    private boolean isItemChecked(int id) {
        return mResourcesMenu.getMenu().findItem(id).isChecked();
    }

    static class DbQuery implements Query<List<OptionAdapterValue>> {
        private List<String> mTypes;

        public DbQuery(List<String> types) {
            mTypes = types;
        }

        @Override
        public List<OptionAdapterValue> query(Context context) {
            if (mTypes.isEmpty()) {
                return new ArrayList<>();
            }

            List<DashboardItemContent> resources = Models.dashboardItemContent().query(mTypes);
            List<OptionAdapterValue> adapterValues = new ArrayList<>();
            for (DashboardItemContent dashboardItemContent : resources) {
                adapterValues.add(new OptionAdapterValue(dashboardItemContent.getUId(),
                        dashboardItemContent.getDisplayName()));
            }

            return adapterValues;
        }
    }
}
