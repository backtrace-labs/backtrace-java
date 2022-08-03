package backtrace.io.data;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class AttributesTest {

    @Test
    public void createAttributes() {
        // GIVEN
        final Map<String, Object> clientAttributes = new HashMap<String, Object>() {{
            put("client-attr-1", "1");
            put("client-attr-2", null);
            put(null, null);
        }};

        final Map<String, Object> reportAttributes = new HashMap<String, Object>() {{
            put("report-attr-1", new HashMap<String, Object>() {{
                        put("inner-attr-1", "test");
                        put(null, null);
                    }}
            );
            put("report-attr-2", null);
            put(null, null);
        }};

        final BacktraceReport report = new BacktraceReport("message", reportAttributes);

        // WHEN
        final Attributes result = new Attributes(report, clientAttributes);

        // THEN
        Assert.assertEquals(result.getAttributes().get("client-attr-1"), "1");
        Assert.assertNull(result.getComplexAttributes().get("client-attr-2"));
        Assert.assertNull(result.getComplexAttributes().get(null));

        Assert.assertNull(result.getAttributes().get("report-attr-2"));

        final Map<String, Object> innerMap = (Map<String, Object>) result.getComplexAttributes().get("report-attr-1");
        Assert.assertEquals(innerMap.get("inner-attr-1"), "test");
        Assert.assertNull(innerMap.get(null));
    }
}
