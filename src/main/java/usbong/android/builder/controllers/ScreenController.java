package usbong.android.builder.controllers;

import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import usbong.android.builder.models.Screen;

/**
 * Created by Rocky Camacho on 6/26/2014.
 */
public class ScreenController implements Controller {

    private static final String TAG = ScreenController.class.getSimpleName();

    public void fetchScreen(long id, Observer<Screen> observer) {
        getScreen(id).observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    private Observable<Screen> getScreen(final long id) {
        return Observable.create(new Observable.OnSubscribe<Screen>() {
            @Override
            public void call(Subscriber<? super Screen> subscriber) {
                Screen screen = loadScreen();
                subscriber.onNext(screen);
                subscriber.onCompleted();
            }

            private Screen loadScreen() {
                Screen screen = null;
                if (id == -1) {
                    screen = new Screen();
                } else {
                    screen = new Select().from(Screen.class)
                            .where(Screen._ID + " = ?", id)
                            .executeSingle();
                }
                return screen;
            }
        }).subscribeOn(Schedulers.io());
    }

    public void save(final Screen screen, Observer<Screen> observer) {
        Observable.create(new Observable.OnSubscribe<Screen>() {
            @Override
            public void call(Subscriber<? super Screen> subscriber) {
                if (screen.isStart == 1) {
                    new Update(Screen.class).set("IsStart = ?", 0)
                            .where("Utree = ?", screen.utree.getId())
                            .execute();
                    screen.isStart = 1;
                    screen.save();
                } else if (Screen.getScreens(screen.utree.getId()).size() == 0) {
                    screen.isStart = 1;
                    screen.save();
                }
                else {
                    screen.save();
                }
                subscriber.onNext(screen);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
}
