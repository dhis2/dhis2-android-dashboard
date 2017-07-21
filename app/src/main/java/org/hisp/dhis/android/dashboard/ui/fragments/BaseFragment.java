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

package org.hisp.dhis.android.dashboard.ui.fragments;

import static android.R.attr.editable;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.hisp.dhis.android.dashboard.DhisService;
import org.hisp.dhis.android.dashboard.R;
import org.hisp.dhis.android.dashboard.api.utils.EventBusProvider;
import org.hisp.dhis.android.dashboard.ui.activities.BaseActivity;
import org.hisp.dhis.android.dashboard.ui.activities.INavigationCallback;

public class BaseFragment extends Fragment {
    INavigationCallback mNavCallback;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof INavigationCallback) {
            mNavCallback = (INavigationCallback) activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mNavCallback = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBusProvider.unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBusProvider.register(this);
    }

    public void toggleNavigationDrawer() {
        if (mNavCallback != null) {
            mNavCallback.toggleNavigationDrawer();
        } else {
            throw new UnsupportedOperationException("The fragment must be attached to Activity " +
                    "which implements INavigationCallback interface");
        }
    }

    public void onBackPressed() {
        if (isAdded()) {
            getActivity().onBackPressed();
        }
    }

    public DhisService getDhisService() {
        if (isAdded() && getActivity() instanceof BaseActivity) {
            return ((BaseActivity) getActivity()).getDhisService();
        } else {
            throw new UnsupportedOperationException("The fragment must be attached to Activity " +
                    "which extends BaseActivity");
        }
    }

    public boolean isDhisServiceBound() {
        if (isAdded() && getActivity() instanceof BaseActivity) {
            return ((BaseActivity) getActivity()).isDhisServiceBound();
        } else {
            throw new UnsupportedOperationException("The fragment must be attached to Activity " +
                    "which extends BaseActivity");
        }
    }
}
