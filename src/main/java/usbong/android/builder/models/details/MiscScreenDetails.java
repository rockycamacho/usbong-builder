package usbong.android.builder.models.details;

import com.google.gson.annotations.Expose;

/**
 * Created by Rocky Camacho on 8/10/2014.
 */
public class MiscScreenDetails {

    public static enum Type {
        TIMESTAMP("timestamp"),
        SIMPLE_ENCRYPT("simpleEncrypt");

        private final String name;

        private Type(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static Type from(String name) {
            for (Type processingType : Type.values()) {
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
    private String type;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
