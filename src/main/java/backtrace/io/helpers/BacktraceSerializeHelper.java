package backtrace.io.helpers;

import backtrace.io.serialization.StackTraceElementTypeAdapter;
import backtrace.io.serialization.ThrowableTypeAdapter;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


/**
 * Helper class for serialize and deserialize objects
 */
public class BacktraceSerializeHelper {

    private static final Gson gson = new GsonBuilder()
            .registerTypeHierarchyAdapter(Throwable.class, new ThrowableTypeAdapter())
            .registerTypeHierarchyAdapter(StackTraceElement.class, new StackTraceElementTypeAdapter())
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES)
            .create();

    /**
     * Serialize given object to JSON string
     *
     * @param object object which will be serialized
     * @return serialized object in JSON string format
     */
    public static String toJson(Object object) {
        return gson.toJson(object);
    }

    /**
     * Deserialize the specified Json into an object of the specified class
     *
     * @param <T>  the type of the desired object
     * @param json the string from which the object is to be deserialized
     * @param type the class of T
     * @return an object of type T from the string. Returns {@code null} if {@code json} is {@code null}
     * or if {@code json} is empty.
     */
    public static <T> T fromJson(String json, Class<T> type) {
        return gson.fromJson(json, type);
    }
}
