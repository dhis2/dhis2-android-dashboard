package org.hisp.dhis.android.dashboard.api.models.interpretation;

import org.hisp.dhis.android.dashboard.api.models.common.IService;
import org.hisp.dhis.android.dashboard.api.models.dashboard.DashboardElement;

/**
 * Created by arazabishov on 8/27/15.
 */
public interface IInterpretationElementService extends IService {
    InterpretationElement createInterpretationElement(Interpretation interpretation,
                                                      DashboardElement dashboardElement,
                                                      String mimeType);
}