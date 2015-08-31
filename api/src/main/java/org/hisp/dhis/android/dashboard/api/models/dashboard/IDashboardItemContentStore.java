package org.hisp.dhis.android.dashboard.api.models.dashboard;

import org.hisp.dhis.android.dashboard.api.models.common.IStore;

import java.util.List;

/**
 * Created by arazabishov on 8/26/15.
 */
public interface IDashboardItemContentStore extends IStore<DashboardItemContent> {
    List<DashboardItemContent> query(List<String> type);
}