package usbong.android.builder.converters;

import usbong.android.builder.enums.UsbongBuilderScreenType;
import usbong.android.builder.models.Screen;
import usbong.android.builder.models.ScreenRelation;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rocky Camacho on 7/14/2014.
 */
public class ScreenConverterStrategy {

    private final static Map<String, ScreenConverter> SCREEN_CONVERTER_MAP = new HashMap<String, ScreenConverter>();

    public static final TextDisplayScreenConverter DEFAULT_SCREEN_CONVERTER = new TextDisplayScreenConverter();

    static {
        SCREEN_CONVERTER_MAP.put(UsbongBuilderScreenType.TEXT.getName(), new TextDisplayScreenConverter());
        SCREEN_CONVERTER_MAP.put(UsbongBuilderScreenType.TEXT_AND_IMAGE.getName(), new TextImageDisplayScreenConverter());
        SCREEN_CONVERTER_MAP.put(UsbongBuilderScreenType.DECISION.getName(), new DecisionScreenConverter());
        SCREEN_CONVERTER_MAP.put(UsbongBuilderScreenType.IMAGE.getName(), new ImageDisplayScreenConverter());
        SCREEN_CONVERTER_MAP.put(UsbongBuilderScreenType.TEXT_INPUT.getName(), new TextInputScreenConverter());
    }

    public static final String DECISION_PREFIX = "DECISION~";

    public String getName(Screen screen) {
        if (SCREEN_CONVERTER_MAP.containsKey(screen.screenType)) {
            return SCREEN_CONVERTER_MAP.get(screen.screenType).getName(screen);
        }
        return DEFAULT_SCREEN_CONVERTER.getName(screen);
    }

    public String getTransition(ScreenRelation screenRelation) {
        if ("DEFAULT".equals(screenRelation.condition)) {
            return getName(screenRelation.child);
        }
        else if(screenRelation.condition.startsWith(DECISION_PREFIX)) {
            return getName(screenRelation.child) + ScreenConverter.SEPARATOR + screenRelation.condition.substring(DECISION_PREFIX.length());
        }
        return getName(screenRelation.child) + ScreenConverter.SEPARATOR + screenRelation.condition;
    }
}
