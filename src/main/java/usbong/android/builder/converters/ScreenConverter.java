package usbong.android.builder.converters;

import usbong.android.builder.models.Screen;

/**
 * Created by Rocky Camacho on 7/14/2014.
 */
public interface ScreenConverter {

    final static String SEPARATOR = "~";

    String getName(Screen screen);

}
