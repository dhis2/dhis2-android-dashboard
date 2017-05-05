package org.hisp.dhis.android.core.converters;

import static junit.framework.Assert.assertTrue;

import org.hisp.dhis.android.dashboard.api.models.Access;
import org.hisp.dhis.android.dashboard.api.persistence.converters.AccessConverter;
import org.junit.Before;
import org.junit.Test;

public class AccessConverterTests {
    public static final String ACCESS_ALL_TRUE_AS_DATABASE_STRING = "{\"manage\":true,"
            + "\"externalize\":true,\"write\":true,\"read\":true,\"update\":true,\"delete\":true}";
    public static final String ACCESS_ALL_FALSE_AS_DATABASE_STRING = "{\"manage\":false,"
            + "\"externalize\":false,\"write\":false,\"read\":false,\"update\":false,"
            + "\"delete\":false}";
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
        assertTrue(accessConverter.getDBValue(accessObject).equals(
                ACCESS_ALL_TRUE_AS_DATABASE_STRING));
    }

    @Test
    public void convert_access_all_true_database_string_to_model() {
        Access access = accessConverter.getModelValue(ACCESS_ALL_TRUE_AS_DATABASE_STRING);
        assertTrue(access.isDelete());
        assertTrue(access.isRead());
        assertTrue(access.isWrite());
        assertTrue(access.isManage());
        assertTrue(access.isExternalize());
    }

    @Test
    public void convert_access_all_false_database_string_to_model() {
        Access access = accessConverter.getModelValue(ACCESS_ALL_FALSE_AS_DATABASE_STRING);
        assertTrue(!access.isDelete());
        assertTrue(!access.isRead());
        assertTrue(!access.isWrite());
        assertTrue(!access.isManage());
        assertTrue(!access.isExternalize());
    }
}
