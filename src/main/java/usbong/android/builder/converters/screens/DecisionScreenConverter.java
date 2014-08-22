package usbong.android.builder.converters.screens;

import usbong.android.builder.enums.UsbongScreenType;
import usbong.android.builder.models.Screen;
import usbong.android.builder.utils.StringUtils;

/**
 * Created by Rocky Camacho on 7/14/2014.
 */
public class DecisionScreenConverter implements ScreenConverter {

    @Override
    public String getName(Screen screen) {
        return UsbongScreenType.LINK.getName() + SEPARATOR + StringUtils.toUsbongText(screen.details);
    }
}
