package usbong.android.builder.controllers;

import android.content.Context;
import android.util.Log;
import android.view.View;
import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import rx.functions.Action1;
import usbong.android.builder.models.Screen;
import usbong.android.builder.models.ScreenRelation;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import usbong.android.builder.utils.FileUtils;
import usbong.android.builder.utils.ScreenUtils;

import java.io.File;
import java.util.List;

/**
 * Created by Rocky Camacho on 7/2/2014.
 */
public class ScreenDetailController implements Controller {

    private static final String TAG = ScreenDetailController.class.getSimpleName();

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
                    for(int i = 0; i < screenRelations.size(); i++) {
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

    public void saveScreen(final Screen currentScreen, final View screenContainer, Observer<Screen> observer) {
        Observable.create(new Observable.OnSubscribe<Screen>() {
            @Override
            public void call(Subscriber<? super Screen> subscriber) {
                Log.d(TAG, "saving...");
                if(currentScreen.isStart == 1) {
                    new Update(Screen.class).set("IsStart = ?", 0)
                            .where("Utree = ?", currentScreen.utree.getId())
                            .execute();
                }
                else if(Screen.getScreens(currentScreen.utree.getId()).size() == 0) {
                    currentScreen.isStart = 1;
                }
                //TODO: save parent and children
                currentScreen.save();
                Log.d(TAG, "saved");
                File screenshotFile = screenContainer.getContext().getFileStreamPath(currentScreen.getScreenshotPath());
                ScreenUtils.saveScreenshot(screenshotFile, screenContainer);
                subscriber.onNext(currentScreen);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void uploadImage(final String fileLocation, final String outputFolderLocation, Observer<File> observer) {
        Observable.create(new Observable.OnSubscribe<File>() {
            @Override
            public void call(Subscriber<? super File> subscriber) {
                if(!isValidImage(fileLocation)) {
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
        return filename.toLowerCase().endsWith(".jpg") ||
                filename.toLowerCase().endsWith(".png") ||
                filename.toLowerCase().endsWith(".jpeg");
    }
}
