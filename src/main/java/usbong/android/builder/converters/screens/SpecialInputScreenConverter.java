package usbong.android.builder.converters.screens;

import com.google.gson.Gson;
import usbong.android.builder.enums.UsbongScreenType;
import usbong.android.builder.models.Screen;
import usbong.android.builder.models.details.SpecialInputScreenDetails;
import usbong.android.builder.utils.JsonUtils;
import usbong.android.builder.utils.StringUtils;

/**
 * Created by Rocky Camacho on 8/10/2014.
 */
public class SpecialInputScreenConverter implements ScreenConverter {

    @Override
    public String getName(Screen screen) {
        SpecialInputScreenDetails specialInputScreenDetails = JsonUtils.fromJson(screen.details, SpecialInputScreenDetails.class);
        String content = StringUtils.toUsbongText(specialInputScreenDetails.getText());
        UsbongScreenType inputType = getUsbongScreenType(specialInputScreenDetails, content);
        if (UsbongScreenType.VIDEO_FROM_FILE.equals(inputType)) {
            return inputType.getName() + SEPARATOR + specialInputScreenDetails.getVideo();
        }
        if (UsbongScreenType.VIDEO_FROM_FILE_WITH_TEXT.equals(inputType)) {
            return inputType.getName() + SEPARATOR + specialInputScreenDetails.getVideo() + SEPARATOR + content;
        }
        return inputType.getName() + SEPARATOR + content;
    }

    private UsbongScreenType getUsbongScreenType(SpecialInputScreenDetails specialInputScreenDetails, String content) {
        UsbongScreenType inputType = UsbongScreenType.DATE;
        if (SpecialInputScreenDetails.InputType.VIDEO.getName().equals(specialInputScreenDetails.getInputType())) {
            inputType = UsbongScreenType.VIDEO_FROM_FILE;
            if (!StringUtils.isEmpty(content)) {
                inputType = UsbongScreenType.VIDEO_FROM_FILE_WITH_TEXT;
            }
        } else if (SpecialInputScreenDetails.InputType.DATE.getName().equals(specialInputScreenDetails.getInputType())) {
            inputType = UsbongScreenType.DATE;
        } else if (SpecialInputScreenDetails.InputType.AUDIO.getName().equals(specialInputScreenDetails.getInputType())) {
            inputType = UsbongScreenType.AUDIO_RECORDER;
        } else if (SpecialInputScreenDetails.InputType.DRAW.getName().equals(specialInputScreenDetails.getInputType())) {
            inputType = UsbongScreenType.PAINT;
        } else if (SpecialInputScreenDetails.InputType.CAMERA.getName().equals(specialInputScreenDetails.getInputType())) {
            inputType = UsbongScreenType.PHOTO_CAPTURE;
        } else if (SpecialInputScreenDetails.InputType.QR_CODE.getName().equals(specialInputScreenDetails.getInputType())) {
            inputType = UsbongScreenType.QR_CODE_READER;
        } else if (SpecialInputScreenDetails.InputType.TIMESTAMP.getName().equals(specialInputScreenDetails.getInputType())) {
            inputType = UsbongScreenType.TIMESTAMP_DISPLAY;
        }
        return inputType;
    }
}
