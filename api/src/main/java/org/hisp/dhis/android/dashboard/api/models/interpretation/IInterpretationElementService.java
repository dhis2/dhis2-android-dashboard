package org.hisp.dhis.android.dashboard.api.models.interpretation;

import org.hisp.dhis.android.dashboard.api.models.dashboard.DashboardElement;
import org.hisp.dhis.android.dashboard.api.models.interpretation.Interpretation;
import org.hisp.dhis.android.dashboard.api.models.interpretation.InterpretationElement;
import org.hisp.dhis.android.dashboard.api.models.common.IService;

/**
 * Created by arazabishov on 8/27/15.
 */
public interface IInterpretationElementService extends IService {
    InterpretationElement createInterpretationElement(Interpretation interpretation,
                                                      DashboardElement dashboardElement,
                                                      String mimeType);
}