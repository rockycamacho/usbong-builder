package usbong.android.builder.controllers;

import com.activeandroid.query.Select;
import usbong.android.builder.models.Screen;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import java.util.List;

/**
 * Created by Rocky Camacho on 7/2/2014.
 */
public class SelectScreenController implements Controller {
    public void fetchScreens(Observer<List<Screen>> observer) {
        getScreens().observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer);
    }

    private Observable<List<Screen>> getScreens() {
        return Observable.create(new Observable.OnSubscribe<List<Screen>>() {
            @Override
            public void call(Subscriber<? super List<Screen>> subscriber) {
                List<Screen> screens = new Select().from(Screen.class).execute();
                subscriber.onNext(screens);
                subscriber.onCompleted();
            }
        })
        .subscribeOn(Schedulers.io());
    }
}
