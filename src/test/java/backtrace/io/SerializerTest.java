package backtrace.io;

import backtrace.io.helpers.BacktraceSerializeHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SerializerTest {
    private final String message = "test-message";
    private String expectedJson;

    class MockTestObject {
        private String message;

        MockTestObject(String message){
            this.message = message;
        }

        String getMessage(){
            return this.message;
        }
    }

    @Before
    public void init(){
        this.expectedJson = String.format("{\"message\":\"%s\"}", this.message);
    }

    @Test
    public void objectSerialization() {
        // GIVEN
        MockTestObject mto = new MockTestObject(this.message);
        // WHEN
        String json = BacktraceSerializeHelper.toJson(mto);
        // THEN
        Assert.assertEquals(expectedJson, json);
    }

    @Test
    public void objectDeserialization() {
        // WHEN
        MockTestObject mto = BacktraceSerializeHelper.fromJson(expectedJson, MockTestObject.class);

        // THEN
        Assert.assertNotNull(mto);
        Assert.assertEquals(this.message, mto.getMessage());
    }
}
