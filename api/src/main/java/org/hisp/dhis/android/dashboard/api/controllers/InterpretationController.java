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

package org.hisp.dhis.android.dashboard.api.controllers;

import android.net.Uri;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.From;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.sql.language.Where;

import org.hisp.dhis.android.dashboard.api.models.Interpretation;
import org.hisp.dhis.android.dashboard.api.models.Interpretation$Table;
import org.hisp.dhis.android.dashboard.api.models.InterpretationComment;
import org.hisp.dhis.android.dashboard.api.models.InterpretationComment$Table;
import org.hisp.dhis.android.dashboard.api.models.InterpretationElement;
import org.hisp.dhis.android.dashboard.api.models.InterpretationElement$Table;
import org.hisp.dhis.android.dashboard.api.models.User;
import org.hisp.dhis.android.dashboard.api.models.User$Table;
import org.hisp.dhis.android.dashboard.api.models.UserAccount;
import org.hisp.dhis.android.dashboard.api.models.meta.DbOperation;
import org.hisp.dhis.android.dashboard.api.models.meta.State;
import org.hisp.dhis.android.dashboard.api.network.APIException;
import org.hisp.dhis.android.dashboard.api.network.DhisApi;
import org.hisp.dhis.android.dashboard.api.persistence.preferences.DateTimeManager;
import org.hisp.dhis.android.dashboard.api.persistence.preferences.ResourceType;
import org.hisp.dhis.android.dashboard.api.utils.DbUtils;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import retrofit.client.Header;
import retrofit.client.Response;
import retrofit.mime.TypedString;

import static org.hisp.dhis.android.dashboard.api.models.BaseIdentifiableObject.merge;
import static org.hisp.dhis.android.dashboard.api.models.BaseIdentifiableObject.toMap;
import static org.hisp.dhis.android.dashboard.api.utils.NetworkUtils.findLocationHeader;
import static org.hisp.dhis.android.dashboard.api.utils.NetworkUtils.handleApiException;
import static org.hisp.dhis.android.dashboard.api.utils.NetworkUtils.unwrapResponse;

/**
 * @author Araz Abishov <araz.abishov.gsoc@gmail.com>.
 */
final class InterpretationController {
    private final DhisApi mDhisApi;

    public InterpretationController(DhisApi dhisApi) {
        mDhisApi = dhisApi;
    }

    public void syncInterpretations() throws APIException {
        getInterpretationDataFromServer();
        sendLocalChanges();
    }

    private void sendLocalChanges() throws APIException {
        sendInterpretationChanges();
        sendInterpretationCommentChanges();
    }

    private void sendInterpretationChanges() throws APIException {
        List<Interpretation> interpretations = new Select()
                .from(Interpretation.class)
                .where(Condition.column(Interpretation$Table
                        .STATE).isNot(State.SYNCED.toString()))
                .orderBy(true, Interpretation$Table.ID)
                .queryList();

        if (interpretations == null || interpretations.isEmpty()) {
            return;
        }

        for (Interpretation interpretation : interpretations) {
            List<InterpretationElement> elements = new Select()
                    .from(InterpretationElement.class)
                    .where(Condition.column(InterpretationElement$Table
                            .INTERPRETATION_INTERPRETATION).is(interpretation.getId()))
                    .queryList();
            interpretation.setInterpretationElements(elements);
        }

        for (Interpretation interpretation : interpretations) {
            switch (interpretation.getState()) {
                case TO_POST: {
                    postInterpretation(interpretation);
                    break;
                }
                case TO_UPDATE: {
                    putInterpretation(interpretation);
                    break;
                }
                case TO_DELETE: {
                    deleteInterpretation(interpretation);
                    break;
                }
            }
        }
    }

