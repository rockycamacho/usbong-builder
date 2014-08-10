package usbong.android.builder.utils;

import android.app.Activity;
import android.content.Intent;
import com.nononsenseapps.filepicker.FilePickerActivity;
//import com.nononsenseapps.filepicker.FilePickerActivity;

/**
 * Created by Rocky Camacho on 7/8/2014.
 */
public class IntentUtils {

    public static final int CHOOSE_FILE_REQUEST_CODE = 888;
    public static final int CHOOSE_FOLDER_REQUEST_CODE = 999;

    private IntentUtils() {
    }

    public static Intent getSelectFileIntent(Activity activity, String mimeType) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        return intent;
    }

    public static Intent getSelectFolderIntent(Activity activity) {
        Intent i = new Intent(activity, FilePickerActivity.class);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, true);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_DIR);
        return i;
    }

    public static Intent getSelectUtreeIntent(Activity activity, String mimeType) {
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
}
