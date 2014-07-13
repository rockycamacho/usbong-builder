package usbong.android.builder.parsers;

import android.util.Log;
import com.activeandroid.query.Select;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import usbong.android.builder.exceptions.ParserException;
import usbong.android.builder.models.Screen;

import java.util.Map;

/**
 * Created by Rocky Camacho on 7/11/2014.
 */
public class TaskNodeHandler implements ElementHandler<Screen> {

    private static final String TAG = TaskNodeHandler.class.getSimpleName();
    private Map<String, Screen> screenMap;

    @Override
    public Screen handle(String qName, Attributes attributes) throws SAXException {
        String nameAttribute = attributes.getValue("name");
        String[] attrs = nameAttribute.split("~");
        String screenType = attrs[0];

        if(!screenMap.containsKey(nameAttribute)) {
            Log.e(TAG, "parentScreen == null: " + screenType);
            throw new SAXException(new ParserException("unable to find `" + nameAttribute + "` from screen map"));
        }
        return screenMap.get(nameAttribute);
    }

    public void setScreenMap(Map<String, Screen> screenMap) {
        this.screenMap = screenMap;
    }
}
