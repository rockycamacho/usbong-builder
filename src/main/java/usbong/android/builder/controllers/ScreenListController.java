package usbong.android.builder.controllers;

import android.util.Log;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Update;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import usbong.android.builder.models.Screen;
import usbong.android.builder.models.ScreenRelation;

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

    public void deleteScreen(final Screen screen, Observer<Object> observer) {
        Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                new Delete().from(ScreenRelation.class)
                        .where("parent = ? OR child = ?", screen.getId())
                        .execute();
                new Delete().from(Screen.class)
                        .where(Screen._ID + " = ?", screen.getId())
                        .execute();
                subscriber.onNext(null);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
}
