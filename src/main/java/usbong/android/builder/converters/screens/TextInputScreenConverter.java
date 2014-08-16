package usbong.android.builder.converters.screens;

import com.google.gson.Gson;
import usbong.android.builder.enums.UsbongScreenType;
import usbong.android.builder.models.Screen;
import usbong.android.builder.models.details.TextInputScreenDetails;
import usbong.android.builder.utils.JsonUtils;
import usbong.android.builder.utils.StringUtils;

/**
 * Created by Rocky Camacho on 8/10/2014.
 */
public class TextInputScreenConverter implements ScreenConverter {

    private static final String STORE_VARIABLE_FORMAT = "@%s=getInput()";

    @Override
    public String getName(Screen screen) {
        TextInputScreenDetails textInputScreenDetails = JsonUtils.fromJson(screen.details, TextInputScreenDetails.class);
        String screenType = UsbongScreenType.TEXT_FIELD.getName();
        String variablePart = StringUtils.EMPTY;
        if (textInputScreenDetails.isStoreVariable()) {
            variablePart = String.format(STORE_VARIABLE_FORMAT, textInputScreenDetails.getVariableName()) + SEPARATOR;
        }
        String content = StringUtils.toUsbongText(textInputScreenDetails.getText());
        if (textInputScreenDetails.isHasUnit()) {
            screenType = UsbongScreenType.TEXT_FIELD_WITH_UNIT.getName();
            return screenType + SEPARATOR + textInputScreenDetails.getUnit() + SEPARATOR + variablePart + content;
        }
        if (TextInputScreenDetails.ALPHA_NUMERIC.equals(textInputScreenDetails.getInputType())) {
            if (textInputScreenDetails.isMultiLine()) {
                screenType = UsbongScreenType.TEXT_AREA.getName();
                if(textInputScreenDetails.isHasAnswer()) {
                    screenType = UsbongScreenType.TEXT_AREA_WITH_ANSWER.getName();
                }
            } else {
                screenType = UsbongScreenType.TEXT_FIELD.getName();
                if(textInputScreenDetails.isHasAnswer()) {
                    screenType = UsbongScreenType.TEXT_FIELD_WITH_ANSWER.getName();
                }
            }
            String answerPart = getAnswerPart(textInputScreenDetails);
            content += answerPart;
        } else if (TextInputScreenDetails.NUMERIC.equals(textInputScreenDetails.getInputType())) {
            screenType = UsbongScreenType.TEXT_FIELD_NUMERICAL.getName();
        }
        return screenType + SEPARATOR + content;
    }

    private String getAnswerPart(TextInputScreenDetails textInputScreenDetails) {
        String answerPart = StringUtils.EMPTY;
        if(textInputScreenDetails.isHasAnswer() && textInputScreenDetails.getAnswers() != null && !textInputScreenDetails.getAnswers().isEmpty()) {
            if(textInputScreenDetails.getAnswers() != null) {
                StringBuilder sb = new StringBuilder();
                for (String answer : textInputScreenDetails.getAnswers()) {
                    if (sb.length() > 0) {
                        sb.append("||");
                    }
                    sb.append(answer);
                }
                answerPart = "?Answer=" + sb.toString();
            }
        }
        return answerPart;
    }
}
