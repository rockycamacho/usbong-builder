package usbong.android.builder.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.nononsenseapps.filepicker.FilePickerActivity;
//import com.nononsenseapps.filepicker.FilePickerActivity;

/**
 * Created by Rocky Camacho on 7/8/2014.
 */
public class IntentUtils {

    public static final String SAMSUNG_MY_FILES_INTENT_ACTION = "com.sec.android.app.myfiles.PICK_DATA";
    public static final int CHOOSE_FILE_REQUESTCODE = 888;

    private IntentUtils() {}

    public static Intent getSelectFileIntent(Activity activity, String mimeType) {
        // This always works
        Intent i = new Intent(activity, FilePickerActivity.class);
        // This works if you defined the intent filter
        // Intent i = new Intent(Intent.ACTION_GET_CONTENT);

        // Set these depending on your use case. These are the defaults.
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
        return i;
    }

    public static Intent getSelectDirectorIntent(Activity activity, String mimeType) {
        // This always works
        Intent i = new Intent(activity, FilePickerActivity.class);
        // This works if you defined the intent filter
        // Intent i = new Intent(Intent.ACTION_GET_CONTENT);

        // Set these depending on your use case. These are the defaults.
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, true);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_DIR);
        return i;
    }

}
