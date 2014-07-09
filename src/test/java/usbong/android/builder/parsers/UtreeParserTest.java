package usbong.android.builder.parsers;

import android.app.Application;
import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;
import usbong.android.builder.UsbongBuilder;
import usbong.android.builder.models.Utree;

import java.io.File;
import java.io.FileInputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Rocky Camacho on 7/4/2014.
 */
@Config(manifest = "../src/main/AndroidManifest.xml")
@RunWith(RobolectricTestRunner.class)
public class UtreeParserTest {

    @Before
    public void setUp() throws Exception {
        ShadowLog.stream = System.out;
    }

    @Test
    public void testParser() throws Exception {
        File file = new File("src/test/" + "the_orange_problem.xml");
        System.out.println(file.getAbsoluteFile());
        assertTrue(file.exists());


        FileInputStream fis = new FileInputStream(file);
        UtreeParser parser = UtreeParser.getInstance();
        System.out.println(parser == null);
        parser.parseModels(fis);

        Utree utree = parser.getUtree();
        assertEquals("the_orange_problem", utree.name);
    }

}
