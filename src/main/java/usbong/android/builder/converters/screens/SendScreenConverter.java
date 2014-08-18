package usbong.android.builder.converters.screens;

import usbong.android.builder.enums.UsbongScreenType;
import usbong.android.builder.models.Screen;
import usbong.android.builder.models.details.SendScreenDetails;
import usbong.android.builder.utils.JsonUtils;
import usbong.android.builder.utils.StringUtils;

/**
 * Created by Rocky Camacho on 8/10/2014.
 */
public class SendScreenConverter implements ScreenConverter {

    @Override
    public String getName(Screen screen) {
        SendScreenDetails sendScreenDetails = JsonUtils.fromJson(screen.details, SendScreenDetails.class);
        String screenType = UsbongScreenType.SEND_TO_CLOUD_BASED_SERVICE.getName();
        if(SendScreenDetails.Type.SEND_TO_CLOUD_BASED_SERVICE.getName().equals(sendScreenDetails.getType())) {
            screenType = UsbongScreenType.SEND_TO_CLOUD_BASED_SERVICE.getName();
        }
        if(SendScreenDetails.Type.SEND_TO_WEB_SERVER.getName().equals(sendScreenDetails.getType())) {
            screenType = UsbongScreenType.SEND_TO_WEBSERVER.getName();
        }
        String content = StringUtils.toUsbongText(sendScreenDetails.getText());
        return screenType + SEPARATOR + content;
    }
}
