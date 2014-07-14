package usbong.android.builder.parsers;

import android.media.Image;
import android.util.Log;
import com.google.gson.Gson;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import usbong.android.builder.enums.ImagePosition;
import usbong.android.builder.enums.UsbongBuilderScreenType;
import usbong.android.builder.enums.UsbongScreenType;
import usbong.android.builder.models.Screen;
import usbong.android.builder.models.ScreenDetails;
import usbong.android.builder.models.Utree;

import java.io.File;
import java.util.*;

/**
 * Created by Rocky Camacho on 7/5/2014.
 */
public class UtreeAndScreenXmlHandler extends DefaultHandler {
    private static final String TAG = UtreeAndScreenXmlHandler.class.getSimpleName();
    public static final String[] IMAGE_FILE_EXTENSIONS = new String[]{".jpg", ".jpeg", ".png"};
    private Utree utree;
    private Map<String, Screen> screenMap = new LinkedHashMap<String, Screen>();
    private List<Screen> screens = new ArrayList<Screen>();
    private Screen currentScreen;
    private Gson gson;
    private String outputFolderLocation;
    private String resFolder;

    public UtreeAndScreenXmlHandler() {
        gson = new Gson();
    }

    //TODO: need to refactor
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if("process-definition".equals(qName)) {
            utree = new Utree();
            utree.name = attributes.getValue("name");
        }
        else if("task-node".equals(qName)) {
            currentScreen = null;
            String nameAttribute = attributes.getValue("name");
            String[] attrs = nameAttribute.split("~");
            String screenType = attrs[0];
            if(UsbongScreenType.TEXT_DISPLAY.getName().equals(screenType)) {
                currentScreen = new Screen();
                String details = attrs[1].replaceAll("\\{", "\\<").replaceAll("\\}", "\\>");
                currentScreen.screenType = UsbongBuilderScreenType.TEXT.getName();
                currentScreen.name = details;
                currentScreen.details = details;
            }
            else if(UsbongScreenType.LINK.getName().equals(screenType)) {
                currentScreen = new Screen();
                String details = attrs[attrs.length - 1].replaceAll("\\{", "\\<").replaceAll("\\}", "\\>");
                String name = details;
                if(attrs.length > 2) {
                    name = attrs[1] + "~" + details;
                }
                currentScreen.screenType = UsbongBuilderScreenType.DECISION.getName();
                currentScreen.name = name;
                currentScreen.details = details;
            }
            else if(UsbongScreenType.DECISION.getName().equals(screenType)) {
                currentScreen = new Screen();
                String details = attrs[attrs.length - 1].replaceAll("\\{", "\\<").replaceAll("\\}", "\\>");
                String name = details;
                if(attrs.length > 2) {
                    name = attrs[1] + "~" + details;
                }
                currentScreen.screenType = UsbongBuilderScreenType.DECISION.getName();
                currentScreen.name = name;
                currentScreen.details = details;
            }
            else if(UsbongScreenType.IMAGE_DISPLAY.getName().equals(screenType)) {
                String name = attrs[attrs.length - 1].replaceAll("\\{", "\\<").replaceAll("\\}", "\\>");
                ScreenDetails screenDetails = new ScreenDetails();
                screenDetails.setImagePath(getImagePath(attrs[1], IMAGE_FILE_EXTENSIONS));
                currentScreen = new Screen();
                currentScreen.screenType = UsbongBuilderScreenType.IMAGE.getName();
                currentScreen.name = name;
                currentScreen.details = gson.toJson(screenDetails);
            }
            else if(UsbongScreenType.TEXT_IMAGE_DISPLAY.getName().equals(screenType)) {
                String details = attrs[attrs.length - 1].replaceAll("\\{", "\\<").replaceAll("\\}", "\\>");
                ScreenDetails screenDetails = new ScreenDetails();
                screenDetails.setText(details);
                screenDetails.setImagePosition(ImagePosition.BELOW_TEXT.getName());
                screenDetails.setImagePath(getImagePath(attrs[1], IMAGE_FILE_EXTENSIONS));
                currentScreen = new Screen();
                currentScreen.screenType = UsbongBuilderScreenType.TEXT_AND_IMAGE.getName();
                currentScreen.name = details;
                currentScreen.details = gson.toJson(screenDetails);
            }
            else if(UsbongScreenType.IMAGE_TEXT_DISPLAY.getName().equals(screenType)) {
                String details = attrs[attrs.length - 1].replaceAll("\\{", "\\<").replaceAll("\\}", "\\>");
                ScreenDetails screenDetails = new ScreenDetails();
                screenDetails.setText(details);
                screenDetails.setImagePosition(ImagePosition.ABOVE_TEXT.getName());
                screenDetails.setImagePath(getImagePath(attrs[1], IMAGE_FILE_EXTENSIONS));
                currentScreen = new Screen();
                currentScreen.screenType = UsbongBuilderScreenType.TEXT_AND_IMAGE.getName();
                currentScreen.name = details;
                currentScreen.details = gson.toJson(screenDetails);
            }
            if(currentScreen == null) {
                Log.w(TAG, "unhandled screenType: " + screenType);
                Log.w(TAG, "screen details: " + nameAttribute);
                return;
            }
            currentScreen.utree = utree;
            Log.d(TAG, "currentScreen.name: " + currentScreen.name);
            Log.d(TAG, "currentScreen.details: " + currentScreen.details);
            Log.d(TAG, "currentScreen.screenType: " + currentScreen.screenType);

            Screen screen = currentScreen;
            screenMap.put(nameAttribute, screen);
        }
    }

    private String getImagePath(String imageId, String... fileExtensions) {
        for(String fileExtension : fileExtensions) {
            File imageFile = new File(resFolder + File.separator + imageId + ".jpg");
            if(imageFile.exists()) {
                return imageFile.getAbsolutePath();
            }
        }
        return null;
    }

    private String getImagePath(File imageFile) {
        return imageFile.getAbsolutePath();
    }

    public Utree getUtree() {
        return utree;
    }

    public Map<String, Screen> getScreens() {
        return screenMap;
    }

    public void clearScreens() {
        screenMap.clear();
    }

    public void setOutputFolderLocation(String outputFolderLocation) {
        this.outputFolderLocation = outputFolderLocation;
        this.resFolder = outputFolderLocation + File.separator + "res";
    }
}
