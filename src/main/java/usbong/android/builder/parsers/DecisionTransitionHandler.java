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
public class DecisionTransitionHandler implements ElementHandler<ScreenRelation> {

    public static final String END_STATE = "end-state";
    public static final String DEFAULT_CONDITION = "DEFAULT";
    private static final String TAG = DecisionTransitionHandler.class.getSimpleName();
    private Gson gson;
    private Map<String, Screen> screenMap;

    public DecisionTransitionHandler() {
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
        String name = toAttribute.substring(0, toAttribute.lastIndexOf("~"));
        String condition = toAttribute.substring(toAttribute.lastIndexOf("~") + 1);
        if (name.startsWith(END_STATE)) {
            return null;
        }
        if (!screenMap.containsKey(name)) {
            Log.e(TAG, "childScreen == null: " + screenType + " " + name);
            throw new SAXException(new ParserException("unable to find `" + name + "` from screen map"));
        }
        Screen childScreen = screenMap.get(name);
        ScreenRelation screenRelation = new ScreenRelation();
        screenRelation.child = childScreen;
        screenRelation.condition = condition;
        return screenRelation;
    }

    public void setScreenMap(Map<String, Screen> screenMap) {
        this.screenMap = screenMap;
    }
}
