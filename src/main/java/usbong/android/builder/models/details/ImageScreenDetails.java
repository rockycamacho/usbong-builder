package usbong.android.builder.models.details;

import com.google.gson.annotations.Expose;

public class ImageScreenDetails {

    @Expose
    private String text;
    @Expose
    private String imagePosition;
    @Expose
    private String imagePath;
    @Expose
    private boolean hasCaption;
    @Expose
    private String imageCaption;

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

    public boolean isHasCaption() {
        return hasCaption;
    }

    public void setHasCaption(boolean hasCaption) {
        this.hasCaption = hasCaption;
    }

    public String getImageCaption() {
        return imageCaption;
    }

    public void setImageCaption(String imageCaption) {
        this.imageCaption = imageCaption;
    }

}