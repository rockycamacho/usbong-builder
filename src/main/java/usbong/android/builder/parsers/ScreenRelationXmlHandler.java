package usbong.android.builder.parsers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import usbong.android.builder.enums.UsbongBuilderScreenType;
import usbong.android.builder.enums.UsbongScreenType;
import usbong.android.builder.models.Screen;
import usbong.android.builder.models.ScreenRelation;
import usbong.android.builder.models.details.ListScreenDetails;
import usbong.android.builder.utils.JsonUtils;
import usbong.android.builder.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Rocky Camacho on 7/5/2014.
 */
public class ScreenRelationXmlHandler extends DefaultHandler {

    private static final String TAG = ScreenRelationXmlHandler.class.getSimpleName();
    public static final TaskNodeHandler TASK_NODE_HANDLER = new TaskNodeHandler();
    public static final DecisionTransitionHandler DECISION_TRANSITION_HANDLER = new DecisionTransitionHandler();
    public static final DefaultTransitionHandler DEFAULT_TRANSITION_HANDLER = new DefaultTransitionHandler();
    public static final String TASK_NODE = "task-node";
    public static final String TASK = "task";
    public static final String TRANSITION = "transition";
    private List<ScreenRelation> screenRelations = new ArrayList<ScreenRelation>();
    private Screen parentScreen = null;
    private Map<String, Screen> screenMap;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        if (TASK_NODE.equals(qName)) {
            parentScreen = TASK_NODE_HANDLER.handle(qName, attributes);
        } else if (TASK.equals(qName) ||
                TRANSITION.equals(qName)) {
            if (parentScreen != null) {
                if (UsbongBuilderScreenType.LIST.getName().equals(parentScreen.screenType) && TASK.equals(qName)) {
                    ListScreenDetails listScreenDetails = JsonUtils.fromJson(parentScreen.details, ListScreenDetails.class);
                    String listItem = attributes.getValue("to");
                    if (StringUtils.isEmpty(listItem)) {
                        listItem = attributes.getValue("name");
                    }
                    listScreenDetails.getItems().add(listItem);
                    parentScreen.save();
                }
                else {
                    ScreenRelation screenRelation = null;
                    if (UsbongBuilderScreenType.DECISION.getName().equals(parentScreen.screenType)) {
                        screenRelation = DECISION_TRANSITION_HANDLER.handle(qName, attributes);
                    } else {
                        screenRelation = DEFAULT_TRANSITION_HANDLER.handle(qName, attributes);
                    }
                    if (screenRelation != null) {
                        screenRelation.parent = parentScreen;
                        screenRelations.add(screenRelation);
                    }
                }
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (TASK_NODE.equals(qName)) {
            parentScreen = null;
        }
    }

    public List<ScreenRelation> getScreenRelations() {
        return screenRelations;
    }

    public void setScreenMap(Map<String, Screen> screenMap) {
        this.screenMap = screenMap;
        TASK_NODE_HANDLER.setScreenMap(screenMap);
        DECISION_TRANSITION_HANDLER.setScreenMap(screenMap);
        DEFAULT_TRANSITION_HANDLER.setScreenMap(screenMap);
    }
}
