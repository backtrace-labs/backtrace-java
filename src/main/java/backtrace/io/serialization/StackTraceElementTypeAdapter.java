package backtrace.io.serialization;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Arrays;

public class StackTraceElementTypeAdapter implements JsonSerializer<StackTraceElement> {

	@Override public JsonElement serialize(StackTraceElement src, Type typeOfSrc, JsonSerializationContext context) {
		if (src == null) {
			return null;
		}
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