    public void postInterpretation(Interpretation interpretation) throws APIException {
        try {
            Response response;

            switch (interpretation.getType()) {
                case Interpretation.TYPE_CHART: {
                    response = mDhisApi.postChartInterpretation(
                            interpretation.getChart().getUId(), new TypedString(interpretation
                                    .getText()));
                    break;
                }
                case Interpretation.TYPE_MAP: {
                    response = mDhisApi.postMapInterpretation(
                            interpretation.getMap().getUId(), new TypedString(interpretation
                                    .getText()));
                    break;
                }
                case Interpretation.TYPE_REPORT_TABLE: {
                    response = mDhisApi.postReportTableInterpretation(
                            interpretation.getReportTable().getUId(), new TypedString
                                    (interpretation.getText()));
                    break;
                }
                default:
                    throw new IllegalArgumentException("Unsupported interpretation type");
            }

            Header header = findLocationHeader(response.getHeaders());
            String interpretationUid = Uri.parse(header
                    .getValue()).getLastPathSegment();
            interpretation.setUId(interpretationUid);
            interpretation.setState(State.SYNCED);
            interpretation.save();

            updateInterpretationTimeStamp(interpretation);

        } catch (APIException apiException) {
            handleApiException(apiException, interpretation);
        }
    }

    public void putInterpretation(Interpretation interpretation) throws APIException {
        try {
            mDhisApi.putInterpretationText(interpretation.getUId(),
                    new TypedString(interpretation.getText()));
            interpretation.setState(State.SYNCED);
            interpretation.save();

            updateInterpretationTimeStamp(interpretation);
        } catch (APIException apiException) {
            handleApiException(apiException, interpretation);
        }
    }

    public void deleteInterpretation(Interpretation interpretation) throws APIException {
        try {
            mDhisApi.deleteInterpretation(interpretation.getUId());
            interpretation.delete();
        } catch (APIException apiException) {
            handleApiException(apiException, interpretation);
        }
    }

    private void sendInterpretationCommentChanges() throws APIException {
        List<InterpretationComment> comments = new Select()
                .from(InterpretationComment.class)
                .where(Condition.column(InterpretationComment$Table
                        .STATE).isNot(State.SYNCED.toString()))
                .queryList();

        if (comments == null || comments.isEmpty()) {
            return;
        }

        for (InterpretationComment comment : comments) {
            switch (comment.getState()) {
                case TO_POST: {
                    postInterpretationComment(comment);
                    break;
                }
                case TO_UPDATE: {
                    putInterpretationComment(comment);
                    break;
                }
                case TO_DELETE: {
                    deleteInterpretationComment(comment);
                    break;
                }
            }
        }
    }

    public void postInterpretationComment(InterpretationComment comment) throws APIException {
        Interpretation interpretation = comment.getInterpretation();

        if (interpretation != null && interpretation.getState() != null) {
            boolean isInterpretationSynced = (interpretation.getState().equals(State.SYNCED) ||
                    interpretation.getState().equals(State.TO_UPDATE));

            if (!isInterpretationSynced) {
                return;
            }

            try {
                Response response = mDhisApi.postInterpretationComment(
                        interpretation.getUId(), new TypedString(comment.getText()));

                Header locationHeader = findLocationHeader(response.getHeaders());
                String commentUid = Uri.parse(locationHeader
                        .getValue()).getLastPathSegment();
                comment.setUId(commentUid);
                comment.setState(State.SYNCED);
                comment.save();

                updateInterpretationCommentTimeStamp(comment);
            } catch (APIException apiException) {
                handleApiException(apiException, comment);
            }
        }
    }

    public void putInterpretationComment(InterpretationComment comment) throws APIException {
        Interpretation interpretation = comment.getInterpretation();

        if (interpretation != null && interpretation.getState() != null) {
            boolean isInterpretationSynced = (interpretation.getState().equals(State.SYNCED) ||
                    interpretation.getState().equals(State.TO_UPDATE));

            if (!isInterpretationSynced) {
                return;
            }

            try {
                mDhisApi.putInterpretationComment(interpretation.getUId(),
                        comment.getUId(), new TypedString(comment.getText()));

                comment.setState(State.SYNCED);
                comment.save();

                updateInterpretationTimeStamp(comment.getInterpretation());
            } catch (APIException apiException) {
                handleApiException(apiException);
            }
        }
    }

