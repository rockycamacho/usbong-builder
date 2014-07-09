package usbong.android.builder.activities;

import android.app.Activity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

@Config(manifest = "src/main/AndroidManifest.xml")
@RunWith(RobolectricTestRunner.class)
public class DeckardActivityRobolectricTest {

    @Test
    public void testSomething() throws Exception {
        Activity activity = Robolectric.buildActivity(DeckardActivity.class).create().get();
        assertTrue(activity != null);
    }
}
