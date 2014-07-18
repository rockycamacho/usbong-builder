package usbong.android.builder.models;

import com.google.gson.annotations.Expose;

public class ScreenDetails {

    @Expose
    private String text;
    @Expose
    private String imagePosition;
    @Expose
    private String imagePath;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImagePosition() {
        return imagePosition;
    }

    public void setImagePosition(String imagePosition) {
        this.imagePosition = imagePosition;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

}