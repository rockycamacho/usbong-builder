package usbong.android.builder.converters.screens;

import usbong.android.builder.enums.UsbongScreenType;
import usbong.android.builder.models.Screen;
import usbong.android.builder.models.details.ListScreenDetails;
import usbong.android.builder.utils.JsonUtils;
import usbong.android.builder.utils.StringUtils;

/**
 * Created by Rocky Camacho on 8/13/2014.
 */
public class ListScreenConverter implements ScreenConverter {
    @Override
    public String getName(Screen screen) {
        ListScreenDetails listScreenDetails = JsonUtils.fromJson(screen.details, ListScreenDetails.class);
        if(ListScreenDetails.ListType.NO_RESPONSE.getName().equals(listScreenDetails.getType())) {
            return StringUtils.toUsbongText(listScreenDetails.getText());
        }
        String screenType = UsbongScreenType.RADIO_BUTTONS.getName();
        if(ListScreenDetails.ListType.MULTIPLE_RESPONSE.getName().equals(listScreenDetails.getType())) {
            screenType = UsbongScreenType.CHECKLIST.getName();
        }
        return screen + SEPARATOR + StringUtils.toUsbongText(listScreenDetails.getText());
    }
}
