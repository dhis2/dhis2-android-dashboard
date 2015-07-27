package org.dhis2.android.dashboard.api.network;

import org.dhis2.android.dashboard.api.persistence.preferences.ResourceType;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Araz Abishov <araz.abishov.gsoc@gmail.com>.
 */
public final class SessionManager {
    private static SessionManager mSessionManager;
    private final Set<ResourceType> mResources;

    private SessionManager() {
        mResources = new HashSet<>();
    }

    public static SessionManager getInstance() {
        if (mSessionManager == null) {
            mSessionManager = new SessionManager();
        }

        return mSessionManager;
    }

    public void delete() {
        mResources.clear();
    }

    public void setResourceTypeSynced(ResourceType resourceType) {
        mResources.add(resourceType);
    }

    public boolean isResourceTypeSynced(ResourceType resourceType) {
        return mResources.contains(resourceType);
    }
}
