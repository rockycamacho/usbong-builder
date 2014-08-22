package usbong.android.builder.parsers;

import android.util.Log;
import usbong.android.builder.enums.ImagePosition;
import usbong.android.builder.enums.UsbongBuilderScreenType;
import usbong.android.builder.enums.UsbongScreenType;
import usbong.android.builder.models.Screen;
import usbong.android.builder.models.details.*;
import usbong.android.builder.utils.JsonUtils;
import usbong.android.builder.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
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
    public static final String ANSWER_PREFIX = "?Answer=";

    public static Screen createFrom(String[] attrs, String resFolder) {
        Screen screen = null;
        String screenType = attrs[0];

        if (UsbongScreenType.TEXT_DISPLAY.getName().equals(screenType)) {
            screen = new Screen();
            String details = StringUtils.toUsbongBuilderText(attrs[1]);
            screen.screenType = UsbongBuilderScreenType.TEXT.getName();
            screen.name = details;
            screen.details = details;
        } else if (hasDecisionBranches(screenType)) {
            screen = new Screen();
            String details = StringUtils.toUsbongBuilderText(attrs[attrs.length - 1]);
            String name = details;
            if (attrs.length > 2) {
                name = attrs[1] + "~" + details;
            }
            screen.screenType = UsbongBuilderScreenType.DECISION.getName();
            screen.name = name;
            screen.details = details;
        } else if (hasImage(screenType)) {
            String name = StringUtils.toUsbongBuilderText(attrs[attrs.length - 1]);
            ImageScreenDetails imageScreenDetails = new ImageScreenDetails();
            imageScreenDetails.setImagePath(getImagePath(resFolder, attrs[1], IMAGE_FILE_EXTENSIONS));
            if (UsbongScreenType.CLICKABLE_IMAGE_DISPLAY.getName().equals(screenType)) {
                imageScreenDetails.setHasCaption(true);
                imageScreenDetails.setImageCaption(name);
            }
            screen = new Screen();
            screen.screenType = UsbongBuilderScreenType.IMAGE.getName();
            screen.name = name;
            screen.details = JsonUtils.toJson(imageScreenDetails);
        } else if (hasTextAndImage(screenType)) {
            String details = StringUtils.toUsbongBuilderText(attrs[attrs.length - 1]);
            ImageScreenDetails imageScreenDetails = new ImageScreenDetails();
            imageScreenDetails.setText(details);
            ImagePosition imagePosition = ImagePosition.BELOW_TEXT;
            if (UsbongScreenType.TEXT_IMAGE_DISPLAY.getName().equals(screenType) ||
                    UsbongScreenType.TEXT_CLICKABLE_IMAGE_DISPLAY.getName().equals(screenType)) {
                imagePosition = ImagePosition.ABOVE_TEXT;
            }
            imageScreenDetails.setImagePosition(imagePosition.getName());
            imageScreenDetails.setImagePath(getImagePath(resFolder, attrs[1], IMAGE_FILE_EXTENSIONS));
            if (UsbongScreenType.TEXT_CLICKABLE_IMAGE_DISPLAY.getName().equals(screenType) ||
                    UsbongScreenType.CLICKABLE_IMAGE_TEXT_DISPLAY.getName().equals(screenType)) {
                imageScreenDetails.setHasCaption(true);
                String imageCaption = StringUtils.toUsbongBuilderText(attrs[2]);
                imageScreenDetails.setImageCaption(imageCaption);
            }
            screen = new Screen();
            screen.screenType = UsbongBuilderScreenType.TEXT_AND_IMAGE.getName();
            screen.name = details;
            screen.details = JsonUtils.toJson(imageScreenDetails);
        } else if (isTextInput(screenType)) {
            String details = StringUtils.toUsbongBuilderText(attrs[attrs.length - 1]);
            TextInputScreenDetails textInputScreenDetails = new TextInputScreenDetails();
            textInputScreenDetails.setMultiLine(UsbongScreenType.TEXT_AREA.getName().equals(screenType));
            String inputType = TextInputScreenDetails.ALPHA_NUMERIC;

            if(details.contains(ANSWER_PREFIX)) {
                textInputScreenDetails.setHasAnswer(true);
                int indexOfAnswer = details.indexOf(ANSWER_PREFIX);
                String[] answers = details.substring(indexOfAnswer + ANSWER_PREFIX.length()).split("\\|\\|");
                textInputScreenDetails.setAnswers(Arrays.asList(answers));
                details = details.substring(0, indexOfAnswer);
            }

            if (UsbongScreenType.TEXT_FIELD_NUMERICAL.getName().equals(screenType) ||
                    UsbongScreenType.TEXT_FIELD_WITH_UNIT.getName().equals(screenType)) {
                inputType = TextInputScreenDetails.NUMERIC;
                if (UsbongScreenType.TEXT_FIELD_WITH_UNIT.getName().equals(screenType)) {
                    String unit = StringUtils.toUsbongBuilderText(attrs[1]);
                    textInputScreenDetails.setHasUnit(true);
                    textInputScreenDetails.setUnit(unit);
                }
            }
            for (String attr : attrs) {
                Matcher matcher = GET_INPUT_PATTERN.matcher(attr);
                if (matcher.matches()) {
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
        } else if (isSpecialInput(screenType)) {
            String details = StringUtils.EMPTY;
            if (!UsbongScreenType.VIDEO_FROM_FILE.getName().equals(screenType)) {
                details = StringUtils.toUsbongBuilderText(attrs[attrs.length - 1]);
            }
            SpecialInputScreenDetails specialInputScreenDetails = new SpecialInputScreenDetails();
            specialInputScreenDetails.setText(details);
            SpecialInputScreenDetails.InputType inputType = getInputType(screenType);
            specialInputScreenDetails.setInputType(inputType.getName());

            screen = new Screen();
            screen.screenType = UsbongBuilderScreenType.SPECIAL_INPUT.getName();
            screen.name = details;
            screen.details = JsonUtils.toJson(specialInputScreenDetails);
        } else if (isSend(screenType)) {
            String details = StringUtils.toUsbongBuilderText(attrs[attrs.length - 1]);
            SendScreenDetails sendScreenDetails = new SendScreenDetails();
            sendScreenDetails.setText(details);
            SendScreenDetails.Type type = SendScreenDetails.Type.from(screenType);
            sendScreenDetails.setType(type.getName());
            screen = new Screen();
            screen.screenType = UsbongBuilderScreenType.SEND.getName();
            screen.name = details;
            screen.details = JsonUtils.toJson(sendScreenDetails);
        } else if (isVideoInput(screenType)) {
            String details = StringUtils.toUsbongBuilderText(attrs[attrs.length - 1]);
            VideoScreenDetails videoScreenDetails = new VideoScreenDetails();
            videoScreenDetails.setText(StringUtils.EMPTY);
            if(UsbongScreenType.VIDEO_FROM_FILE_WITH_TEXT.getName().equals(screenType)) {
                videoScreenDetails.setText(details);
            }
            String video = attrs[1];
            videoScreenDetails.setVideo(video);
            screen = new Screen();
            screen.screenType = UsbongBuilderScreenType.VIDEO.getName();
            screen.name = details;
            screen.details = JsonUtils.toJson(videoScreenDetails);
        } else if (isMisc(screenType)) {
            String details = StringUtils.toUsbongBuilderText(attrs[attrs.length - 1]);
            MiscScreenDetails miscScreenDetails = new MiscScreenDetails();
            miscScreenDetails.setText(details);
            if(UsbongScreenType.SIMPLE_ENCRYPT.getName().equals(screenType)) {
                miscScreenDetails.setType(MiscScreenDetails.Type.SIMPLE_ENCRYPT.getName());
            }
            else if(UsbongScreenType.TIMESTAMP_DISPLAY.getName().equals(screenType)) {
                miscScreenDetails.setType(MiscScreenDetails.Type.TIMESTAMP.getName());
            }
            screen = new Screen();
            screen.screenType = UsbongBuilderScreenType.MISC.getName();
            screen.name = details;
            screen.details = JsonUtils.toJson(miscScreenDetails);
        } else if (isList(screenType)) {
            String details = StringUtils.toUsbongBuilderText(attrs[attrs.length - 1]);
            ListScreenDetails listScreenDetails = new ListScreenDetails();
            if(details.contains(ANSWER_PREFIX)) {
                listScreenDetails.setHasAnswer(true);
                int indexOfAnswer = details.indexOf(ANSWER_PREFIX);
                try {
                    int answer = Integer.parseInt(details.substring(indexOfAnswer + ANSWER_PREFIX.length()));
                    listScreenDetails.setAnswer(answer);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
                details = details.substring(0, indexOfAnswer);
            }
            listScreenDetails.setText(details);
            ListScreenDetails.ListType listType = ListScreenDetails.ListType.SINGLE_ANSWER;
            if(UsbongScreenType.CHECKLIST.getName().equals(screenType)) {
                listType = ListScreenDetails.ListType.MULTIPLE_ANSWERS;
            }
            listScreenDetails.setType(listType.getName());
            listScreenDetails.setItems(new ArrayList<String>());
            listScreenDetails.setAnswer(0);
            screen = new Screen();
            screen.screenType = UsbongBuilderScreenType.LIST.getName();
            screen.name = details;
            screen.details = JsonUtils.toJson(listScreenDetails);
        } else if(isClassification(attrs)) {
            String details = StringUtils.toUsbongBuilderText(attrs[attrs.length - 1]);
            ListScreenDetails listScreenDetails = new ListScreenDetails();
            listScreenDetails.setText(details);
            listScreenDetails.setType(ListScreenDetails.ListType.ANY_ANSWER.getName());
            listScreenDetails.setItems(new ArrayList<String>());
            listScreenDetails.setAnswer(0);
            screen = new Screen();
            screen.screenType = UsbongBuilderScreenType.LIST.getName();
            screen.name = details;
            screen.details = JsonUtils.toJson(listScreenDetails);
        } else {
            Log.w(TAG, "unhandled screenType: " + screenType);
            Log.w(TAG, "screen details: " + Arrays.toString(attrs));
        }
        return screen;
    }

    private static boolean isList(String screenType) {
        return UsbongScreenType.RADIO_BUTTONS.getName().equals(screenType) ||
                UsbongScreenType.RADIO_BUTTONS_WITH_ANSWER.getName().equals(screenType) ||
                UsbongScreenType.CHECKLIST.getName().equals(screenType);
    }

    private static boolean isClassification(String[] attrs) {
        return attrs.length == 1;
    }

    private static boolean isSend(String screenType) {
        return UsbongScreenType.SEND_TO_WEBSERVER.getName().equals(screenType) ||
                UsbongScreenType.SEND_TO_CLOUD_BASED_SERVICE.getName().equals(screenType);
    }

    private static boolean isMisc(String screenType) {
        return UsbongScreenType.TIMESTAMP_DISPLAY.getName().equals(screenType) ||
                UsbongScreenType.SIMPLE_ENCRYPT.getName().equals(screenType);
    }

    private static boolean isVideoInput(String screenType) {
        return UsbongScreenType.VIDEO_FROM_FILE.getName().equals(screenType) ||
                UsbongScreenType.VIDEO_FROM_FILE_WITH_TEXT.getName().equals(screenType);
    }

    private static boolean hasDecisionBranches(String screenType) {
        return UsbongScreenType.LINK.getName().equals(screenType) ||
                UsbongScreenType.DECISION.getName().equals(screenType);
    }

    private static boolean hasImage(String screenType) {
        return UsbongScreenType.IMAGE_DISPLAY.getName().equals(screenType) ||
                UsbongScreenType.CLICKABLE_IMAGE_DISPLAY.getName().equals(screenType);
    }

    private static boolean hasTextAndImage(String screenType) {
        return UsbongScreenType.TEXT_IMAGE_DISPLAY.getName().equals(screenType) ||
                UsbongScreenType.IMAGE_TEXT_DISPLAY.getName().equals(screenType) ||
                UsbongScreenType.TEXT_CLICKABLE_IMAGE_DISPLAY.getName().equals(screenType) ||
                UsbongScreenType.CLICKABLE_IMAGE_TEXT_DISPLAY.getName().equals(screenType);
    }

    private static boolean isTextInput(String screenType) {
        return UsbongScreenType.TEXT_AREA.getName().equals(screenType) ||
                UsbongScreenType.TEXT_FIELD.getName().equals(screenType) ||
                UsbongScreenType.TEXT_FIELD_NUMERICAL.getName().equals(screenType) ||
                UsbongScreenType.TEXT_FIELD_WITH_UNIT.getName().equals(screenType);
    }

    private static boolean isSpecialInput(String screenType) {
        return UsbongScreenType.AUDIO_RECORDER.getName().equals(screenType) ||
                UsbongScreenType.DATE.getName().equals(screenType) ||
                UsbongScreenType.PAINT.getName().equals(screenType) ||
                UsbongScreenType.PHOTO_CAPTURE.getName().equals(screenType) ||
                UsbongScreenType.QR_CODE_READER.getName().equals(screenType);
    }

    private static SpecialInputScreenDetails.InputType getInputType(String screenType) {
        SpecialInputScreenDetails.InputType inputType = SpecialInputScreenDetails.InputType.DATE;
        if (UsbongScreenType.AUDIO_RECORDER.getName().equals(screenType)) {
            inputType = SpecialInputScreenDetails.InputType.AUDIO;
        } else if (UsbongScreenType.DATE.getName().equals(screenType)) {
            inputType = SpecialInputScreenDetails.InputType.DATE;
        } else if (UsbongScreenType.PAINT.getName().equals(screenType)) {
            inputType = SpecialInputScreenDetails.InputType.DRAW;
        } else if (UsbongScreenType.PHOTO_CAPTURE.getName().equals(screenType)) {
            inputType = SpecialInputScreenDetails.InputType.CAMERA;
        } else if (UsbongScreenType.QR_CODE_READER.getName().equals(screenType)) {
            inputType = SpecialInputScreenDetails.InputType.QR_CODE;
        }
        return inputType;
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
