package usbong.android.builder.models;

import android.provider.BaseColumns;
import android.util.Log;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.From;
import com.activeandroid.query.Select;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Rocky Camacho on 6/26/2014.
 */
@Table(name = "Screens", id = BaseColumns._ID)
public class Screen extends Model implements BaseColumns, Serializable {

    public static final String TAG = Screen.class.getSimpleName();

    @Column(name = "Name")
    public String name;

    @Column(name = "Utree")
    public Utree utree;

    @Column(name = "ScreenType")
    public String screenType;

    @Column(name = "Details")
    public String details;

    public static List<Screen> getScreens(long treeId) {
        return new Select().from(Screen.class)
                .where("Utree = ?", treeId)
                .execute();
    };

    public static List<Screen> getParentsOf(long screenId) {
        return new Select().from(Screen.class)
                .as("screen")
                .innerJoin(ScreenRelation.class)
                .as("relation")
                .on("screen." + Screen._ID + " = " + "relation.parent")
                .where("relation.child = ?", screenId)
                .execute();
    }

    public static List<Screen> getChildrenOf(long screenId) {
        From where = new Select().from(Screen.class)
                .as("screen")
                .innerJoin(ScreenRelation.class)
                .as("relation")
                .on("screen." + Screen._ID + " = " + "relation.child")
                .where("relation.parent = ?", screenId);
        Log.d(TAG, where.toSql());
        return where
                .execute();
    }

    public String getScreenshotPath() {
        return "screen_" + getId() + ".png";
    }

}
