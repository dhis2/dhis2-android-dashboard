package org.hisp.dhis.android.dashboard.api.models;

import android.content.Context;

import com.raizlabs.android.dbflow.config.FlowManager;

import org.hisp.dhis.android.dashboard.api.models.dashboard.DashboardElementStore;
import org.hisp.dhis.android.dashboard.api.models.dashboard.DashboardItemContentStore;
import org.hisp.dhis.android.dashboard.api.models.dashboard.DashboardItemStore;
import org.hisp.dhis.android.dashboard.api.models.dashboard.DashboardStore;
import org.hisp.dhis.android.dashboard.api.models.dashboard.IDashboardElementStore;
import org.hisp.dhis.android.dashboard.api.models.dashboard.IDashboardItemContentStore;
import org.hisp.dhis.android.dashboard.api.models.dashboard.IDashboardItemStore;
import org.hisp.dhis.android.dashboard.api.models.dashboard.IDashboardStore;
import org.hisp.dhis.android.dashboard.api.models.interpretation.IInterpretationCommentStore;
import org.hisp.dhis.android.dashboard.api.models.interpretation.IInterpretationElementStore;
import org.hisp.dhis.android.dashboard.api.models.interpretation.IInterpretationStore;
import org.hisp.dhis.android.dashboard.api.models.interpretation.InterpretationCommentStore;
import org.hisp.dhis.android.dashboard.api.models.interpretation.InterpretationElementStore;
import org.hisp.dhis.android.dashboard.api.models.interpretation.InterpretationStore;
import org.hisp.dhis.android.dashboard.api.models.user.IUserAccountStore;
import org.hisp.dhis.android.dashboard.api.models.user.UserAccountStore;

/**
 * Created by arazabishov on 8/18/15.
 */
public final class Models {
    private static Models models;

    // Dashboard store objects
    private final IDashboardStore dashboardRepository;
    private final IDashboardItemStore dashboardItemRepository;
    private final IDashboardElementStore dashboardElementRepository;
    private final IDashboardItemContentStore dashboardItemContentStore;

    // Interpretation store objects
    private final IInterpretationStore interpretationStore;
    private final IInterpretationCommentStore interpretationCommentStore;
    private final IInterpretationElementStore interpretationElementStore;

    // User store object
    private final IUserAccountStore userAccountStore;

    public Models(Context context) {
        FlowManager.init(context);

        dashboardRepository = new DashboardStore();
        dashboardItemRepository = new DashboardItemStore();
        dashboardElementRepository = new DashboardElementStore();
        dashboardItemContentStore = new DashboardItemContentStore();

        interpretationStore = new InterpretationStore();
        interpretationCommentStore = new InterpretationCommentStore();
        interpretationElementStore = new InterpretationElementStore();

        userAccountStore = new UserAccountStore();
    }

    public static void init(Context context) {
        models = new Models(context);
    }

    private static Models getInstance() {
        if (models == null) {
            throw new IllegalArgumentException("You should call inti method first");
        }

        return models;
    }

    public static IDashboardStore dashboards() {
        return getInstance().dashboardRepository;
    }

    public static IDashboardItemStore dashboardItems() {
        return getInstance().dashboardItemRepository;
    }

    public static IDashboardElementStore dashboardElements() {
        return getInstance().dashboardElementRepository;
    }

    public static IDashboardItemContentStore dashboardItemContent() {
        return getInstance().dashboardItemContentStore;
    }

    public static IInterpretationStore interpretations() {
        return getInstance().interpretationStore;
    }

    public static IInterpretationCommentStore interpretationComments() {
        return getInstance().interpretationCommentStore;
    }

    public static IInterpretationElementStore interpretationElements() {
        return getInstance().interpretationElementStore;
    }

    public static IUserAccountStore userAccount() {
        return getInstance().userAccountStore;
    }
}
