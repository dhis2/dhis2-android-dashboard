package org.hisp.dhis.android.core.converters;

import static junit.framework.Assert.assertTrue;

import org.hisp.dhis.android.core.commons.FileReader;
import org.hisp.dhis.android.dashboard.api.models.Access;
import org.hisp.dhis.android.dashboard.api.persistence.converters.AccessConverter;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class AccessConverterTests {
    public static Access accessObject;
    AccessConverter accessConverter = new AccessConverter();

    @Before
    public void setUp() throws Exception {
        accessObject = new Access();
        accessObject.setDelete(true);
        accessObject.setWrite(true);
        accessObject.setUpdate(true);
        accessObject.setRead(true);
        accessObject.setManage(true);
        accessObject.setExternalize(true);
    }

    @Test
    public void convert_access_object_to_database_string() throws Exception {
        String access = new FileReader().getStringFromFile("access_all_true_string.txt");
        assertTrue(accessConverter.getDBValue(accessObject).equals(access));
    }

    @Test
    public void convert_access_all_true_database_string_to_model() throws IOException {
        Access access = accessConverter.getModelValue( new FileReader().getStringFromFile(
                "access_all_true_string.txt"));
        assertTrue(access.isDelete());
        assertTrue(access.isRead());
        assertTrue(access.isWrite());
        assertTrue(access.isManage());
        assertTrue(access.isExternalize());
    }

    @Test
    public void convert_access_all_false_database_string_to_model() throws IOException {
        Access access = accessConverter.getModelValue( new FileReader().getStringFromFile(
                "access_all_false_string.txt"));
        assertTrue(!access.isDelete());
        assertTrue(!access.isRead());
        assertTrue(!access.isWrite());
        assertTrue(!access.isManage());
        assertTrue(!access.isExternalize());
    }
}
