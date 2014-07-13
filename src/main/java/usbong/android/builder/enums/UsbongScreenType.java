package usbong.android.builder.enums;

/**
 * Created by Rocky Camacho on 6/27/2014.
 */
public enum UsbongScreenType {

    DECISION("decision"),
    TEXT_FIELD("textField"),
    TEXT_FIELD_WITH_UNIT("textFieldWithUnit"),
    TEXT_FIELD_NUMERICAL("textFieldNumerical"),
    TEXT_AREA("textArea"),
    DATE("date"),
    TEXT_DISPLAY("textDisplay"),
    IMAGE_DISPLAY("imageDisplay"),
    TEXT_IMAGE_DISPLAY("textImageDisplay"),
    IMAGE_TEXT_DISPLAY("imageTextDisplay"),
    CLICKABLE_IMAGE_DISPLAY("clickableImageDisplay"),
    TEXT_CLICKABLE_IMAGE_DISPLAY("textClickableImageDisplay"),
    CLICKABLE_IMAGE_TEXT_DISPLAY("clickableImageTextDisplay"),
    GPS("gps"),
    CLASSIFICATION("classification"),
    CHECKLIST("checkList"),
    RADIO_BUTTONS("radioButtons"),
    AUDIO_RECORDER("audioRecorder"),
    PHOTO_CAPTURE("photoCapture"),
    LINK("link"),
    SEND_TO_WEBSERVER("sendToWebServer"),
    SEND_TO_CLOUD_BASED_SERVICE("sendToCloudBasedService"),
    PAINT("paint"),
    QR_CODE_READER("qrCodeReader"),
    TEXT_FIELD_WITH_ANSWER("textFieldWithAnswer"),
    TEXT_AREA_WITH_ANSWER("textAreaWithAnswer"),
    RADIO_BUTTONS_WITH_ANSWER("radioButtonsWithAnswer"),
    VIDEO_FROM_FILE("videoFromFile"),
    VIDEO_FROM_FILE_WITH_TEXT("videoFromFileWithText"),
    TIMESTAMP_DISPLAY("timestampDisplay"),
    SIMPLE_ENCRYPT("simpleEncrypt");

    private final String name;

    UsbongScreenType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
