package usbong.android.builder.parsers;

import android.util.Log;
import com.activeandroid.query.Select;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import usbong.android.builder.enums.ScreenType;
import usbong.android.builder.exceptions.ParserException;
import usbong.android.builder.models.Screen;
import usbong.android.builder.models.ScreenRelation;
import usbong.android.builder.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rocky Camacho on 7/5/2014.
 */
public class ScreenRelationXmlHandler extends DefaultHandler {

    private static final String TAG = ScreenRelationXmlHandler.class.getSimpleName();
    public static final String END_STATE = "end-state";
    public static final String DEFAULT_CONDITION = "DEFAULT";
    private List<ScreenRelation> screenRelations = new ArrayList<ScreenRelation>();
    private Screen parentScreen = null;

    //TODO: refactor
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if("task-node".equals(qName)) {
            String nameAttribute = attributes.getValue("name");
            String[] attrs = nameAttribute.split("~");
            String screenType = attrs[0];

            if("textDisplay".equals(screenType) ||
                    "link".equals(screenType) ||
                    "decision".equals(screenType)) {
                String details = attrs[1].replaceAll("\\{", "\\<").replaceAll("\\}", "\\>");
                parentScreen = new Select().from(Screen.class)
                        .where("Details = ?", details)
                        .executeSingle();
                if(parentScreen == null) {
                    throw new SAXException(new ParserException("unable to find `" + details + "` from database"));
                }
            }
            //TODO: handle other types
        }
        else if("task".equals(qName) ||
                "transition".equals(qName)) {
            if(parentScreen != null) {
                String toAttribute = attributes.getValue("to");
                if(StringUtils.isEmpty(toAttribute)) {
                    toAttribute = attributes.getValue("name");
                }
                String[] attrs = toAttribute.split("~");
                String screenType = attrs[0];
                int detailsIndex = attrs.length - 1;
                if("link".equals(screenType)) {
                    detailsIndex = attrs.length - 2;
                }
                String details = attrs[detailsIndex].replaceAll("\\{", "\\<").replaceAll("\\}", "\\>");
                if(details.startsWith(END_STATE)) {
                    return;
                }
                Screen childScreen = new Select().from(Screen.class)
                        .where("Details = ?", details)
                        .executeSingle();

                if(childScreen == null) {
                    throw new SAXException(new ParserException("unable to find `" + details + "` from database"));
                }
                ScreenRelation screenRelation = new ScreenRelation();
                screenRelation.parent = parentScreen;
                screenRelation.child = childScreen;
                if(ScreenType.TEXT_DISPLAY.getName().equals(screenType)) {
                    screenRelation.condition = DEFAULT_CONDITION;
                }
                else if(ScreenType.LINK.getName().equals(screenType) ||
                        ScreenType.DECISION.getName().equals(screenType)){
                    screenRelation.condition = attrs[attrs.length - 1];
                }
                else {
                    Log.w(TAG, "unhandled screen type: " + screenType);
                    return;
                }
                screenRelations.add(screenRelation);
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if("task-node".equals(qName)) {
            parentScreen = null;
        }
    }

    public List<ScreenRelation> getScreenRelations() {
        return screenRelations;
    }
}
