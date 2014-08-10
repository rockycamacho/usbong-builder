package usbong.android.builder.converters;

import android.util.Log;
import android.util.Xml;
import org.xmlpull.v1.XmlSerializer;
import usbong.android.builder.enums.UsbongBuilderScreenType;
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
    public static final String NEWLINE = "\n";
    public static final String TAB = "   ";
    private ScreenConverterStrategy strategy;
    private Queue<Screen> pendingNodes;
    private Map<String, Screen> nodes;
    private BufferedWriter fw;

    public UtreeConverter() {
        strategy = new ScreenConverterStrategy();
        nodes = new HashMap<String, Screen>();
        pendingNodes = new LinkedList<Screen>();
    }

    public void convert(Utree tree, String outputFileLocation) {
        fw = null;
        try {
            fw = new BufferedWriter(new FileWriter(new File(outputFileLocation)));
            fw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            fw.write(NEWLINE);
            fw.write(NEWLINE);
            fw.write("<process-definition xmlns=\"\" name=\"" + tree.name + "\">");
            fw.write(NEWLINE);

            Screen startingScreen = Utree.getStartScreen(tree);
            if (startingScreen == null) {
                throw new IllegalStateException(".utree has not defined a start screen");
            }
            Log.d(TAG, "startingScreen details: " + startingScreen.details);
            createStartNode(startingScreen);
            pendingNodes.add(startingScreen);
            while (!pendingNodes.isEmpty()) {
                Screen screen = pendingNodes.remove();
                String name = strategy.getName(screen);
                if (!nodes.containsKey(name)) {
                    nodes.put(name, screen);
                    createTaskNode(screen);
                }
            }
            createExitNode();
            fw.write("</process-definition>");
            fw.flush();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            ResourceUtils.close(fw);
        }
    }

    private void createTaskNode(Screen screen) throws IOException {
        fw.write(TAB);
        fw.write("<task-node name=\"" + strategy.getName(screen) + "\">");
        fw.write(NEWLINE);
        List<ScreenRelation> screenRelations = ScreenRelation.getChildrenOf(screen.getId());
        if (screenRelations.isEmpty()) {
            fw.write(TAB);
            fw.write(TAB);
            fw.write("<transition to=\"end-state1\" name=\"Any\"></transition>");
            fw.write(NEWLINE);
        } else {
            for (int i = 0; i < screenRelations.size(); i++) {
                ScreenRelation screenRelation = screenRelations.get(i);
                fw.write(TAB);
                fw.write(TAB);
                String transitionTo = strategy.getTransition(screenRelation);
                if (i == screenRelations.size() - 1) {
                    if(UsbongBuilderScreenType.DECISION.getName().equals(screen.screenType)) {
                        fw.write("<task name=\"" + transitionTo + "\"></task>");
                    }
                    fw.write("<transition to=\"" + transitionTo + "\" name=\"Any\"></transition>");
                } else {
                    fw.write("<task name=\"" + transitionTo + "\"></task>");
                }
                fw.write(NEWLINE);
                pendingNodes.add(screenRelation.child);
            }
        }
        fw.write(TAB);
        fw.write("</task-node>");
        fw.write(NEWLINE);
        fw.write(NEWLINE);
    }

    private void createStartNode(Screen screen) throws IOException {
        fw.write(TAB);
        fw.write("<start-state name=\"start-state1\">");
        fw.write(NEWLINE);
        fw.write(TAB);
        fw.write(TAB);
        fw.write("<transition to=\"" + strategy.getName(screen) + "\"></transition>");
        fw.write(NEWLINE);
        fw.write(TAB);
        fw.write("</start-state>");
        fw.write(NEWLINE);
        fw.write(NEWLINE);
    }

    private void createExitNode() throws IOException {
        fw.write(TAB);
        fw.write("<end-state name=\"end-state1\"></end-state>");
        fw.write(NEWLINE);
    }

}
