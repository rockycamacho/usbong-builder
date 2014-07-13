package usbong.android.builder.parsers;

import android.util.Log;
import com.activeandroid.query.Select;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import usbong.android.builder.enums.UsbongScreenType;
import usbong.android.builder.exceptions.ParserException;
import usbong.android.builder.models.Screen;
import usbong.android.builder.models.ScreenRelation;
import usbong.android.builder.utils.StringUtils;

/**
 * Created by Rocky Camacho on 7/11/2014.
 */
public class DecisionTransitionHandler implements ElementHandler<ScreenRelation> {

    public static final String END_STATE = "end-state";
    public static final String DEFAULT_CONDITION = "DEFAULT";
    private static final String TAG = DecisionTransitionHandler.class.getSimpleName();

    @Override
    public ScreenRelation handle(String qName, Attributes attributes) throws SAXException {
        String toAttribute = attributes.getValue("to");
        if (StringUtils.isEmpty(toAttribute)) {
            toAttribute = attributes.getValue("name");
        }
        String[] attrs = toAttribute.split("~");
        String screenType = attrs[0];
        int detailsIndex = attrs.length - 2;
        String details = attrs[detailsIndex].replaceAll("\\{", "\\<").replaceAll("\\}", "\\>");
        if (details.startsWith(END_STATE)) {
            return null;
        }
        Screen childScreen = new Select().from(Screen.class)
                .where("Details = ?", details)
                .executeSingle();

        if (childScreen == null) {
            Log.e(TAG, "childScreen == null: " + screenType + " " + toAttribute);
            throw new SAXException(new ParserException("unable to find `" + details + "` from database"));
        }
        ScreenRelation screenRelation = new ScreenRelation();
        screenRelation.child = childScreen;
        if (UsbongScreenType.TEXT_DISPLAY.getName().equals(screenType)) {
            screenRelation.condition = DEFAULT_CONDITION;
        } else if (UsbongScreenType.LINK.getName().equals(screenType) ||
                UsbongScreenType.DECISION.getName().equals(screenType)) {
            screenRelation.condition = attrs[attrs.length - 1];
        } else {
            Log.w(TAG, "unhandled screen type: " + screenType);
            throw new SAXException(new ParserException("unhandled " + qName + " screen type: " + screenType));
        }
        return screenRelation;
    }
}
