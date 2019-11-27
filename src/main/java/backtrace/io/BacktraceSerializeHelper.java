package backtrace.io;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


/**
 * Helper class for serialize and deserialize objects
 */
class BacktraceSerializeHelper {

    /**
     * Serialize given object to JSON string
     *
     * @param object object which will be serialized
     * @return serialized object in JSON string format
     */
    static String toJson(Object object) {
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES).create();
        return gson.toJson(object);
    }

    /**
     * Deserialize the specified Json into an object of the specified class
     * @param <T> the type of the desired object
     * @param json the string from which the object is to be deserialized
     * @param type the class of T
     * @return an object of type T from the string. Returns {@code null} if {@code json} is {@code null}
     * or if {@code json} is empty.
     */
    static <T> T fromJson(String json, Class<T> type) {
        return new Gson().fromJson(json, type);
    }
}
