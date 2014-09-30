package usbong.android.builder.converters.screens;

import com.google.gson.Gson;
import usbong.android.builder.enums.UsbongScreenType;
import usbong.android.builder.models.Screen;
import usbong.android.builder.models.details.ImageScreenDetails;
import usbong.android.builder.utils.JsonUtils;
import usbong.android.builder.utils.StringUtils;

/**
 * Created by Rocky Camacho on 7/14/2014.
 */
public class ImageDisplayScreenConverter implements ScreenConverter {

    @Override
    public String getName(Screen screen) {
        ImageScreenDetails imageScreenDetails = JsonUtils.fromJson(screen.details, ImageScreenDetails.class);
        String imageId = imageScreenDetails.getImagePath().substring(imageScreenDetails.getImagePath().lastIndexOf("/") + 1, imageScreenDetails.getImagePath().lastIndexOf("."));
        String screenType = UsbongScreenType.IMAGE_DISPLAY.getName();
        String captionPart = StringUtils.EMPTY;
        if (imageScreenDetails.isHasCaption()) {
            screenType = UsbongScreenType.CLICKABLE_IMAGE_DISPLAY.getName();
            captionPart = SEPARATOR + StringUtils.toUsbongText(imageScreenDetails.getImageCaption());
        }
        return screenType + SEPARATOR + imageId + captionPart;
    }
}
