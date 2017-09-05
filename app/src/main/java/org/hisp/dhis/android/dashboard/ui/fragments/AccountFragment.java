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

package org.hisp.dhis.android.dashboard.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.hisp.dhis.android.dashboard.R;
import org.hisp.dhis.android.dashboard.api.models.UserAccount;
import org.hisp.dhis.android.dashboard.api.persistence.loaders.DbLoader;
import org.hisp.dhis.android.dashboard.api.persistence.loaders.Query;
import org.hisp.dhis.android.dashboard.ui.adapters.AccountFieldAdapter;
import org.hisp.dhis.android.dashboard.ui.models.Field;
import org.hisp.dhis.android.dashboard.ui.views.GridDividerDecoration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Araz Abishov <araz.abishov.gsoc@gmail.com>.
 */
public final class AccountFragment extends BaseFragment implements LoaderCallbacks<List<Field>> {
    private static final int LOADER_ID = 66756123;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;

    AccountFieldAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        mToolbar.setNavigationIcon(R.mipmap.ic_menu);
        mToolbar.setTitle(R.string.account);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleNavigationDrawer();
            }
        });

        mAdapter = new AccountFieldAdapter(getActivity().getApplicationContext(),
                (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new GridDividerDecoration(getActivity()
                .getApplicationContext()));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, getArguments(), this);
    }

    @Override
    public Loader<List<Field>> onCreateLoader(int id, Bundle args) {
        if (LOADER_ID == id) {
            List<DbLoader.TrackedTable> trackedTables = new ArrayList<>();
            trackedTables.add(new DbLoader.TrackedTable(UserAccount.class));
            return new DbLoader<>(getActivity().getApplicationContext(),
                    trackedTables, new UserAccountQuery());
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<List<Field>> loader, List<Field> data) {
        if (loader != null && loader.getId() == LOADER_ID) {
            mAdapter.swapData(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Field>> loader) {
        // stub implementation
    }

    private static class UserAccountQuery implements Query<List<Field>> {

        @Override
        public List<Field> query(Context context) {
            UserAccount userAccount = UserAccount.getCurrentUserAccountFromDb();

            String gender = userAccount.getGender();
            if (gender != null) {
                switch (gender) {
                    case "gender_female": {
                        gender = "Female";
                        break;
                    }
                    case "gender_male": {
                        gender = "Male";
                        break;
                    }
                    case "gender_other": {
                        gender = "Other";
                        break;
                    }
                }
            }
            return Arrays.asList(
                    new Field(getString(context, R.string.first_name), userAccount.getFirstName()),
                    new Field(getString(context, R.string.surname), userAccount.getSurname()),
                    new Field(getString(context, R.string.gender), gender),
                    new Field(getString(context, R.string.birthday), userAccount.getBirthday()),
                    new Field(getString(context, R.string.introduction), userAccount.getIntroduction()),
                    new Field(getString(context, R.string.education), userAccount.getEducation()),
                    new Field(getString(context, R.string.employer), userAccount.getEmployer()),
                    new Field(getString(context, R.string.interests), userAccount.getInterests()),
                    new Field(getString(context, R.string.job_title), userAccount.getJobTitle()),
                    new Field(getString(context, R.string.languages), userAccount.getLanguages()),
                    new Field(getString(context, R.string.email), userAccount.getEmail()),
                    new Field(getString(context, R.string.phone_number), userAccount.getPhoneNumber())
            );
        }

        private static String getString(Context context, int resource) {
            return context.getString(resource);
        }
    }
}
