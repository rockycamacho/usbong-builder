package usbong.android.builder.converters;

import com.google.gson.Gson;
import usbong.android.builder.enums.UsbongScreenType;
import usbong.android.builder.models.Screen;
import usbong.android.builder.models.ScreenDetails;
import usbong.android.builder.utils.StringUtils;

/**
 * Created by Rocky Camacho on 7/14/2014.
 */
public class ImageDisplayScreenConverter implements ScreenConverter {

    private Gson gson;

    public ImageDisplayScreenConverter() {
        gson = new Gson();
    }

    @Override
    public String getName(Screen screen) {
        ScreenDetails screenDetails = gson.fromJson(screen.details, ScreenDetails.class);
        String imageId = screenDetails.getImagePath().substring(screenDetails.getImagePath().lastIndexOf("/") + 1, screenDetails.getImagePath().lastIndexOf("."));
        String screenType = UsbongScreenType.IMAGE_DISPLAY.getName();
        if(screenDetails.isHasCaption()) {
            screenType = UsbongScreenType.CLICKABLE_IMAGE_DISPLAY.getName();
            String imageCaption = StringUtils.toUsbongText(screenDetails.getImageCaption());
            return screenType + SEPARATOR + imageId + SEPARATOR + imageCaption;
        }
        return screenType + SEPARATOR + imageId + SEPARATOR + "null";
    }
}
