package usbong.android.builder.models.details;

import usbong.android.builder.enums.ImagePosition;
import usbong.android.builder.enums.UsbongBuilderScreenType;
import usbong.android.builder.models.Screen;
import usbong.android.builder.utils.JsonUtils;
import usbong.android.builder.utils.StringUtils;

/**
 * Created by Rocky Camacho on 8/7/2014.
 */
public class ScreenDetailsFactory {
    public static String create(Screen screen) {
        if (UsbongBuilderScreenType.TEXT_AND_IMAGE.getName().equals(screen.screenType) ||
                UsbongBuilderScreenType.IMAGE.getName().equals(screen.screenType)) {
            ImageScreenDetails imageScreenDetails = new ImageScreenDetails();
            imageScreenDetails.setText(screen.name);
            imageScreenDetails.setImagePosition(ImagePosition.ABOVE_TEXT.getName());
            imageScreenDetails.setImagePath(StringUtils.EMPTY);
            return JsonUtils.toJson(imageScreenDetails);
        }
        if (UsbongBuilderScreenType.TEXT_INPUT.getName().equals(screen.screenType)) {
            TextInputScreenDetails textInputScreenDetails = new TextInputScreenDetails();
            textInputScreenDetails.setText(screen.name);
            textInputScreenDetails.setUnit(StringUtils.EMPTY);
            textInputScreenDetails.setVariableName(StringUtils.EMPTY);
            return JsonUtils.toJson(textInputScreenDetails);
        }
        if (UsbongBuilderScreenType.SPECIAL_INPUT.getName().equals(screen.screenType)) {
            SpecialInputScreenDetails specialInputScreenDetails = new SpecialInputScreenDetails();
            specialInputScreenDetails.setText(screen.name);
            specialInputScreenDetails.setInputType(SpecialInputScreenDetails.InputType.DRAW.getName());
            specialInputScreenDetails.setVideo(StringUtils.EMPTY);
            return JsonUtils.toJson(specialInputScreenDetails);
        }
        if (UsbongBuilderScreenType.PROCESSING.getName().equals(screen.screenType)) {
            ProcessingScreenDetails processingScreenDetails = new ProcessingScreenDetails();
            processingScreenDetails.setText(screen.name);
            processingScreenDetails.setProcessingType(ProcessingScreenDetails.ProcessingType.SEND_TO_WEB_SERVER.getName());
            return JsonUtils.toJson(processingScreenDetails);
        }
        return screen.name;
    }
}
