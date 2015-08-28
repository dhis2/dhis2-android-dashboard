package org.hisp.dhis.android.dashboard.api.controllers.common;

import org.hisp.dhis.android.dashboard.api.network.APIException;

/**
 * Created by arazabishov on 8/28/15.
 */
public interface IDataController extends IController {
    void sync() throws APIException;
}
