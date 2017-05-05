package org.hisp.dhis.android.core.systeminfo;

import static org.junit.Assert.assertTrue;

import org.hisp.dhis.android.core.commons.DateTestUtils;
import org.hisp.dhis.android.core.commons.FileReader;
import org.hisp.dhis.android.core.commons.JsonParser;
import org.hisp.dhis.android.dashboard.api.models.SystemInfo;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

public class SystemInfoTests {

    @Test
    public void systemInfo_should_map_from_json_String() throws IOException, ParseException {
        SystemInfo systemInfo = (SystemInfo) JsonParser.getModelFromJson(SystemInfo.class,
                new FileReader().getStringFromFile("systeminfo.json"));

        assertTrue(systemInfo.getVersion().equals("2.26"));
        assertTrue(DateTestUtils.compareParsedDateWithStringDate(systemInfo.getBuildTime(),
                "2017-05-04T06:37:32.000"));
        assertTrue(systemInfo.getCalendar().equals("iso8601"));
        assertTrue(systemInfo.getIntervalSinceLastAnalyticsTableSuccess().equals("2345 h, 15 m, 11 s"));
        assertTrue(systemInfo.getLastAnalyticsTableSuccess().equals("2017-01-26T23:19:34.009"));
        assertTrue(systemInfo.getRevision().equals("f297d4c"));
        assertTrue(DateTestUtils.compareParsedDateWithStringDate(systemInfo.getServerDate(),
                "2017-05-04T16:34:45.957"));
        assertTrue(systemInfo.getDateFormat().equals("yyyy-mm-dd"));
    }


}
