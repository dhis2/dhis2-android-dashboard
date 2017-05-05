package org.hisp.dhis.android.core.interpretation;

import static org.junit.Assert.assertTrue;

import org.hisp.dhis.android.core.commons.DateTestUtils;
import org.hisp.dhis.android.core.commons.FileReader;
import org.hisp.dhis.android.core.commons.JsonParser;
import org.hisp.dhis.android.dashboard.api.models.Access;
import org.hisp.dhis.android.dashboard.api.models.Interpretation;
import org.hisp.dhis.android.dashboard.api.models.InterpretationComment;
import org.hisp.dhis.android.dashboard.api.models.InterpretationElement;
import org.hisp.dhis.android.dashboard.api.models.User;
import org.junit.Test;

import java.io.IOException;

public class InterpretationTests {

    @Test
    public void interpretation_shouldMapFromJsonString() throws IOException {
        Interpretation interpretation = getInterpretationFromJson();
        assertTrue(DateTestUtils.compareParsedDateWithStringDate(interpretation.getCreated(),
                "2017-10-21T10:10:43.451"));
        assertTrue(DateTestUtils.compareParsedDateWithStringDate(interpretation.getLastUpdated(),
                "2017-10-21T10:10:43.451"));
        assertTrue(interpretation.getName().equals("BR11Oy1Q4yR"));
        assertTrue(interpretation.getUId().equals("BR11Oy1Q4yR"));
        assertTrue(interpretation.getDisplayName().equals("BR11Oy1Q4yR"));
        assertTrue(interpretation.getType().equals("CHART"));
        assertTrue(interpretation.getText().equals(
                "This chart shows that BCG doses is low for 2014, why is that?"));
        Access access = getAccessObject();
        assertTrue(interpretation.getAccess().isDelete() == access.isDelete());
        assertTrue(interpretation.getAccess().isExternalize() == access.isExternalize());
        assertTrue(interpretation.getAccess().isManage() == access.isManage());
        assertTrue(interpretation.getAccess().isRead() == access.isRead());
        assertTrue(interpretation.getAccess().isWrite() == access.isWrite());
    }

    @Test
    public void interpretation_chart_shouldMapFromJsonString() throws IOException {
        Interpretation interpretation = getInterpretationFromJson();
        InterpretationElement interpretationElement = interpretation.getChart();
        assertTrue(DateTestUtils.compareParsedDateWithStringDate(interpretationElement
                .getCreated(), "2013-05-29T12:52:54.560"));
        assertTrue(DateTestUtils.compareParsedDateWithStringDate(interpretationElement
                .getLastUpdated(), "2015-07-15T15:25:20.264"));
        assertTrue(interpretationElement.getName().equals(
                "Immunization: BCG, Measles, YF doses comparison"));
        assertTrue(interpretationElement.getUId().equals("R9A0rvAydpn"));
        assertTrue(interpretationElement.getDisplayName().equals(
                "Immunization: BCG, Measles, YF doses comparison"));
        assertTrue(interpretationElement.getType() == null);
        assertTrue(interpretationElement.getAccess() == null);
    }

    @Test
    public void interpretation_user_shouldMapFromJsonString() throws IOException {
        Interpretation interpretation = getInterpretationFromJson();
        User user = interpretation.getUser();
        assertTrue(DateTestUtils.compareParsedDateWithStringDate(user
                .getCreated(), "2013-04-18T17:15:08.407"));
        assertTrue(DateTestUtils.compareParsedDateWithStringDate(user
                .getLastUpdated(), "2017-01-19T14:24:04.447"));
        assertTrue(user.getName().equals(
                "John Traore"));
        assertTrue(user.getUId().equals("xE7jOejl9FI"));
        assertTrue(user.getDisplayName().equals(
                "John Traore"));
    }

    @Test
    public void interpretation_first_comment_shouldMapFromJsonString() throws IOException {
        InterpretationComment interpretationComment = getFirstCommentFromInterpretationJson();
        assertTrue(DateTestUtils.compareParsedDateWithStringDate(interpretationComment.getCreated(),
                "2014-10-21T10:11:19.537"));
        assertTrue(DateTestUtils.compareParsedDateWithStringDate(
                interpretationComment.getLastUpdated(),
                "2014-10-21T10:11:19.537"));
        assertTrue(interpretationComment.getUId().equals("Eg7x5Kt2XgV"));
        assertTrue(interpretationComment.getText().equals(
                "It might be caused by a stock-out of vaccines."));
    }

