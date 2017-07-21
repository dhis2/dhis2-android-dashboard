import static org.junit.Assert.assertTrue;

import org.hisp.dhis.android.dashboard.utils.TextUtils;
import org.junit.Test;

public class TextUtilsTest {

    @Test
    public void test_empty_string_is_empty() {
        assertTrue(TextUtils.isEmpty(""));
    }

    @Test
    public void test_null_string_is_empty() {
        assertTrue(TextUtils.isEmpty(null));
    }
}