    public void deleteInterpretationComment(InterpretationComment comment) throws APIException {
        Interpretation interpretation = comment.getInterpretation();

        if (interpretation != null && interpretation.getState() != null) {
            boolean isInterpretationSynced = (interpretation.getState().equals(State.SYNCED) ||
                    interpretation.getState().equals(State.TO_UPDATE));

            // 1) If State of Interpretation is TO_DELETE,
            //    there is no meaning to remove its comments by hand.
            //    They will be removed automatically when interpretation is removed.
            // 2) If State of Interpretation is TO_POST,
            //    we cannot create comment on server, since we don't have
            //    interpretation UUID to associate comment with.
            // In all other State cases (TO_UPDATE, SYNCED), we can delete comments
            if (!isInterpretationSynced) {
                return;
            }

            try {
                mDhisApi.deleteInterpretationComment(
                        interpretation.getUId(), comment.getUId());
                comment.delete();

                updateInterpretationTimeStamp(comment.getInterpretation());
            } catch (APIException apiException) {
                handleApiException(apiException, comment);
            }
        }
    }

    /**
     * This method gets only time stamp from server
     * for given interpretation and updates it locally.
     *
     * @param interpretation Interpretation to update.
     */
    private void updateInterpretationTimeStamp(Interpretation interpretation) throws APIException {
        try {
            final Map<String, String> QUERY_PARAMS = new HashMap<>();
            QUERY_PARAMS.put("fields", "[created,lastUpdated]");

            Interpretation updatedInterpretation = mDhisApi
                    .getInterpretation(interpretation.getUId(), QUERY_PARAMS);

            // merging updated timestamp to local interpretation model
            interpretation.setCreated(updatedInterpretation.getCreated());
            interpretation.setLastUpdated(updatedInterpretation.getLastUpdated());
            interpretation.save();
        } catch (APIException apiException) {
            handleApiException(apiException, interpretation);
        }
    }

    private void updateInterpretationCommentTimeStamp(InterpretationComment comment) throws
            APIException {
        try {
            // after posting comment, timestamp both of interpretation and comment will change.
            // we have to reflect these changes here in order not to break data integrity during
            // next synchronizations to server.
            Map<String, String> queryParams = new HashMap<>();
            queryParams.put("fields", "created,lastUpdated,comments[id,created,lastUpdated]");
            Interpretation persistedInterpretation = comment.getInterpretation();
            Interpretation updatedInterpretation = mDhisApi
                    .getInterpretation(persistedInterpretation.getUId(), queryParams);

            // first, update timestamp of interpretation
            persistedInterpretation.setCreated(updatedInterpretation.getCreated());
            persistedInterpretation.setLastUpdated(updatedInterpretation.getLastUpdated());
            persistedInterpretation.save();

            // second, find comment which we have added recently and update its timestamp
            Map<String, InterpretationComment> updatedComments
                    = toMap(updatedInterpretation.getComments());
            if (updatedComments.containsKey(comment.getUId())) {
                InterpretationComment updatedComment = updatedComments.get(comment.getUId());

                // set timestamp here
                comment.setCreated(updatedComment.getCreated());
                comment.setLastUpdated(updatedComment.getLastUpdated());
                comment.save();
            }
        } catch (APIException apiException) {
            handleApiException(apiException);
        }
    }

