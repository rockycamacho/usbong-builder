package usbong.android.builder.converters.screens;

import com.google.gson.Gson;
import usbong.android.builder.enums.ImagePosition;
import usbong.android.builder.enums.UsbongScreenType;
import usbong.android.builder.models.Screen;
import usbong.android.builder.models.details.ImageScreenDetails;
import usbong.android.builder.utils.JsonUtils;
import usbong.android.builder.utils.StringUtils;

/**
 * Created by Rocky Camacho on 7/14/2014.
 */
public class TextImageDisplayScreenConverter implements ScreenConverter {

    @Override
    public String getName(Screen screen) {
        ImageScreenDetails imageScreenDetails = JsonUtils.fromJson(screen.details, ImageScreenDetails.class);
        String imageId = imageScreenDetails.getImagePath().substring(imageScreenDetails.getImagePath().lastIndexOf("/") + 1, imageScreenDetails.getImagePath().lastIndexOf("."));
        String screenType = getScreenType(imageScreenDetails);
        String captionPart = StringUtils.EMPTY;
        if (imageScreenDetails.isHasCaption()) {
            captionPart =  SEPARATOR + StringUtils.toUsbongText(imageScreenDetails.getImageCaption());
        }
        return screenType + SEPARATOR + imageId + captionPart + SEPARATOR + StringUtils.toUsbongText(imageScreenDetails.getText());
    }

    private String getScreenType(ImageScreenDetails imageScreenDetails) {
        if (ImagePosition.ABOVE_TEXT.equals(imageScreenDetails.getImagePosition())) {
            if (imageScreenDetails.isHasCaption()) {
                return UsbongScreenType.CLICKABLE_IMAGE_TEXT_DISPLAY.getName();
            } else {
                return UsbongScreenType.IMAGE_TEXT_DISPLAY.getName();
            }
        } else {
            if (imageScreenDetails.isHasCaption()) {
                return UsbongScreenType.TEXT_CLICKABLE_IMAGE_DISPLAY.getName();
            } else {
                return UsbongScreenType.TEXT_IMAGE_DISPLAY.getName();
            }
        }
    }
}
