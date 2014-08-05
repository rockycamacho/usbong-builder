package usbong.android.builder.converters;

import com.google.gson.Gson;
import usbong.android.builder.enums.ImagePosition;
import usbong.android.builder.enums.UsbongScreenType;
import usbong.android.builder.models.Screen;
import usbong.android.builder.models.ScreenDetails;
import usbong.android.builder.utils.StringUtils;

/**
 * Created by Rocky Camacho on 7/14/2014.
 */
public class TextImageDisplayScreenConverter implements ScreenConverter {

    private Gson gson;

    public TextImageDisplayScreenConverter() {
        gson = new Gson();
    }

    @Override
    public String getName(Screen screen) {
        ScreenDetails screenDetails = gson.fromJson(screen.details, ScreenDetails.class);
        String imageId = screenDetails.getImagePath().substring(screenDetails.getImagePath().lastIndexOf("/") + 1, screenDetails.getImagePath().lastIndexOf("."));
        String screenType = getScreenType(screenDetails);
        if(screenDetails.isHasCaption()) {
            return screenType + SEPARATOR + imageId + SEPARATOR + StringUtils.toUsbongText(screenDetails.getImageCaption());
        }
        return screenType + SEPARATOR + imageId + SEPARATOR + StringUtils.toUsbongText(screenDetails.getText());
    }

    private String getScreenType(ScreenDetails screenDetails) {
        if(ImagePosition.ABOVE_TEXT.equals(screenDetails.getImagePosition())) {
            if(screenDetails.isHasCaption()) {
                return UsbongScreenType.CLICKABLE_IMAGE_TEXT_DISPLAY.getName();
            }
            else {
                return UsbongScreenType.IMAGE_TEXT_DISPLAY.getName();
            }
        }
        else {
            if(screenDetails.isHasCaption()) {
                return UsbongScreenType.TEXT_CLICKABLE_IMAGE_DISPLAY.getName();
            }
            else {
                return UsbongScreenType.TEXT_IMAGE_DISPLAY.getName();
            }
        }
    }
}
