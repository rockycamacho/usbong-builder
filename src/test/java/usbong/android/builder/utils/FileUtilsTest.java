package usbong.android.builder.utils;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Rocky Camacho on 7/7/2014.
 */
@Config(manifest = "../src/main/AndroidManifest.xml")
@RunWith(RobolectricTestRunner.class)
public class FileUtilsTest {

    @Before
    public void setUp() throws Exception {
        ShadowLog.stream = System.out;
    }

    @Test
     public void testUnzip() throws Exception {
        String zipFileLocation = "src/test/" + "champs.utree";
        String destination = new File("src/test").getAbsolutePath();
        System.out.println("destination: " + destination);
        String treeName = zipFileLocation.substring(zipFileLocation.lastIndexOf("/") + 1, zipFileLocation.lastIndexOf(".utree"));
        String xmlFilePath = destination + File.separator + treeName + File.separator + treeName + ".xml";
        System.out.println("xmlFilePath: " + xmlFilePath);
        FileUtils.unzip(zipFileLocation, destination);
    }

    @Test
    public void testZip() throws Exception {
        String contentLocation = new File("src/test/" + "champs2/").getAbsolutePath();
        FileUtils.zip("src/test/" + "champs2.zip", contentLocation);
    }

}
