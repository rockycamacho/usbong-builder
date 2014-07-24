package usbong.android.builder.utils;

import com.google.gson.Gson;
import usbong.android.builder.models.ScreenDetails;

/**
 * Created by Rocky Camacho on 7/19/2014.
 */
public class JsonUtils {

    private static final Gson GSON = new Gson();

    public static <T> String toJson(T object) {
        return GSON.toJson(object);
    }

    public static <T> T fromJson(String details, Class<T> clazz) {
        return GSON.fromJson(details, clazz);
    }
}
