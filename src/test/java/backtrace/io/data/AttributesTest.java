package backtrace.io.data;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class AttributesTest {

    @Test
    public void createAttributes() {
        // GIVEN
        Map<String, Object> clientAttributes = new HashMap<>() {{
            put("client-attr-1", "1");
            put("client-attr-2", null);
            put(null, null);
        }};

        Map<String, Object> reportAttributes = new HashMap<>() {{
            put("report-attr-1", new HashMap<>() {{
                        put("inner-attr-1", "test");
                        put(null, null);
                    }}
            );
            put("report-attr-2", null);
            put(null, null);
        }};

        BacktraceReport report = new BacktraceReport("message", reportAttributes);

        // WHEN
        Attributes result = new Attributes(report, clientAttributes);

        // THEN
        Assert.assertEquals(result.getAttributes().get("client-attr-1"), "1");
        Assert.assertNull(result.getComplexAttributes().get("client-attr-2"));
        Assert.assertNull(result.getComplexAttributes().get(null));

        Assert.assertNull(result.getAttributes().get("report-attr-2"));

        Map<String, Object> innerMap = (Map<String, Object>) result.getComplexAttributes().get("report-attr-1");
        Assert.assertEquals(innerMap.get("inner-attr-1"), "test");
        Assert.assertNull(innerMap.get(null));
    }
}
