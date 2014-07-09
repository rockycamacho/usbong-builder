package usbong.android.builder;

import android.annotation.TargetApi;
import android.app.Application;
import android.os.Build;
import android.os.StrictMode;
import com.activeandroid.ActiveAndroid;

/**
 * Created by Rocky Camacho on 6/26/2014.
 */
public class UsbongBuilder extends Application {

    private static final boolean DEVELOPER_MODE = true;

    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);

        if (DEVELOPER_MODE) {
            //enableStrictMode();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void enableStrictMode() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()   // or .detectAll() for all detectable problems
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ActiveAndroid.dispose();
    }

}
