package usbong.android.builder.converters.screens;

import usbong.android.builder.enums.UsbongScreenType;
import usbong.android.builder.models.Screen;
import usbong.android.builder.models.details.ListScreenDetails;
import usbong.android.builder.utils.JsonUtils;
import usbong.android.builder.utils.StringUtils;

/**
 * Created by Rocky Camacho on 8/13/2014.
 */
public class ListScreenConverter implements ScreenConverter {
    @Override
    public String getName(Screen screen) {
        ListScreenDetails listScreenDetails = JsonUtils.fromJson(screen.details, ListScreenDetails.class);
        if(ListScreenDetails.ListType.ANY_ANSWER.getName().equals(listScreenDetails.getType())) {
            return StringUtils.toUsbongText(listScreenDetails.getText());
        }
        String contentPart = StringUtils.toUsbongText(listScreenDetails.getText());
        String screenType = StringUtils.EMPTY;
        if(ListScreenDetails.ListType.SINGLE_ANSWER.getName().equals(listScreenDetails.getType())) {
            screenType = UsbongScreenType.RADIO_BUTTONS.getName();
            if(listScreenDetails.isHasAnswer()) {
                screenType = UsbongScreenType.RADIO_BUTTONS_WITH_ANSWER.getName();
                String answerPart = getAnswerPart(listScreenDetails);
                contentPart += answerPart;
            }
        }
        else  if(ListScreenDetails.ListType.MULTIPLE_ANSWERS.getName().equals(listScreenDetails.getType())) {
            screenType = UsbongScreenType.CHECKLIST.getName();
            contentPart = listScreenDetails.getNumberOfChecksNeeded() + SEPARATOR + contentPart;
        }
        return screenType + SEPARATOR + contentPart;
    }

    private String getAnswerPart(ListScreenDetails listScreenDetails) {
        String answerPart = StringUtils.EMPTY;
        if(listScreenDetails.isHasAnswer()) {
            answerPart = "?Answer=" + listScreenDetails.getAnswer();
        }
        return answerPart;
    }
}
