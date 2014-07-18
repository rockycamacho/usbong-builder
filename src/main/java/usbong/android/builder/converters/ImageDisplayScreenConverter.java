package usbong.android.builder.converters;

import com.google.gson.Gson;
import usbong.android.builder.enums.UsbongScreenType;
import usbong.android.builder.models.Screen;
import usbong.android.builder.models.ScreenDetails;

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
        return UsbongScreenType.IMAGE_DISPLAY.getName() + SEPARATOR + imageId + SEPARATOR + screenDetails.getText();
    }
}