    private void getInterpretationDataFromServer() throws APIException {
        DateTime lastUpdated = DateTimeManager.getInstance()
                .getLastUpdated(ResourceType.INTERPRETATIONS);
        DateTime serverTime = mDhisApi.getSystemInfo().getServerDate();

        List<Interpretation> interpretations = updateInterpretations(lastUpdated);
        List<InterpretationComment> comments = updateInterpretationComments(interpretations);
        List<User> users = updateInterpretationUsers(interpretations, comments);

        Queue<DbOperation> operations = new LinkedList<>();
        operations.addAll(DbUtils.createOperations(
                queryInterpretationUsers(), users));
        operations.addAll(createOperations(
                queryInterpretations(), interpretations));
        operations.addAll(DbUtils.createOperations(
                queryInterpretationComments(null), comments));

        DbUtils.applyBatch(operations);
        DateTimeManager.getInstance()
                .setLastUpdated(ResourceType.INTERPRETATIONS, serverTime);
    }

    private List<Interpretation> updateInterpretations(DateTime lastUpdated) throws APIException {
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
                "comments" + "[" + BASE + ",user" + "[" + BASE + "]" + ",text" + "]");

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
                    case Interpretation.TYPE_DATA_SET_REPORT: {
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
                interpretation.setInterpretationElements(queryInterpretationElements
                        (interpretation));
                interpretation.setComments(queryInterpretationComments(interpretation));
            }
        }

        return merge(actualInterpretations, updatedInterpretations, persistedInterpretations);
    }

    private List<InterpretationComment> updateInterpretationComments(List<Interpretation>
                                                                             interpretations) {
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
        UserAccount currentUserAccount
                = UserAccount.getCurrentUserAccountFromDb();
        User currentUser = new Select()
                .from(User.class)
                .where(Condition.column(User$Table
                        .UID).is(currentUserAccount.getUId()))
                .querySingle();
        if (currentUser == null) {
            currentUser = UserAccount
                    .toUser(currentUserAccount);
        }

        users.put(currentUser.getUId(), currentUser);
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

    private static List<DbOperation> createOperations(List<Interpretation> oldModels,
                                                      List<Interpretation> newModels) {
        List<DbOperation> ops = new ArrayList<>();

        Map<String, Interpretation> newModelsMap = toMap(newModels);
        Map<String, Interpretation> oldModelsMap = toMap(oldModels);

        for (String oldModelKey : oldModelsMap.keySet()) {
            Interpretation newModel = newModelsMap.get(oldModelKey);
            Interpretation oldModel = oldModelsMap.get(oldModelKey);

            if (newModel == null) {
                ops.add(DbOperation.delete(oldModel));
                continue;
            }

            if (newModel.getLastUpdated().isAfter(oldModel.getLastUpdated())) {
                newModel.setId(oldModel.getId());
                ops.add(DbOperation.update(newModel));
            }

            newModelsMap.remove(oldModelKey);
        }

        for (String newModelKey : newModelsMap.keySet()) {
            Interpretation item = newModelsMap.get(newModelKey);

            // we also have to insert interpretation elements here
            ops.add(DbOperation.insert(item));

            List<InterpretationElement> elements = item
                    .getInterpretationElements();
            for (InterpretationElement element : elements) {
                ops.add(DbOperation.insert(element));
            }
        }

        return ops;
    }

    private static List<Interpretation> queryInterpretations() {
        return new Select()
                .from(Interpretation.class)
                .where(Condition.column(Interpretation$Table
                        .STATE).isNot(State.TO_POST.toString()))
                .queryList();
    }

    private static List<User> queryInterpretationUsers() {
        return new Select().from(User.class).queryList();
    }

    private static List<InterpretationElement> queryInterpretationElements(Interpretation
                                                                                   interpretation) {
        From<InterpretationElement> from = new Select().from(InterpretationElement.class);

        if (interpretation != null) {
            return from
                    .where(Condition.column(InterpretationElement$Table
                            .INTERPRETATION_INTERPRETATION).is(interpretation.getId()))
                    .queryList();
        }

        return from.queryList();
    }

    private static List<InterpretationComment> queryInterpretationComments(Interpretation
                                                                                   interpretation) {
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