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
        ScreenRelation screenRelation = new ScreenRelation();
        if(hasAnswer(qName, attributes)) {
            String name = attributes.getValue("to");
            String condition = attributes.getValue("name");
            Screen childScreen = screenMap.get(name);
            screenRelation.child = childScreen;
            if("Yes".equals(attributes.getValue("name"))) {
                screenRelation.condition = "ANSWER~Correct";
            } else if ("No".equals(attributes.getValue("name"))) {
                screenRelation.condition = "ANSWER~Incorrect";
            }
        }
        else {
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

            screenRelation.child = childScreen;
            screenRelation.condition = "DECISION~" + condition;
        }
        return screenRelation;
    }

    private boolean hasAnswer(String qName, Attributes attributes) {
        return "transition".equals(qName) && attributes.getValue("name") != null && !"Any".equals(attributes.getValue("name"));
    }

    public void setScreenMap(Map<String, Screen> screenMap) {
        this.screenMap = screenMap;
    }
}
