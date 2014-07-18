package usbong.android.builder.converters;

import android.util.Log;
import android.util.Xml;
import org.xmlpull.v1.XmlSerializer;
import usbong.android.builder.models.Screen;
import usbong.android.builder.models.ScreenRelation;
import usbong.android.builder.models.Utree;
import usbong.android.builder.utils.ResourceUtils;
import usbong.android.builder.utils.StringUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by Rocky Camacho on 7/14/2014.
 */
public class UtreeConverter {

    private static final String TAG = UtreeConverter.class.getSimpleName();
    public static final String FEATURE_INDENT_OUTPUT = "http://xmlpull.org/v1/doc/features.html#indent-output";
    public static final String ENCODING = "UTF-8";
    public static final String NAMESPACE = StringUtils.EMPTY;
    private XmlSerializer xmlSerializer;
    private ScreenConverterStrategy strategy;
    private Queue<Screen> pendingNodes;
    private Map<String, Screen> nodes;

    public UtreeConverter() {
        xmlSerializer = Xml.newSerializer();
        xmlSerializer.setFeature(FEATURE_INDENT_OUTPUT, true);
        strategy = new ScreenConverterStrategy();
        nodes = new HashMap<String, Screen>();
        pendingNodes = new LinkedList<Screen>();
    }

    public void convert(Utree tree, String outputFileLocation) {
        BufferedWriter fw = null;
        try {
            fw = new BufferedWriter(new FileWriter(new File(outputFileLocation)));
            xmlSerializer.setOutput(fw);
            xmlSerializer.startDocument(ENCODING, true);
            xmlSerializer.startTag(NAMESPACE, "process-definition");
            xmlSerializer.attribute(NAMESPACE, "name", tree.name);

            Screen startingScreen = Utree.getStartScreen(tree);
            if(startingScreen == null) {
                throw new IllegalStateException(".utree has not defined a start screen");
            }
            Log.d(TAG, "startingScreen details: " + startingScreen.details);
            createStartNode(startingScreen);
            pendingNodes.add(startingScreen);
            while(!pendingNodes.isEmpty()) {
                Screen screen = pendingNodes.remove();
                String name = strategy.getName(screen);
                if(!nodes.containsKey(name)) {
                    nodes.put(name, screen);
                    createTaskNode(screen);
                }
            }
            createExitNode();
            xmlSerializer.endTag(NAMESPACE, "process-definition");
            xmlSerializer.endDocument();
            fw.flush();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            ResourceUtils.close(fw);
        }
    }

    private void createTaskNode(Screen screen) throws IOException {
        xmlSerializer.startTag(NAMESPACE, "task-node");
        xmlSerializer.attribute(NAMESPACE, "name", strategy.getName(screen));
        List<ScreenRelation> screenRelations = ScreenRelation.getChildrenOf(screen.getId());
        if(screenRelations.isEmpty()) {
            xmlSerializer.startTag(NAMESPACE, "transition");
            xmlSerializer.attribute(NAMESPACE, "to", "end-state1");
            xmlSerializer.attribute(NAMESPACE, "name", "Any");
            xmlSerializer.endTag(NAMESPACE, "transition");
        }
        else {
            for(int i = 0; i < screenRelations.size(); i++) {
                ScreenRelation screenRelation = screenRelations.get(i);
                if(i == screenRelations.size() - 1) {
                    xmlSerializer.startTag(NAMESPACE, "transition");
                    xmlSerializer.attribute(NAMESPACE, "to", strategy.getTransition(screenRelation));
                    xmlSerializer.endTag(NAMESPACE, "transition");
                }
                else {
                    xmlSerializer.startTag(NAMESPACE, "task");
                    xmlSerializer.attribute(NAMESPACE, "name", strategy.getTransition(screenRelation));
                    xmlSerializer.endTag(NAMESPACE, "task");
                }
                String childScreenName = strategy.getName(screenRelation.child);
                if(!nodes.containsKey(childScreenName)) {
                    pendingNodes.add(screenRelation.child);
                }
            }
        }
        xmlSerializer.endTag(NAMESPACE, "task-node");
    }

    private void createStartNode(Screen screen) throws IOException {
        xmlSerializer.startTag(NAMESPACE, "start-state");
        xmlSerializer.attribute(NAMESPACE, "name", "start-state1");
        xmlSerializer.startTag(NAMESPACE, "transition");
        xmlSerializer.attribute(NAMESPACE, "to", strategy.getName(screen));
        xmlSerializer.endTag(NAMESPACE, "transition");
        xmlSerializer.endTag(NAMESPACE, "start-state");
    }

    private void createExitNode() throws IOException {
        xmlSerializer.startTag(NAMESPACE, "end-state");
        xmlSerializer.attribute(NAMESPACE, "name", "end-state1");
        xmlSerializer.endTag(NAMESPACE, "end-state");
    }

}
