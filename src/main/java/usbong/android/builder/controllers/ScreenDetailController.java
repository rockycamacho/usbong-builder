package usbong.android.builder.controllers;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import usbong.android.builder.events.OnScreenDetailsSave;
import usbong.android.builder.models.Screen;
import usbong.android.builder.models.ScreenRelation;
import usbong.android.builder.utils.FileUtils;
import usbong.android.builder.utils.ResourceUtils;
import usbong.android.builder.utils.ScreenUtils;

import java.io.File;
import java.util.List;

/**
 * Created by Rocky Camacho on 7/2/2014.
 */
public class ScreenDetailController implements Controller {

    private static final String TAG = ScreenDetailController.class.getSimpleName();
    private static final String[] IMAGE_EXTENSIONS = new String[]{".jpg", ".png", ".jpeg"};
    private static final String[] VIDEO_EXTENSIONS = new String[]{".3gp", ".mp4", ".ts", ".webm", ".mkv"};

    public void deleteAllChildScreens(final long screenId, Observer<Integer> observer) {
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                int deletedItems = ScreenRelation.deleteAll(screenId);
                subscriber.onNext(deletedItems);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void loadScreen(final long screenId, Observer<Screen> observer) {
        Observable.create(new Observable.OnSubscribe<Screen>() {
            @Override
            public void call(Subscriber<? super Screen> subscriber) {
                Screen screen = new Select().from(Screen.class)
                        .where(Screen._ID + " = ?", screenId)
                        .executeSingle();
                if (screen == null) {
                    throw new IllegalArgumentException("invalid screen id: " + screenId);
                }
                subscriber.onNext(screen);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void add(final List<ScreenRelation> screenRelations, Observer<Object> observer) {
        Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                ActiveAndroid.beginTransaction();
                try {
                    for (int i = 0; i < screenRelations.size(); i++) {
                        ScreenRelation screenRelation = screenRelations.get(i);
                        screenRelation.save();
                    }
                    ActiveAndroid.setTransactionSuccessful();
                } finally {
                    ActiveAndroid.endTransaction();
                }
                subscriber.onNext(null);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void loadParentScreens(final long screenId, Observer<List<ScreenRelation>> observer) {
        Observable.create(new Observable.OnSubscribe<List<ScreenRelation>>() {
            @Override
            public void call(Subscriber<? super List<ScreenRelation>> subscriber) {
                List<ScreenRelation> screenRelations = ScreenRelation.getParentsOf(screenId);
                subscriber.onNext(screenRelations);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void loadChildrenScreens(final long screenId, Observer<List<ScreenRelation>> observer) {
        Observable.create(new Observable.OnSubscribe<List<ScreenRelation>>() {
            @Override
            public void call(Subscriber<? super List<ScreenRelation>> subscriber) {
                List<ScreenRelation> screenRelations = ScreenRelation.getChildrenOf(screenId);
                subscriber.onNext(screenRelations);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void addRelation(final Screen parentScreen, final Screen childScreen, final String condition, Observer<ScreenRelation> observer) {
        Observable.create(new Observable.OnSubscribe<ScreenRelation>() {
            @Override
            public void call(Subscriber<? super ScreenRelation> subscriber) {
                ScreenRelation screenRelation = new ScreenRelation();
                screenRelation.parent = parentScreen;
                screenRelation.child = childScreen;
                screenRelation.condition = condition;
                screenRelation.save();
                subscriber.onNext(screenRelation);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void addOrUpdateRelation(final Screen parentScreen, final Screen childScreen, final String condition, Observer<ScreenRelation> observer) {
        Observable.create(new Observable.OnSubscribe<ScreenRelation>() {
            @Override
            public void call(Subscriber<? super ScreenRelation> subscriber) {
                ScreenRelation existingRelationWithSameCondition = new Select().from(ScreenRelation.class)
                        .where("parent = ? and condition = ?", parentScreen.getId(), condition)
                        .executeSingle();
                if(existingRelationWithSameCondition != null) {
                    existingRelationWithSameCondition.child = childScreen;
                    existingRelationWithSameCondition.save();
                    subscriber.onNext(existingRelationWithSameCondition);
                }
                else {
                    ScreenRelation screenRelation = new ScreenRelation();
                    screenRelation.parent = parentScreen;
                    screenRelation.child = childScreen;
                    screenRelation.condition = condition;
                    screenRelation.save();
                    subscriber.onNext(screenRelation);
                }
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void removeRelation(final Screen parentScreen, final Screen childScreen, Observer<Integer> observer) {
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                int count = new Select().from(ScreenRelation.class)
                        .where("parent = ? and child = ?", parentScreen.getId(), childScreen.getId())
                        .count();
                if(count > 0) {
                    new Delete().from(ScreenRelation.class)
                            .where("parent = ? and child = ?", parentScreen.getId(), childScreen.getId())
                            .execute();
                }
                subscriber.onNext(count);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void saveScreen(final Screen screen, final OnScreenDetailsSave event, final View screenContainer, Observer<Screen> observer) {
        Observable.create(new Observable.OnSubscribe<Screen>() {
            @Override
            public void call(Subscriber<? super Screen> subscriber) {
                Log.d(TAG, "saving...");
                if (screen.isStart == 1) {
                    Log.d(TAG, "screen.isStart == 1");
                    new Update(Screen.class).set("IsStart = ?", 0)
                            .where("Utree = ?", screen.utree.getId())
                            .execute();
                    screen.isStart = 1;
                    screen.save();
                } else if (Screen.getScreens(screen.utree.getId()).size() == 0) {
                    Log.d(TAG, "Screen.getScreens(screen.utree.getId()).size() == 0");
                    screen.isStart = 1;
                    screen.save();
                } else {
                    //WTF?!? need to re-set the values of screen name and details if isStart == 0
                    screen.name = event.getName();
                    screen.details = event.getContent();
                    Log.d(TAG, "screen.save() " + screen.name + " " + screen.details);
                    screen.save();
                }

                Log.d(TAG, "saved");
                File screenshotFile = screenContainer.getContext().getFileStreamPath(screen.getScreenshotPath());
                ScreenUtils.saveScreenshot(screenshotFile, screenContainer);
                subscriber.onNext(screen);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void uploadImage(final Context context, final Uri uri, final String outputFolderLocation, Observer<File> observer) {
        Observable.create(new Observable.OnSubscribe<File>() {
            @Override
            public void call(Subscriber<? super File> subscriber) {
                String fileLocation = getPath(context, uri);
                Log.d(TAG, "fileLocation: " + fileLocation);
                if (!isValidImage(fileLocation)) {
                    throw new IllegalArgumentException("Not a valid image file. Please select a jpg or png file");
                }
                FileUtils.mkdir(outputFolderLocation);
                File sourceFile = new File(fileLocation);
                File destinationFile = new File(outputFolderLocation + File.separator + sourceFile.getName());
                FileUtils.copy(sourceFile, destinationFile);
                subscriber.onNext(destinationFile);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    private boolean isValidImage(String filename) {
        return isValidFile(filename.toLowerCase(), IMAGE_EXTENSIONS);
    }

    @SuppressLint("NewApi")
    public static String getPath(Context context, Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        String[] projection = {"_data"};
        Cursor cursor = null;

        try {
            cursor = context.getContentResolver().query(uri, projection, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow("_data");
            if (cursor.moveToFirst()) {
                return cursor.getString(column_index);
            }
        } finally {
            ResourceUtils.close(cursor);
        }
        return null;
    }

    public void uploadVideo(final Context context, final Uri uri, final String outputFolderLocation, Observer<File> observer) {
        Observable.create(new Observable.OnSubscribe<File>() {
            @Override
            public void call(Subscriber<? super File> subscriber) {
                String fileLocation = getPath(context, uri);
                Log.d(TAG, "fileLocation: " + fileLocation);
                if (!isValidVideo(fileLocation)) {
                    throw new IllegalArgumentException("Not a valid video file. Please select a 3gp or mp4 file");
                }
                FileUtils.mkdir(outputFolderLocation);
                File sourceFile = new File(fileLocation);
                File destinationFile = new File(outputFolderLocation + File.separator + sourceFile.getName());
                FileUtils.copy(sourceFile, destinationFile);
                subscriber.onNext(destinationFile);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    private boolean isValidVideo(String filename) {
        return isValidFile(filename.toLowerCase(), VIDEO_EXTENSIONS);
    }

    private boolean isValidFile(String filename, String[] supportedFileExtensions) {
        String name = filename.toLowerCase();
        for (String supportedFileExtension : supportedFileExtensions) {
            if (name.endsWith(supportedFileExtension)) {
                return true;
            }
        }
        return false;
    }
}
