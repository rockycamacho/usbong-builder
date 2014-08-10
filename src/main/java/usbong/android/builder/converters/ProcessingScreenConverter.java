package usbong.android.builder.converters;

import com.google.gson.Gson;
import usbong.android.builder.models.Screen;
import usbong.android.builder.models.details.ProcessingScreenDetails;
import usbong.android.builder.utils.StringUtils;

/**
 * Created by Rocky Camacho on 8/10/2014.
 */
public class ProcessingScreenConverter implements ScreenConverter {

    private final Gson gson;

    public ProcessingScreenConverter() {
        gson = new Gson();
    }

    @Override
    public String getName(Screen screen) {
        ProcessingScreenDetails processingScreenDetails = gson.fromJson(screen.details, ProcessingScreenDetails.class);
        String screenType = processingScreenDetails.getProcessingType();
        String content = StringUtils.toUsbongText(processingScreenDetails.getText());
        return screenType + SEPARATOR + content;
    }
}
