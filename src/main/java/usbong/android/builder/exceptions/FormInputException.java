package usbong.android.builder.exceptions;

/**
 * Created by Rocky Camacho on 8/7/2014.
 */
public class FormInputException extends Exception {
    public FormInputException(String message) {
        super(message);
    }

    public FormInputException(String message, Throwable tr) {
        super(message, tr);
    }
}
