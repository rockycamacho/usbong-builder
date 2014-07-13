package usbong.android.builder.controllers;

import com.activeandroid.query.Select;
import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import usbong.android.builder.events.OnNeedRefreshTrees;
import usbong.android.builder.models.Utree;
import usbong.android.builder.parsers.UtreeParser;
import usbong.android.builder.utils.FileUtils;

import java.io.File;
import java.util.List;

/**
 * Created by Rocky Camacho on 6/26/2014.
 */
public class UtreeListController implements Controller {

    public static final String TAG = UtreeListController.class.getSimpleName();
    public static final String UTREE_FILE_EXTENSION = ".utree";
    public static final String XML_FILE_EXTENSION = ".xml";

    public void fetchUtrees(Observer<List<Utree>> observer) {
        getUtrees().observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public Observable<List<Utree>> getUtrees() {
        return Observable.create(new Observable.OnSubscribe<List<Utree>>() {
            @Override
            public void call(Subscriber<? super List<Utree>> subscriber) {
                List<Utree> utrees = new Select().from(Utree.class).execute();
                subscriber.onNext(utrees);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io());
    }

    public void importTree(final String fileLocation, final String outputFolderLocation, Observer<String> observer) {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                if (fileLocation.endsWith(XML_FILE_EXTENSION)) {
                    String treeName = fileLocation.substring(fileLocation.lastIndexOf("/") + 1, fileLocation.lastIndexOf(XML_FILE_EXTENSION));
                    parseTreeDetails(fileLocation, outputFolderLocation + File.separator + treeName);
                    subscriber.onNext(fileLocation);
                    subscriber.onCompleted();
                } else if (fileLocation.endsWith(UTREE_FILE_EXTENSION)) {
                    FileUtils.unzip(fileLocation, outputFolderLocation);
                    String treeName = fileLocation.substring(fileLocation.lastIndexOf("/") + 1, fileLocation.lastIndexOf(UTREE_FILE_EXTENSION));
                    String xmlFilePath = outputFolderLocation + File.separator + treeName + File.separator + treeName + XML_FILE_EXTENSION;
                    parseTreeDetails(xmlFilePath, outputFolderLocation + File.separator + treeName);
                    subscriber.onNext(xmlFilePath);
                    subscriber.onCompleted();
                } else {
                    throw new IllegalArgumentException("Unable to read file. Make sure to select a valid *.utree or *.xml file");
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    private void parseTreeDetails(String xmlPath, String utreeOutputFolder) {
        UtreeParser parser = UtreeParser.getInstance();
        parser.parseAndSave(xmlPath, utreeOutputFolder);
    }
}
