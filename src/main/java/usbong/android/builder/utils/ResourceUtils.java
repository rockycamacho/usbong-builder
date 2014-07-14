package usbong.android.builder.utils;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.channels.FileChannel;

/**
 * Created by Rocky Camacho on 6/29/2014.
 */
public class ResourceUtils {

    private static final String TAG = ResourceUtils.class.getSimpleName();

    public static void close(OutputStream out) {
        try{
            if(out != null) {
                out.close();
            }
        } catch(Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public static void free(Bitmap bmp) {
        if(bmp != null && !bmp.isRecycled()) {
            bmp.recycle();
        }
    }

    public static void close(InputStream in) {
        try{
            if(in != null) {
                in.close();
            }
        } catch(Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public static void close(FileChannel channel) {
        try{
            if(channel != null) {
                channel.close();
            }
        } catch(Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public static void close(Writer writer) {
        try{
            if(writer != null) {
                writer.close();
            }
        } catch(Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }
}
