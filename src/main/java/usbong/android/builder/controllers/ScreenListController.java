package usbong.android.builder.controllers;

import usbong.android.builder.models.Screen;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import java.util.List;

/**
 * Created by Rocky Camacho on 6/26/2014.
 */
public class ScreenListController implements Controller {


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
}
