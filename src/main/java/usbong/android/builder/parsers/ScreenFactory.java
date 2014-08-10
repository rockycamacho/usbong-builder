package usbong.android.builder.parsers;

import android.util.Log;
import usbong.android.builder.enums.ImagePosition;
import usbong.android.builder.enums.UsbongBuilderScreenType;
import usbong.android.builder.enums.UsbongScreenType;
import usbong.android.builder.models.Screen;
import usbong.android.builder.models.details.ImageScreenDetails;
import usbong.android.builder.models.details.TextInputScreenDetails;
import usbong.android.builder.utils.JsonUtils;
import usbong.android.builder.utils.StringUtils;

import java.io.File;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Rocky Camacho on 8/10/2014.
 */
public class ScreenFactory {

    private static final String TAG = ScreenFactory.class.getSimpleName();
    private static final String[] IMAGE_FILE_EXTENSIONS = new String[]{".jpg", ".jpeg", ".png"};
    private static final Pattern GET_INPUT_PATTERN = Pattern.compile("@([a-z][a-zA-Z0-9]*)=getInput\\(\\)");

    public static Screen createFrom(String[] attrs, String resFolder) {
        Screen screen = null;
        String screenType = attrs[0];

        if (UsbongScreenType.TEXT_DISPLAY.getName().equals(screenType)) {
            screen = new Screen();
            String details = StringUtils.toUsbongBuilderText(attrs[1]);
            screen.screenType = UsbongBuilderScreenType.TEXT.getName();
            screen.name = details;
            screen.details = details;
        } else if (UsbongScreenType.LINK.getName().equals(screenType)) {
            screen = new Screen();
            String details = StringUtils.toUsbongBuilderText(attrs[attrs.length - 1]);
            String name = details;
            if (attrs.length > 2) {
                name = attrs[1] + "~" + details;
            }
            screen.screenType = UsbongBuilderScreenType.DECISION.getName();
            screen.name = name;
            screen.details = details;
        } else if (UsbongScreenType.DECISION.getName().equals(screenType)) {
            screen = new Screen();
            String details = StringUtils.toUsbongBuilderText(attrs[attrs.length - 1]);
            String name = details;
            if (attrs.length > 2) {
                name = attrs[1] + "~" + details;
            }
            screen.screenType = UsbongBuilderScreenType.DECISION.getName();
            screen.name = name;
            screen.details = details;
        } else if (UsbongScreenType.IMAGE_DISPLAY.getName().equals(screenType) ||
                UsbongScreenType.CLICKABLE_IMAGE_DISPLAY.getName().equals(screenType)) {
            String name = StringUtils.toUsbongBuilderText(attrs[attrs.length - 1]);
            ImageScreenDetails imageScreenDetails = new ImageScreenDetails();
            imageScreenDetails.setImagePath(getImagePath(resFolder, attrs[1], IMAGE_FILE_EXTENSIONS));
            if(UsbongScreenType.CLICKABLE_IMAGE_DISPLAY.getName().equals(screenType)) {
                imageScreenDetails.setHasCaption(true);
                imageScreenDetails.setImageCaption(name);
            }
            screen = new Screen();
            screen.screenType = UsbongBuilderScreenType.IMAGE.getName();
            screen.name = name;
            screen.details = JsonUtils.toJson(imageScreenDetails);
        } else if (UsbongScreenType.TEXT_IMAGE_DISPLAY.getName().equals(screenType) ||
                UsbongScreenType.IMAGE_TEXT_DISPLAY.getName().equals(screenType) ||
                UsbongScreenType.TEXT_CLICKABLE_IMAGE_DISPLAY.getName().equals(screenType) ||
                UsbongScreenType.CLICKABLE_IMAGE_TEXT_DISPLAY.getName().equals(screenType)) {
            String details = StringUtils.toUsbongBuilderText(attrs[attrs.length - 1]);
            ImageScreenDetails imageScreenDetails = new ImageScreenDetails();
            imageScreenDetails.setText(details);
            ImagePosition imagePosition = ImagePosition.BELOW_TEXT;
            if(UsbongScreenType.TEXT_IMAGE_DISPLAY.getName().equals(screenType) ||
                    UsbongScreenType.TEXT_CLICKABLE_IMAGE_DISPLAY.getName().equals(screenType)) {
                imagePosition = ImagePosition.ABOVE_TEXT;
            }
            imageScreenDetails.setImagePosition(imagePosition.getName());
            imageScreenDetails.setImagePath(getImagePath(resFolder, attrs[1], IMAGE_FILE_EXTENSIONS));
            if(UsbongScreenType.TEXT_CLICKABLE_IMAGE_DISPLAY.getName().equals(screenType) ||
                    UsbongScreenType.CLICKABLE_IMAGE_TEXT_DISPLAY.getName().equals(screenType)) {
                imageScreenDetails.setHasCaption(true);
                String imageCaption = StringUtils.toUsbongBuilderText(attrs[2]);
                imageScreenDetails.setImageCaption(imageCaption);
            }
            screen = new Screen();
            screen.screenType = UsbongBuilderScreenType.TEXT_AND_IMAGE.getName();
            screen.name = details;
            screen.details = JsonUtils.toJson(imageScreenDetails);
        } else if(UsbongScreenType.TEXT_AREA.getName().equals(screenType) ||
                UsbongScreenType.TEXT_FIELD.getName().equals(screenType) ||
                UsbongScreenType.TEXT_FIELD_NUMERICAL.getName().equals(screenType) ||
                UsbongScreenType.TEXT_FIELD_WITH_UNIT.getName().equals(screenType)) {
            String details = StringUtils.toUsbongBuilderText(attrs[attrs.length - 1]);
            TextInputScreenDetails textInputScreenDetails = new TextInputScreenDetails();
            textInputScreenDetails.setMultiLine(UsbongScreenType.TEXT_AREA.getName().equals(screenType));
            String inputType = TextInputScreenDetails.ALPHA_NUMERIC;
            if(UsbongScreenType.TEXT_FIELD_NUMERICAL.getName().equals(screenType) ||
                    UsbongScreenType.TEXT_FIELD_WITH_UNIT.getName().equals(screenType)) {
                inputType = TextInputScreenDetails.NUMERIC;
                if(UsbongScreenType.TEXT_FIELD_WITH_UNIT.getName().equals(screenType)) {
                    String unit = StringUtils.toUsbongBuilderText(attrs[1]);
                    textInputScreenDetails.setHasUnit(true);
                    textInputScreenDetails.setUnit(unit);
                }
            }
            for(String attr : attrs) {
                Matcher matcher = GET_INPUT_PATTERN.matcher(attr);
                if(matcher.matches()) {
                    String variableName = matcher.group(1);
                    textInputScreenDetails.setStoreVariable(true);
                    textInputScreenDetails.setVariableName(variableName);
                    break;
                }
            }
            textInputScreenDetails.setInputType(inputType);

            screen = new Screen();
            screen.screenType = UsbongBuilderScreenType.TEXT_INPUT.getName();
            screen.name = details;
            screen.details = JsonUtils.toJson(textInputScreenDetails);
        } else {
            Log.w(TAG, "unhandled screenType: " + screenType);
            Log.w(TAG, "screen details: " + Arrays.toString(attrs));
        }
        return screen;
    }

    private static String getImagePath(String resFolder, String imageId, String... fileExtensions) {
        for (String fileExtension : fileExtensions) {
            File imageFile = new File(resFolder + File.separator + imageId + fileExtension);
            if (imageFile.exists()) {
                return imageFile.getAbsolutePath();
            }
        }
        return null;
    }
}
