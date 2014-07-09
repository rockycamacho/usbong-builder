package usbong.android.builder.exceptions;

/**
 * Created by Rocky Camacho on 7/5/2014.
 */
public class ParserException extends Exception {

    public ParserException(String message) {
        super(message);
    }

    public ParserException(String message, Throwable tr) {
        super(message, tr);
    }
}
