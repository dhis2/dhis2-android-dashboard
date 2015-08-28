package org.hisp.dhis.android.dashboard.api.api;

import android.content.Context;

import com.raizlabs.android.dbflow.config.FlowManager;

import org.hisp.dhis.android.dashboard.api.models.common.IModelsStore;
import org.hisp.dhis.android.dashboard.api.models.common.ModelsStore;
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
import org.hisp.dhis.android.dashboard.api.models.user.IUserStore;
import org.hisp.dhis.android.dashboard.api.models.user.UserAccountStore;
import org.hisp.dhis.android.dashboard.api.models.user.UserStore;

/**
 * Created by arazabishov on 8/18/15.
 */
public final class Models {
    private static Models models;

    // Dashboard store objects
    private final IDashboardStore dashboardStore;
    private final IDashboardItemStore dashboardItemStore;
    private final IDashboardElementStore dashboardElementStore;
    private final IDashboardItemContentStore dashboardItemContentStore;

    // Interpretation store objects
    private final IInterpretationStore interpretationStore;
    private final IInterpretationCommentStore interpretationCommentStore;
    private final IInterpretationElementStore interpretationElementStore;

    // User store object
    private final IUserAccountStore userAccountStore;
    private final IUserStore userStore;

    // Models store
    private final IModelsStore modelsStore;

    public Models(Context context) {
        FlowManager.init(context);

        dashboardStore = new DashboardStore();
        dashboardItemStore = new DashboardItemStore();
        dashboardElementStore = new DashboardElementStore();
        dashboardItemContentStore = new DashboardItemContentStore();

        interpretationStore = new InterpretationStore();
        interpretationCommentStore = new InterpretationCommentStore();
        interpretationElementStore = new InterpretationElementStore();

        userAccountStore = new UserAccountStore();
        userStore = new UserStore();

        modelsStore = new ModelsStore();
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
        return getInstance().dashboardStore;
    }

    public static IDashboardItemStore dashboardItems() {
        return getInstance().dashboardItemStore;
    }

    public static IDashboardElementStore dashboardElements() {
        return getInstance().dashboardElementStore;
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

    public static IUserStore users() {
        return getInstance().userStore;
    }

    public static IModelsStore modelsStore() {
        return getInstance().modelsStore;
    }
}
