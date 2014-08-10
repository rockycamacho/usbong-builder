package usbong.android.builder.parsers;

import android.util.Log;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import usbong.android.builder.models.Screen;
import usbong.android.builder.models.Utree;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Rocky Camacho on 7/5/2014.
 */
public class UtreeAndScreenXmlHandler extends DefaultHandler {
    private static final String TAG = UtreeAndScreenXmlHandler.class.getSimpleName();
    private Utree utree;
    private Map<String, Screen> screenMap = new LinkedHashMap<String, Screen>();
    private Screen currentScreen;
    private String outputFolderLocation;
    private String resFolder;

    //TODO: need to refactor
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if ("process-definition".equals(qName)) {
            utree = new Utree();
            utree.name = attributes.getValue("name");
        } else if ("task-node".equals(qName)) {
            String nameAttribute = attributes.getValue("name");
            String[] attrs = nameAttribute.split("~");
            currentScreen = ScreenFactory.createFrom(attrs, resFolder);
            if (currentScreen == null) {
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
