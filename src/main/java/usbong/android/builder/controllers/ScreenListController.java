package usbong.android.builder.controllers;

import android.util.Log;
import android.view.View;
import com.activeandroid.query.Update;
import usbong.android.builder.models.Screen;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import usbong.android.builder.utils.ScreenUtils;

import java.io.File;
import java.util.List;

/**
 * Created by Rocky Camacho on 6/26/2014.
 */
public class ScreenListController implements Controller {

    private static final String TAG = ScreenListController.class.getSimpleName();

    public void fetchScreens(long treeId, Observer<List<Screen>> observer) {
        getScreens(treeId).observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    private Observable<List<Screen>> getScreens(final long treeId) {
        return Observable.create(new Observable.OnSubscribe<List<Screen>>() {
            @Override
            public void call(Subscriber<? super List<Screen>> subscriber) {
                List<Screen> screens = Screen.getScreens(treeId);
                subscriber.onNext(screens);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io());
    }



    public void markAsStart(final Screen screen, Observer<Screen> observer) {
        Observable.create(new Observable.OnSubscribe<Screen>() {
            @Override
            public void call(Subscriber<? super Screen> subscriber) {
                Log.d(TAG, "saving...");
                new Update(Screen.class).set("IsStart = ?", 0)
                        .where("Utree = ?", screen.utree.getId())
                        .execute();
                screen.isStart = 1;
                screen.save();
                Log.d(TAG, "saved");
                subscriber.onNext(screen);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
}
