package usbong.android.builder.parsers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import usbong.android.builder.models.Screen;

/**
 * Created by Rocky Camacho on 7/11/2014.
 */
public interface ElementHandler<T> {

    T handle(String qName, Attributes attributes) throws SAXException;

}
