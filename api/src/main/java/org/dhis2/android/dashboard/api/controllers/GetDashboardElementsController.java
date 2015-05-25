/*
 * Copyright (c) 2015, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.dhis2.android.dashboard.api.controllers;

import org.dhis2.android.dashboard.api.DhisManager;
import org.dhis2.android.dashboard.api.models.DashboardElement;
import org.dhis2.android.dashboard.api.network.APIException;
import org.dhis2.android.dashboard.api.network.models.Session;
import org.dhis2.android.dashboard.api.network.tasks.GetDashboardElementsTask;

import java.util.ArrayList;
import java.util.List;

public final class GetDashboardElementsController implements IController<List<DashboardElement>> {
    private final DhisManager mDhisManager;
    private final Session mSession;

    public GetDashboardElementsController(DhisManager dhisManager, Session session) {
        mDhisManager = dhisManager;
        mSession = session;
    }

    @Override public List<DashboardElement> run() throws APIException {
        return getNewDashboardElements();
    }

    private List<DashboardElement> getNewDashboardElements() throws APIException {
        List<DashboardElement> elements = new ArrayList<>();
        elements.addAll(getNewDashboardElementsByType(DashboardElement.TYPE_CHART));
        elements.addAll(getNewDashboardElementsByType(DashboardElement.TYPE_EVENT_CHART));
        elements.addAll(getNewDashboardElementsByType(DashboardElement.TYPE_MAP));
        elements.addAll(getNewDashboardElementsByType(DashboardElement.TYPE_REPORT_TABLE));
        elements.addAll(getNewDashboardElementsByType(DashboardElement.TYPE_EVENT_REPORT));
        elements.addAll(getNewDashboardElementsByType(DashboardElement.TYPE_USERS));
        elements.addAll(getNewDashboardElementsByType(DashboardElement.TYPE_REPORTS));
        elements.addAll(getNewDashboardElementsByType(DashboardElement.TYPE_RESOURCES));
        return elements;
    }

    private List<DashboardElement> getNewDashboardElementsByType(String type) {
        List<DashboardElement> elements = (new GetDashboardElementsTask(mDhisManager,
                mSession.getServerUri(), mSession.getCredentials(), type).run());
        for (DashboardElement element: elements) {
            element.setType(type);
        }
        return elements;
    }
}
