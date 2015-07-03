/*
 * Copyright (c) 2015, dhis2
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.dhis2.android.dashboard.api.controllers;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.From;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.sql.language.Where;

import org.dhis2.android.dashboard.api.DhisManager;
import org.dhis2.android.dashboard.api.models.Interpretation;
import org.dhis2.android.dashboard.api.models.Interpretation$Table;
import org.dhis2.android.dashboard.api.models.InterpretationComment;
import org.dhis2.android.dashboard.api.models.InterpretationComment$Table;
import org.dhis2.android.dashboard.api.models.InterpretationElement;
import org.dhis2.android.dashboard.api.models.InterpretationElement$Table;
import org.dhis2.android.dashboard.api.models.User;
import org.dhis2.android.dashboard.api.models.meta.DbOperation;
import org.dhis2.android.dashboard.api.models.meta.State;
import org.dhis2.android.dashboard.api.network.APIException;
import org.dhis2.android.dashboard.api.network.DhisApi;
import org.dhis2.android.dashboard.api.network.RepoManager;
import org.dhis2.android.dashboard.api.persistence.preferences.DateTimeManager;
import org.dhis2.android.dashboard.api.persistence.preferences.DateTimeManager.ResourceType;
import org.dhis2.android.dashboard.api.utils.DbUtils;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import retrofit.RetrofitError;

import static org.dhis2.android.dashboard.api.utils.MergeUtils.merge;
import static org.dhis2.android.dashboard.api.utils.NetworkUtils.unwrapResponse;

/**
 * @author Araz Abishov <araz.abishov.gsoc@gmail.com>.
 */
public final class InterpretationController implements IController<Object> {
    private final DhisApi mDhisApi;

    public InterpretationController(DhisManager dhisManager) {
        mDhisApi = RepoManager.createService(dhisManager.getServerUrl(),
                dhisManager.getUserCredentials());
    }

    @Override
    public Object run() throws APIException {
        getInterpretationDataFromServer();

        return null;
    }

    private void getInterpretationDataFromServer() throws RetrofitError {
        DateTime lastUpdated = DateTimeManager.getInstance()
                .getLastUpdated(ResourceType.INTERPRETATIONS);
        DateTime serverTime = mDhisApi.getSystemInfo().getServerDate();

        List<Interpretation> interpretations = updateInterpretations(lastUpdated);
        List<InterpretationElement> elements = updateInterpretationElements(interpretations);
        List<InterpretationComment> comments = updateInterpretationComments(interpretations);
        List<User> users = updateInterpretationUsers(interpretations, comments);

        Queue<DbOperation> operations = new LinkedList<>();
        operations.addAll(DbUtils.createOperations(
                queryInterpretationUsers(), users));
        operations.addAll(DbUtils.createOperations(
                queryInterpretations(), interpretations));
        operations.addAll(DbUtils.createOperations(
                queryInterpretationElements(null), elements));
        operations.addAll(DbUtils.createOperations(
                queryInterpretationComments(null), comments));

        DbUtils.applyBatch(operations);
        DateTimeManager.getInstance()
                .setLastUpdated(ResourceType.INTERPRETATIONS, serverTime);
    }

