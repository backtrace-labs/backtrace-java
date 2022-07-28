package backtrace.io.serialization;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Arrays;

public class ThrowableTypeAdapter implements JsonSerializer<Throwable> {

	@Override public JsonElement serialize(Throwable src, Type typeOfSrc, JsonSerializationContext context) {
		if (src == null) {
			return null;
		}
		JsonObject json = new JsonObject();
		json.addProperty("detailMessage", src.getMessage());
		var cause = context.serialize(src.getCause(), Throwable.class);
		if (cause != null) {
			json.add("cause", cause);
		}
		var trace = new JsonArray();
		Arrays.stream(src.getStackTrace()).forEach(stackTraceElement -> {
			trace.add(context.serialize(stackTraceElement));
		});
		json.add("stackTrace", trace);
		return json;
	}

}
