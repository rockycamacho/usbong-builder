package usbong.android.builder.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoUtils;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Rocky Camacho on 6/27/2014.
 */
public class ScreenUtils {

    private static final String TAG = ScreenUtils.class.getSimpleName();

    private ScreenUtils() {}

    public static Bitmap loadBitmapFromView(View v) {
        v.setDrawingCacheEnabled(true);
        Bitmap b = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false);
        return b;
    }

    public static void saveScreenshot(File file, View screenContainer) {
        FileOutputStream out = null;
        Bitmap bmp = loadBitmapFromView(screenContainer);
        try {
            if(file.exists()) {
                file.delete();
            }
            out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();

            clearCache(screenContainer.getContext());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            ResourceUtils.free(bmp);
            ResourceUtils.close(out);
        }
    }

    private static void clearCache(Context context) {
        Picasso picasso = Picasso.with(context);
        PicassoUtils.clearCache(picasso);
    }
}
