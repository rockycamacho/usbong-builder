package usbong.android.builder.parsers;

import android.util.Log;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import usbong.android.builder.enums.ScreenType;
import usbong.android.builder.models.Screen;
import usbong.android.builder.models.Utree;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rocky Camacho on 7/5/2014.
 */
public class UtreeAndScreenXmlHandler extends DefaultHandler {
    private static final String TAG = UtreeAndScreenXmlHandler.class.getSimpleName();
    private Utree utree;
    private List<Screen> screens = new ArrayList<Screen>();
    private Screen currentScreen;

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
            if("textDisplay".equals(screenType)) {
                currentScreen = new Screen();
                String details = attrs[1].replaceAll("\\{", "\\<").replaceAll("\\}", "\\>");
                currentScreen.screenType = ScreenType.TEXT_DISPLAY.getName();
                currentScreen.name = details;
                currentScreen.details = details;
            }
            else if("link".equals(screenType)) {
                currentScreen = new Screen();
                String details = attrs[attrs.length - 1].replaceAll("\\{", "\\<").replaceAll("\\}", "\\>");
                String name = details;
                if(attrs.length > 2) {
                    name = attrs[1] + "~" + details;
                }
                currentScreen.screenType = ScreenType.TEXT_DISPLAY.getName();
                currentScreen.name = details;
                currentScreen.details = details;
            }
            else if("decision".equals(screenType)) {
                currentScreen = new Screen();
                String details = attrs[1].replaceAll("\\{", "\\<").replaceAll("\\}", "\\>");
                currentScreen.screenType = ScreenType.TEXT_DISPLAY.getName();
                currentScreen.name = details;
                currentScreen.details = details;
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

            screens.add(currentScreen);
        }
    }

    public Utree getUtree() {
        return utree;
    }

    public List<Screen> getScreens() {
        return screens;
    }
}
