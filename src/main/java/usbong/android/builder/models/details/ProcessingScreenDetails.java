package usbong.android.builder.models.details;

import com.google.gson.annotations.Expose;

/**
 * Created by Rocky Camacho on 8/10/2014.
 */
public class ProcessingScreenDetails {

    public static enum ProcessingType {
        SEND_TO_WEB_SERVER("sendToWebServer"),
        SEND_TO_CLOUD_BASED_SERVICE("sendToCloudBasedService"),
        SIMPLE_ENCRYPT("simpleEncrypt");

        private final String name;

        private ProcessingType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static ProcessingType from(String name) {
            for (ProcessingType processingType : ProcessingType.values()) {
                if (processingType.getName().equals(name)) {
                    return processingType;
                }
            }
            return null;
        }
    }

    @Expose
    private String text;
    @Expose
    private String processingType;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getProcessingType() {
        return processingType;
    }

    public void setProcessingType(String processingType) {
        this.processingType = processingType;
    }

}
