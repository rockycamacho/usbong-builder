package usbong.android.builder.models;

import com.google.gson.annotations.Expose;

public class TextImageDetails {

    @Expose
    private String details;
    @Expose
    private String imagePosition;
    @Expose
    private String imagePath;

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
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