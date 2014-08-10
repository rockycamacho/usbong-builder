package usbong.android.builder.controllers;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import com.activeandroid.ActiveAndroid;
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

    public void deleteAllChildScreens(final long screenId, Observer<Object> observer) {
        Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                ScreenRelation.deleteAll(screenId);
                subscriber.onNext(null);
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

    public static String getPath(Context context, Uri uri) {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
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
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
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
