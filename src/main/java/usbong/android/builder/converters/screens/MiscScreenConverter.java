package usbong.android.builder.converters.screens;

import usbong.android.builder.enums.UsbongScreenType;
import usbong.android.builder.models.Screen;
import usbong.android.builder.models.details.MiscScreenDetails;
import usbong.android.builder.models.details.SendScreenDetails;
import usbong.android.builder.utils.JsonUtils;
import usbong.android.builder.utils.StringUtils;

/**
 * Created by Rocky Camacho on 8/10/2014.
 */
public class MiscScreenConverter implements ScreenConverter {

    @Override
    public String getName(Screen screen) {
        MiscScreenDetails miscScreenDetails = JsonUtils.fromJson(screen.details, MiscScreenDetails.class);
        String screenType = UsbongScreenType.TIMESTAMP_DISPLAY.getName();
        if(MiscScreenDetails.Type.TIMESTAMP.getName().equals(miscScreenDetails.getType())) {
            screenType = UsbongScreenType.TIMESTAMP_DISPLAY.getName();
        }
        if(MiscScreenDetails.Type.SIMPLE_ENCRYPT.getName().equals(miscScreenDetails.getType())) {
            screenType = UsbongScreenType.SIMPLE_ENCRYPT.getName();
        }
        String content = StringUtils.toUsbongText(miscScreenDetails.getText());
        return screenType + SEPARATOR + content;
    }
}
