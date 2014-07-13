package usbong.android.builder.parsers;

import android.util.Log;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import usbong.android.builder.models.Screen;
import usbong.android.builder.models.ScreenRelation;
import usbong.android.builder.models.Utree;
import usbong.android.builder.utils.ResourceUtils;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Rocky Camacho on 7/4/2014.
 */
public class UtreeParser {

    private static final String TAG = Utree.class.getSimpleName();
    private static UtreeParser instance;
    private UtreeAndScreenXmlHandler utreeXmlHandler;
    private XMLReader utreeAndScreenXmlReader;
    private ScreenRelationXmlHandler screenRelationXmlHandler;
    private XMLReader screenRelationXmlReader;

    public static UtreeParser getInstance() {
        if (instance == null) {
            instance = new UtreeParser();
        }
        return instance;
    }

    private UtreeParser() {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            utreeAndScreenXmlReader = factory.newSAXParser().getXMLReader();
            utreeXmlHandler = new UtreeAndScreenXmlHandler();
            utreeAndScreenXmlReader.setContentHandler(utreeXmlHandler);

            screenRelationXmlReader = factory.newSAXParser().getXMLReader();
            screenRelationXmlHandler = new ScreenRelationXmlHandler();
            screenRelationXmlReader.setContentHandler(screenRelationXmlHandler);
        } catch (SAXException e) {
            Log.d(TAG, e.getMessage(), e);
        } catch (ParserConfigurationException e) {
            Log.d(TAG, e.getMessage(), e);
        }
    }

    public void parseModels(InputStream inputStream, String utreeOutputFolder) {
        try {
            utreeXmlHandler.setOutputFolderLocation(utreeOutputFolder);
            utreeXmlHandler.clearScreens();
            utreeAndScreenXmlReader.parse(new InputSource(inputStream));
        } catch (IOException e) {
            Log.d(TAG, e.getMessage(), e);
        } catch (SAXException e) {
            Log.d(TAG, e.getMessage(), e);
        }
    }

    public void parseRelations(InputStream inputStream) {
        try {
            screenRelationXmlHandler.setScreenMap(utreeXmlHandler.getScreens());
            screenRelationXmlReader.parse(new InputSource(inputStream));
        } catch (IOException e) {
            Log.d(TAG, e.getMessage(), e);
        } catch (SAXException e) {
            Log.d(TAG, e.getMessage(), e);
        }
    }

    public Utree getUtree() {
        if(utreeXmlHandler == null) {
            return null;
        }
        return utreeXmlHandler.getUtree();
    }

    public Map<String, Screen> getScreens() {
        if(utreeXmlHandler == null) {
            return new HashMap<String, Screen>();
        }
        return utreeXmlHandler.getScreens();
    }

    public List<ScreenRelation> getScreenRelations() {
        if(screenRelationXmlHandler == null) {
            return new ArrayList<ScreenRelation>();
        }
        return screenRelationXmlHandler.getScreenRelations();
    }

    public void parseAndSave(String xmlPath, String utreeOutputFolder) {
        parseAndSaveModels(xmlPath, utreeOutputFolder);
        parseAndSaveRelations(xmlPath);
    }

    private void parseAndSaveRelations(String xmlPath) {
        FileInputStream relationInputStream = null;
        try {
            relationInputStream = new FileInputStream(new File(xmlPath));
            parseRelations(relationInputStream);
            for (ScreenRelation screenRelations : getScreenRelations()) {
                screenRelations.save();
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            ResourceUtils.close(relationInputStream);
        }
    }

    private void parseAndSaveModels(String xmlPath, String utreeOutputFolder) {
        FileInputStream modelInputStream = null;
        try {
            modelInputStream = new FileInputStream(new File(xmlPath));
            parseModels(modelInputStream, utreeOutputFolder);
            Utree utree = getUtree();
            utree.save();
            for (Screen screen : getScreens().values()) {
                screen.save();
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            ResourceUtils.close(modelInputStream);
        }
    }
}
