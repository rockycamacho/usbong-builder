package usbong.android.builder.converters;

import com.google.gson.Gson;
import usbong.android.builder.enums.UsbongScreenType;
import usbong.android.builder.models.Screen;
import usbong.android.builder.models.details.ImageScreenDetails;
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
        ImageScreenDetails imageScreenDetails = gson.fromJson(screen.details, ImageScreenDetails.class);
        String imageId = imageScreenDetails.getImagePath().substring(imageScreenDetails.getImagePath().lastIndexOf("/") + 1, imageScreenDetails.getImagePath().lastIndexOf("."));
        String screenType = UsbongScreenType.IMAGE_DISPLAY.getName();
        if(imageScreenDetails.isHasCaption()) {
            screenType = UsbongScreenType.CLICKABLE_IMAGE_DISPLAY.getName();
            String imageCaption = StringUtils.toUsbongText(imageScreenDetails.getImageCaption());
            return screenType + SEPARATOR + imageId + SEPARATOR + imageCaption;
        }
        return screenType + SEPARATOR + imageId + SEPARATOR + "null";
    }
}
