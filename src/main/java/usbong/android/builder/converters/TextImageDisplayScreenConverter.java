package usbong.android.builder.converters;

import com.google.gson.Gson;
import usbong.android.builder.enums.ImagePosition;
import usbong.android.builder.enums.UsbongScreenType;
import usbong.android.builder.models.Screen;
import usbong.android.builder.models.ScreenDetails;

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
        String screenType = UsbongScreenType.TEXT_IMAGE_DISPLAY.getName();
        if(ImagePosition.ABOVE_TEXT.equals(screenDetails.getImagePosition())) {
            screenType = UsbongScreenType.IMAGE_TEXT_DISPLAY.getName();
        }
        return screenType + SEPARATOR + imageId + SEPARATOR + screen.name;
    }
}
