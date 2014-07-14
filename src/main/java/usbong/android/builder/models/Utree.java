package usbong.android.builder.models;

import android.provider.BaseColumns;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Rocky Camacho on 6/26/2014.
 */
@Table(name = "Utrees", id = BaseColumns._ID)
public class Utree extends Model implements BaseColumns, Serializable {

    @Column(name = "Name")
    public String name;

    public List<Screen> screens() {
        return getMany(Screen.class, "Utree");
    }

    public static Screen getStartScreen(Utree tree) {
        return new Select().from(Screen.class)
                .where("Utree = ? AND IsStart = ?", tree.getId(), 1)
                .executeSingle();
    }
}
