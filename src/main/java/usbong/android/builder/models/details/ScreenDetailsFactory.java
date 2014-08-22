package usbong.android.builder.models.details;

import usbong.android.builder.enums.ImagePosition;
import usbong.android.builder.enums.UsbongBuilderScreenType;
import usbong.android.builder.models.Screen;
import usbong.android.builder.utils.JsonUtils;
import usbong.android.builder.utils.StringUtils;

import java.util.ArrayList;

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
            textInputScreenDetails.setAnswers(new ArrayList<String>());
            return JsonUtils.toJson(textInputScreenDetails);
        }
        if (UsbongBuilderScreenType.SPECIAL_INPUT.getName().equals(screen.screenType)) {
            SpecialInputScreenDetails specialInputScreenDetails = new SpecialInputScreenDetails();
            specialInputScreenDetails.setText(screen.name);
            specialInputScreenDetails.setInputType(SpecialInputScreenDetails.InputType.DRAW.getName());
            specialInputScreenDetails.setVideo(StringUtils.EMPTY);
            return JsonUtils.toJson(specialInputScreenDetails);
        }
        if (UsbongBuilderScreenType.SEND.getName().equals(screen.screenType)) {
            SendScreenDetails sendScreenDetails = new SendScreenDetails();
            sendScreenDetails.setText(screen.name);
            sendScreenDetails.setType(SendScreenDetails.Type.SEND_TO_WEB_SERVER.getName());
            return JsonUtils.toJson(sendScreenDetails);
        }
        if (UsbongBuilderScreenType.VIDEO.getName().equals(screen.screenType)) {
            VideoScreenDetails videoScreenDetails = new VideoScreenDetails();
            videoScreenDetails.setText(screen.name);
            videoScreenDetails.setVideo(StringUtils.EMPTY);
            return JsonUtils.toJson(videoScreenDetails);
        }
        if (UsbongBuilderScreenType.MISC.getName().equals(screen.screenType)) {
            MiscScreenDetails miscScreenDetails = new MiscScreenDetails();
            miscScreenDetails.setText(screen.name);
            miscScreenDetails.setType(MiscScreenDetails.Type.SIMPLE_ENCRYPT.getName());
            return JsonUtils.toJson(miscScreenDetails);
        }
        if (UsbongBuilderScreenType.LIST.getName().equals(screen.screenType)) {
            ListScreenDetails listScreenDetails = new ListScreenDetails();
            listScreenDetails.setText(screen.name);
            listScreenDetails.setType(ListScreenDetails.ListType.ANY_ANSWER.getName());
            listScreenDetails.setItems(new ArrayList<String>());
            listScreenDetails.setAnswer(0);
            return JsonUtils.toJson(listScreenDetails);
        }
        return screen.name;
    }
}
