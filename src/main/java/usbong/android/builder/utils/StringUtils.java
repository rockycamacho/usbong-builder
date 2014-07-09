package usbong.android.builder.utils;

/**
 * Created by Rocky Camacho on 7/9/2014.
 */
public class StringUtils {

    private StringUtils() {}

    public static boolean isEmpty(String text) {
        return text == null || text.trim().length() == 0;
    }
}