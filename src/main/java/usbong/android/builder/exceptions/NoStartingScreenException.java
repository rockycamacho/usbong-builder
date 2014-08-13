package usbong.android.builder.exceptions;

/**
 * Created by Rocky Camacho on 8/13/2014.
 */
public class NoStartingScreenException extends Exception {
    public NoStartingScreenException(String message) {
        super(message);
    }

    public NoStartingScreenException(String message, Throwable tr) {
        super(message, tr);
    }
}
