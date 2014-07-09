package usbong.android.builder.events;

/**
 * Created by Rocky Camacho on 6/29/2014.
 */
public class OnScreenDetailsSave {
    private final String name;
    private final String content;

    public OnScreenDetailsSave(String name, String content) {
        this.name = name;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public String getName() {
        return name;
    }
}
