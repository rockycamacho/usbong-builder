package usbong.android.builder.parsers;

import android.util.Log;
import com.activeandroid.query.Select;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import usbong.android.builder.exceptions.ParserException;
import usbong.android.builder.models.Screen;

/**
 * Created by Rocky Camacho on 7/11/2014.
 */
public class TaskNodeHandler implements ElementHandler<Screen> {

    private static final String TAG = TaskNodeHandler.class.getSimpleName();

    @Override
    public Screen handle(String qName, Attributes attributes) throws SAXException {
        String nameAttribute = attributes.getValue("name");
        String[] attrs = nameAttribute.split("~");
        String screenType = attrs[0];

        //TODO: split this up some more
        if("textDisplay".equals(screenType) ||
                "link".equals(screenType) ||
                "decision".equals(screenType)) {
            String details = attrs[attrs.length-1].replaceAll("\\{", "\\<").replaceAll("\\}", "\\>");;
            Screen parentScreen = new Select().from(Screen.class)
                    .where("Details = ?", details)
                    .executeSingle();
            if(parentScreen == null) {
                Log.e(TAG, "parentScreen == null: " + screenType);
                throw new SAXException(new ParserException("unable to find `" + details + "` from database"));
            }
            return parentScreen;
        }
        else {
            Log.w(TAG, "unhandled task-node screen type: " + screenType);
            throw new SAXException(new ParserException("unhandled task-node screen type: " + screenType));
        }
    }
}
