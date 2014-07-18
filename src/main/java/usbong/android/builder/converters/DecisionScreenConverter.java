package usbong.android.builder.converters;

import usbong.android.builder.enums.UsbongScreenType;
import usbong.android.builder.models.Screen;

/**
 * Created by Rocky Camacho on 7/14/2014.
 */
public class DecisionScreenConverter implements ScreenConverter {

    @Override
    public String getName(Screen screen) {
        return UsbongScreenType.DECISION.getName() + SEPARATOR + screen.details;
    }
}
