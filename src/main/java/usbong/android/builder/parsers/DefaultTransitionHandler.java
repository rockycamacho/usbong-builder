package usbong.android.builder.parsers;

import android.util.Log;
import com.google.gson.Gson;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import usbong.android.builder.exceptions.ParserException;
import usbong.android.builder.models.Screen;
import usbong.android.builder.models.ScreenRelation;
import usbong.android.builder.utils.StringUtils;

import java.util.Map;

/**
 * Created by Rocky Camacho on 7/11/2014.
 */
public class DefaultTransitionHandler implements ElementHandler<ScreenRelation> {

    public static final String END_STATE = "end-state";
    public static final String DEFAULT_CONDITION = "DEFAULT";
    private static final String TAG = DefaultTransitionHandler.class.getSimpleName();
    private Gson gson;
    private Map<String, Screen> screenMap;

    public DefaultTransitionHandler() {
        gson = new Gson();
    }

    @Override
    public ScreenRelation handle(String qName, Attributes attributes) throws SAXException {
        String toAttribute = attributes.getValue("to");
        if (StringUtils.isEmpty(toAttribute)) {
            toAttribute = attributes.getValue("name");
        }
        String[] attrs = toAttribute.split("~");
        String screenType = attrs[0];
        if (toAttribute.startsWith(END_STATE)) {
            return null;
        }
        if (!screenMap.containsKey(toAttribute)) {
            Log.e(TAG, "childScreen == null: " + screenType + " " + toAttribute);
            throw new SAXException(new ParserException("unable to find `" + toAttribute + "` from screen map"));
        }
        Screen childScreen = screenMap.get(toAttribute);
        ScreenRelation screenRelation = new ScreenRelation();
        screenRelation.child = childScreen;
        screenRelation.condition = DEFAULT_CONDITION;
        return screenRelation;
    }

    public void setScreenMap(Map<String, Screen> screenMap) {
        this.screenMap = screenMap;
    }
}
