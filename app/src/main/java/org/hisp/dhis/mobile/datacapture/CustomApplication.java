package org.hisp.dhis.mobile.datacapture;

import android.app.Application;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import org.hisp.dhis.mobile.datacapture.api.android.events.OnReportDeleteEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.OnReportPostEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.OnReportSaveEvent;
import org.hisp.dhis.mobile.datacapture.api.managers.DHISManager;
import org.hisp.dhis.mobile.datacapture.ui.activities.LoginActivity;
import org.hisp.dhis.mobile.datacapture.utils.BusProvider;
import org.hisp.dhis.mobile.datacapture.utils.PreferenceUtils;

// TODO subscribe to Unauthorized exceptions
// TODO (in order to show message to user and logout)
public class CustomApplication extends Application {
    private DHISService mDHISService;

    @Override
    public void onCreate() {
        super.onCreate();

        DHISManager manager = DHISManager.getInstance();

        String serverUrl = PreferenceUtils.get(this, LoginActivity.SERVER_URL);
        String credentials = PreferenceUtils.get(this, LoginActivity.USER_CREDENTIALS);

        manager.setServerUrl(serverUrl);
        manager.setCredentials(credentials);

        mDHISService = new DHISService(getBaseContext());
        BusProvider.getInstance().register(mDHISService);
        BusProvider.getInstance().register(this);
    }

    @Subscribe
    public void onReportPostedEvent(OnReportPostEvent event) {
        if (event.getResponseHolder().getException() != null) {
            showMessage("Something gone wrong...");
        } else {
            showMessage(getResources().getString(R.string.report_is_posted));
        }
    }

    @Subscribe
    public void onReportDeletedEvent(OnReportDeleteEvent event) {
        showMessage(getResources().getString(R.string.report_is_deleted));
    }

    @Subscribe
    public void onReportSavedEvent(OnReportSaveEvent event) {
        showMessage(getResources().getString(R.string.report_is_saved));
    }

    private void showMessage(String message) {
        Toast.makeText(getBaseContext(), message,
                Toast.LENGTH_SHORT).show();
    }

}
