package org.hisp.dhis.mobile.datacapture.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import org.hisp.dhis.mobile.datacapture.R;
import org.hisp.dhis.mobile.datacapture.api.managers.DHISManager;


public class LauncherActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DHISManager manager = DHISManager.getInstance();

        Intent intent;
        if (manager.getServerUrl() == null || manager.getCredentials() == null) {
            intent = new Intent(this, LoginActivity.class);
        } else {
            intent = new Intent(this, MenuActivity.class);
        }

        startActivity(intent);
        finish();
    }
}
