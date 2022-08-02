package backtrace.io.serialization;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class StackTraceElementTypeAdapter implements JsonSerializer<StackTraceElement> {

    @Override
    public JsonElement serialize(StackTraceElement src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null) {
            return null;
        }
        return toJson(src);
    }

    private JsonElement toJson(StackTraceElement src) {
        JsonObject json = new JsonObject();
        json.addProperty("classLoaderName", src.getClassLoaderName());
        json.addProperty("moduleName", src.getModuleName());
        json.addProperty("moduleVersion", src.getModuleVersion());
        json.addProperty("declaringClass", src.getClassName());
        json.addProperty("methodName", src.getMethodName());
        json.addProperty("fileName", src.getFileName());
        json.addProperty("lineNumber", src.getLineNumber());
        return json;
    }

}
