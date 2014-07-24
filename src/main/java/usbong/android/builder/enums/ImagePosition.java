package usbong.android.builder.enums;

/**
 * Created by Rocky Camacho on 6/27/2014.
 */
public enum ImagePosition {

    ABOVE_TEXT("Above Text"),
    BELOW_TEXT("Below Text");

    private final String name;

    ImagePosition(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ImagePosition from(String name) {
        for (ImagePosition imagePosition : ImagePosition.values()) {
            if (imagePosition.getName().equals(name)) {
                return imagePosition;
            }
        }
        return null;
    }
}
