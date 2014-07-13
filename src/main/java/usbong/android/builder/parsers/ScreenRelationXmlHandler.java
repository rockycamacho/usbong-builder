package usbong.android.builder.parsers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import usbong.android.builder.enums.UsbongScreenType;
import usbong.android.builder.models.Screen;
import usbong.android.builder.models.ScreenRelation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rocky Camacho on 7/5/2014.
 */
public class ScreenRelationXmlHandler extends DefaultHandler {

    private static final String TAG = ScreenRelationXmlHandler.class.getSimpleName();
    public static final String END_STATE = "end-state";
    public static final String DEFAULT_CONDITION = "DEFAULT";
    public static final TaskNodeHandler TASK_NODE_HANDLER = new TaskNodeHandler();
    private List<ScreenRelation> screenRelations = new ArrayList<ScreenRelation>();
    private Screen parentScreen = null;

    //TODO: refactor
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        if("task-node".equals(qName)) {
            parentScreen = TASK_NODE_HANDLER.handle(qName, attributes);
        }
        else if("task".equals(qName) ||
                "transition".equals(qName)) {
            if(parentScreen != null) {
                ScreenRelation screenRelation = null;
                if(UsbongScreenType.LINK.getName().equals(parentScreen.screenType) ||
                        UsbongScreenType.DECISION.getName().equals(parentScreen.screenType)) {
                    screenRelation = new DecisionTransitionHandler().handle(qName, attributes);
                }
                else {
                    screenRelation = new DefaultTransitionHandler().handle(qName, attributes);
                }
                if(screenRelation != null) {
                    screenRelation.parent = parentScreen;
                    screenRelations.add(screenRelation);
                }
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
