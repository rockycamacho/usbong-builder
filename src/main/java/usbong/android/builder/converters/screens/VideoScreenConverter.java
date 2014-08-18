package usbong.android.builder.converters.screens;

import usbong.android.builder.enums.UsbongScreenType;
import usbong.android.builder.models.Screen;
import usbong.android.builder.models.details.SpecialInputScreenDetails;
import usbong.android.builder.models.details.VideoScreenDetails;
import usbong.android.builder.utils.JsonUtils;
import usbong.android.builder.utils.StringUtils;

/**
 * Created by Rocky Camacho on 8/10/2014.
 */
public class VideoScreenConverter implements ScreenConverter {

    @Override
    public String getName(Screen screen) {
        VideoScreenDetails videoScreenDetails = JsonUtils.fromJson(screen.details, VideoScreenDetails.class);
        String content = StringUtils.toUsbongText(videoScreenDetails.getText());
        UsbongScreenType inputType = getUsbongScreenType(content);
        String contentPart = getContentPart(content, inputType);
        return inputType.getName() + SEPARATOR + videoScreenDetails.getVideo() + contentPart;
    }

    private String getContentPart(String content, UsbongScreenType inputType) {
        String contentPart = StringUtils.EMPTY;
        if (UsbongScreenType.VIDEO_FROM_FILE_WITH_TEXT.equals(inputType)) {
            contentPart = SEPARATOR + content;
        }
        return contentPart;
    }

    private UsbongScreenType getUsbongScreenType(String content) {
        if (!StringUtils.isEmpty(content)) {
            return UsbongScreenType.VIDEO_FROM_FILE_WITH_TEXT;
        }
        return UsbongScreenType.VIDEO_FROM_FILE;
    }
}
