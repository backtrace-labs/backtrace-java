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
     * @param json
     * @param type
     * @param <T>
     * @return
     */
    static <T> T fromJson(String json, Class<T> type) {
        return new Gson().fromJson(json, type);
    }
}
