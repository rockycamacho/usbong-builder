package usbong.android.builder.models.details;

import com.google.gson.annotations.Expose;

/**
 * Created by Rocky Camacho on 8/10/2014.
 */
public class SpecialInputScreenDetails {

    public static enum InputType {
        DRAW("draw"),
        DATE("date"),
        AUDIO("audio"),
        CAMERA("camera"),
        QR_CODE("qrCode");

        private final String name;

        private InputType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static InputType from(String name) {
            for (InputType imageType : InputType.values()) {
                if (imageType.getName().equals(name)) {
                    return imageType;
                }
            }
            return null;
        }
    }

    @Expose
    private String text;
    @Expose
    private String inputType;
    @Expose
    private String video;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

}
