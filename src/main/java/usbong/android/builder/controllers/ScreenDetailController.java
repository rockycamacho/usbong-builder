package usbong.android.builder.controllers;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import usbong.android.builder.models.Screen;
import usbong.android.builder.models.ScreenRelation;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import java.util.List;

/**
 * Created by Rocky Camacho on 7/2/2014.
 */
public class ScreenDetailController implements Controller {

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
}
