package org.hisp.dhis.mobile.datacapture.ui.activities;

import android.support.v7.app.ActionBarActivity;

import org.hisp.dhis.mobile.datacapture.utils.BusProvider;

public class BaseActivity extends ActionBarActivity {

    public void onPause() {
        super.onPause();

        BusProvider.getInstance().unregister(this);
    }

    public void onResume() {
        super.onResume();

        BusProvider.getInstance().register(this);
    }
}
