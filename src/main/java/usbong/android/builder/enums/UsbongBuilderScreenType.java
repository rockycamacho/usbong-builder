package usbong.android.builder.enums;

/**
 * Created by Rocky Camacho on 6/27/2014.
 */
public enum UsbongBuilderScreenType {

    DECISION("decision"),
    TEXT("text"),
    IMAGE("image"),
    TEXT_AND_IMAGE("textImage"),
    TEXT_INPUT("textInput"),
    SPECIAL_INPUT("specialInput"),
    VIDEO("video"),
    MISC("misc"),
    SEND("send"),
    LIST("list");

    private final String name;

    UsbongBuilderScreenType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
