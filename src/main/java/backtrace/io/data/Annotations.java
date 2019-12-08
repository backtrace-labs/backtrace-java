package backtrace.io.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Get report annotations - environment variables and application dependencies
 */
class Annotations {
    static class AnnotationException implements Serializable {
        @SerializedName("message")
        @SuppressWarnings({"unused", "FieldCanBeLocal"})
        private Object message;

        AnnotationException(Object message) {
            setMessage(message);
        }

        private void setMessage(Object message) {
            this.message = message;
        }
    }

    /**
     * Creates annotation object with complex attributes, environments variables and exception details
     *
     * @param exceptionObject   exception string message
     * @param complexAttributes user complex attributes
     * @return annotation object
     */
    static Map<String, Object> getAnnotations(Object exceptionObject, Map<String, Object> complexAttributes) {
        Map<String, Object> result = new HashMap<>();
        result.put("Environment Variables", System.getenv());
        if (complexAttributes != null) {
            result.putAll(complexAttributes);
        }

        result.put("Exception", new AnnotationException(exceptionObject));
        return result;
    }
}

