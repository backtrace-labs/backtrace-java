package backtrace.io;

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
     *
     * @param exceptionMessage
     * @param complexAttributes
     * @return
     */
    static Map<String, Object> getAnnotations(Object exceptionMessage, Map<String, Object> complexAttributes) {
        Map<String, Object> result = new HashMap<>();
        result.put("Environment Variables", System.getenv());
        if (complexAttributes != null) {
            result.putAll(complexAttributes);
        }

        result.put("Exception", new AnnotationException(exceptionMessage));
        return result;
    }
}

