package lesson5.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.internal.platform.Platform;
import okhttp3.logging.HttpLoggingInterceptor;

public class PrettyLogger implements HttpLoggingInterceptor.Logger {
    ObjectMapper mapper = new ObjectMapper();
    @Override
    public void log(String message) {
        String trimMessage = message.trim();
        if ((trimMessage.startsWith("{") && trimMessage.endsWith("}"))
    || (trimMessage.startsWith("[") && trimMessage.endsWith("]"))) {

            try {
                Object object = mapper.readValue(message, Object.class);
                String prettyJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
                Platform.get().log(Platform.INFO, prettyJson, null);
            } catch (JsonProcessingException e) {
               Platform.get().log(Platform.WARN, message, e);
            }
        }
        else {
            Platform.get().log(Platform.INFO, message, null);
        }
    }
}