    private List<Interpretation> updateInterpretations(DateTime lastUpdated) throws RetrofitError {
        final Map<String, String> QUERY_MAP_BASIC = new HashMap<>();
        final Map<String, String> QUERY_MAP_FULL = new HashMap<>();
        final String BASE = "id,created,lastUpdated,name,displayName,access";

        QUERY_MAP_BASIC.put("fields", "id");
        QUERY_MAP_FULL.put("fields", BASE + ",text,type," +
                "chart" + "[" + BASE + "]," +
                "map" + "[" + BASE + "]," +
                "reportTable" + "[" + BASE + "]," +
                "user" + "[" + BASE + "]," +
                "dataSet" + "[" + BASE + "]," +
                "period" + "[" + BASE + "]," +
                "organisationUnit" + "[" + BASE + "]," +
                "comments" + "[" + BASE + ",user,text" + "]");

        if (lastUpdated != null) {
            QUERY_MAP_FULL.put("filter", "lastUpdated:gt:" + lastUpdated.toString());
        }

        List<Interpretation> actualInterpretations = unwrapResponse(mDhisApi
                .getInterpretations(QUERY_MAP_BASIC), "interpretations");

        List<Interpretation> updatedInterpretations = unwrapResponse(mDhisApi
                .getInterpretations(QUERY_MAP_FULL), "interpretations");

        if (updatedInterpretations != null && !updatedInterpretations.isEmpty()) {

            for (Interpretation interpretation : updatedInterpretations) {

                // build relationship with comments
                if (interpretation.getComments() != null &&
                        !interpretation.getComments().isEmpty()) {

                    for (InterpretationComment comment : interpretation.getComments()) {
                        comment.setInterpretation(interpretation);
                    }
                }

                // we need to set mime type and interpretation to each element
                switch (interpretation.getType()) {
                    case Interpretation.TYPE_CHART: {
                        interpretation.getChart()
                                .setType(InterpretationElement.TYPE_CHART);
                        interpretation.getChart()
                                .setInterpretation(interpretation);
                        break;
                    }
                    case Interpretation.TYPE_MAP: {
                        interpretation.getMap()
                                .setType(InterpretationElement.TYPE_MAP);
                        interpretation.getMap()
                                .setInterpretation(interpretation);
                        break;
                    }
                    case Interpretation.TYPE_REPORT_TABLE: {
                        interpretation.getReportTable()
                                .setType(InterpretationElement.TYPE_REPORT_TABLE);
                        interpretation.getReportTable()
                                .setInterpretation(interpretation);
                        break;
                    }
                    case Interpretation.TYPE_DATASET_REPORT: {
                        interpretation.getDataSet()
                                .setType(InterpretationElement.TYPE_DATA_SET);
                        interpretation.getPeriod()
                                .setType(InterpretationElement.TYPE_PERIOD);
                        interpretation.getOrganisationUnit()
                                .setType(InterpretationElement.TYPE_ORGANISATION_UNIT);

                        interpretation.getDataSet().setInterpretation(interpretation);
                        interpretation.getPeriod().setInterpretation(interpretation);
                        interpretation.getOrganisationUnit().setInterpretation(interpretation);
                        break;
                    }
                }
            }
        }

        List<Interpretation> persistedInterpretations = queryInterpretations();
        if (persistedInterpretations != null
                && !persistedInterpretations.isEmpty()) {
            for (Interpretation interpretation : persistedInterpretations) {
                interpretation.setInterpretationElements(queryInterpretationElements(interpretation));
                interpretation.setComments(queryInterpretationComments(interpretation));
            }
        }

        return merge(actualInterpretations, updatedInterpretations, persistedInterpretations);
    }

    private List<InterpretationComment> updateInterpretationComments(List<Interpretation> interpretations) {
        List<InterpretationComment> interpretationComments = new ArrayList<>();

        if (interpretations != null && !interpretations.isEmpty()) {
            for (Interpretation interpretation : interpretations) {
                interpretationComments.addAll(interpretation.getComments());
            }
        }

        return interpretationComments;
    }

    private List<User> updateInterpretationUsers(List<Interpretation> interpretations,
                                                 List<InterpretationComment> comments) {
        Map<String, User> users = new HashMap<>();

        if (interpretations != null && !interpretations.isEmpty()) {
            for (Interpretation interpretation : interpretations) {
                User user = interpretation.getUser();
                if (users.containsKey(user.getUId())) {
                    user = users.get(user.getUId());
                    interpretation.setUser(user);
                } else {
                    users.put(user.getUId(), user);
                }
            }
        }

        if (comments != null && !comments.isEmpty()) {
            for (InterpretationComment comment : comments) {
                User user = comment.getUser();
                if (users.containsKey(user.getUId())) {
                    user = users.get(user.getUId());
                    comment.setUser(user);
                } else {
                    users.put(user.getUId(), user);
                }
            }
        }

        return new ArrayList<>(users.values());
    }

    private List<InterpretationElement> updateInterpretationElements(List<Interpretation> interpretations) {
        List<InterpretationElement> elements = new ArrayList<>();

        if (interpretations != null && !interpretations.isEmpty()) {
            for (Interpretation interpretation : interpretations) {
                elements.addAll(interpretation.getInterpretationElements());
            }
        }

        return elements;
    }

    private static List<Interpretation> queryInterpretations() {
        return new Select().from(Interpretation.class)
                .where(Condition.column(Interpretation$Table
                        .STATE).is(State.TO_POST.toString()))
                .queryList();
    }

    private static List<User> queryInterpretationUsers() {
        return new Select().from(User.class).queryList();
    }

    private static List<InterpretationElement> queryInterpretationElements(Interpretation interpretation) {
        From<InterpretationElement> from = new Select().from(InterpretationElement.class);

        if (interpretation != null) {
            return from.where(Condition.column(InterpretationElement$Table
                    .INTERPRETATION_INTERPRETATION).is(interpretation.getId()))
                    .queryList();
        }

        return from.queryList();
    }

    private static List<InterpretationComment> queryInterpretationComments(Interpretation interpretation) {
        Where<InterpretationComment> where = new Select()
                .from(InterpretationComment.class)
                .where(Condition.column(InterpretationComment$Table
                        .STATE).isNot(State.TO_POST.toString()));

        if (interpretation != null) {
            where = where.and(Condition.column(InterpretationComment$Table
                    .INTERPRETATION_INTERPRETATION).is(interpretation.getId()));
        }

        return where.queryList();
    }
}
