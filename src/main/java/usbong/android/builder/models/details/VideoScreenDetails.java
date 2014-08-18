package usbong.android.builder.models.details;

import com.google.gson.annotations.Expose;

/**
 * Created by Rocky Camacho on 8/18/2014.
 */
public class VideoScreenDetails {

    @Expose
    private String text;
    @Expose
    private String video;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

}