    @Test
    public void interpretation_second_comment_shouldMapFromJsonString() throws IOException {
        InterpretationComment interpretationComment = getSecondCommentFromInterpretationJson();
        assertTrue(DateTestUtils.compareParsedDateWithStringDate(interpretationComment.getCreated(),
                "2014-10-21T10:11:44.325"));
        assertTrue(DateTestUtils.compareParsedDateWithStringDate(
                interpretationComment.getLastUpdated(),
                "2014-10-21T10:11:44.325"));
        assertTrue(interpretationComment.getUId().equals("oRmqfmnCLsQ"));
        assertTrue(interpretationComment.getText().equals("Yes I believe so"));
    }

    @Test
    public void interpretation_comments_first_user_shouldMapFromJsonString() throws IOException {
        User user = getUserFromInterpretationJsonFirstComment();
        assertTrue(DateTestUtils.compareParsedDateWithStringDate(user.getCreated(),
                "2013-04-18T17:15:08.407"));
        assertTrue(DateTestUtils.compareParsedDateWithStringDate(user.getLastUpdated(),
                "2017-01-19T14:24:04.447"));
        assertTrue(user.getName().equals("John Traore"));
        assertTrue(user.getUId().equals("xE7jOejl9FI"));
        assertTrue(user.getDisplayName().equals("John Traore"));

        assertTrue(DateTestUtils.compareParsedDateWithStringDate(user.getCreated(),
                "2013-04-18T17:15:08.407"));
        assertTrue(DateTestUtils.compareParsedDateWithStringDate(user.getLastUpdated(),
                "2017-01-19T14:24:04.447"));
        assertTrue(user.getName().equals("John Traore"));
        assertTrue(user.getUId().equals("xE7jOejl9FI"));
        assertTrue(user.getDisplayName().equals("John Traore"));
    }

    @Test
    public void interpretation_comments_second_user_shouldMapFromJsonString() throws IOException {
        User user = getUserFromInterpretationJsonSecondComment();
        assertTrue(DateTestUtils.compareParsedDateWithStringDate(user.getCreated(),
                "2013-04-18T17:15:08.407"));
        assertTrue(DateTestUtils.compareParsedDateWithStringDate(user.getLastUpdated(),
                "2017-01-19T14:24:04.447"));
        assertTrue(user.getName().equals("John Traore"));
        assertTrue(user.getUId().equals("xE7jOejl9FI"));
        assertTrue(user.getDisplayName().equals("John Traore"));

        assertTrue(DateTestUtils.compareParsedDateWithStringDate(user.getCreated(),
                "2013-04-18T17:15:08.407"));
        assertTrue(DateTestUtils.compareParsedDateWithStringDate(user.getLastUpdated(),
                "2017-01-19T14:24:04.447"));
        assertTrue(user.getName().equals("John Traore"));
        assertTrue(user.getUId().equals("xE7jOejl9FI"));
        assertTrue(user.getDisplayName().equals("John Traore"));
    }

    private User getUserFromInterpretationJsonSecondComment() throws IOException {
        Interpretation interpretation = getInterpretationFromJson();
        InterpretationComment interpretationComment = interpretation.getComments().get(1);
        return interpretationComment.getUser();
    }

    private InterpretationComment getFirstCommentFromInterpretationJson() throws IOException {
        return getInterpretationComment(0);
    }

    private InterpretationComment getSecondCommentFromInterpretationJson() throws IOException {
        return getInterpretationComment(1);
    }

    private InterpretationComment getInterpretationComment(int index) throws IOException {
        Interpretation interpretation = getInterpretationFromJson();
        return interpretation.getComments().get(index);
    }

    private User getUserFromInterpretationJsonFirstComment() throws IOException {
        InterpretationComment interpretationComment =
                getFirstCommentFromInterpretationJson();
        return interpretationComment.getUser();
    }

    private Interpretation getInterpretationFromJson() throws IOException {
        return (Interpretation) JsonParser.getModelFromJson(Interpretation.class,
                new FileReader().getStringFromFile("interpretation.json"));
    }

    private Access getAccessObject() {
        Access access = new Access();
        access.setDelete(true);
        access.setExternalize(false);
        access.setManage(true);
        access.setRead(true);
        access.setUpdate(true);
        access.setWrite(true);
        return access;
    }
}
