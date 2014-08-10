package usbong.android.builder.events;

/**
 * Created by Rocky Camacho on 8/7/2014.
 */
public class OnScreenDetailsSaveError {

    private Exception exception;

    public OnScreenDetailsSaveError(Exception exception) {
        this.exception = exception;
    }

    public Exception getException() {
        return exception;
    }
}
